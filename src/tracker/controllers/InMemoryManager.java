package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class InMemoryManager implements Manager  {

    protected Map<Integer, Task> tasks;
    protected Map<Integer, Subtask> subtasks;
    protected Map<Integer, Epic> epics;
    protected int taskIdCounter;
    protected HistoryManager historyManager;

    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime()
                    .orElse(null), Comparator.nullsLast(LocalDateTime::compareTo)));

    public InMemoryManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.taskIdCounter = 1;
        this.historyManager = historyManager;
    }

    @Override
    public Task addTask(Task newTask) {
        try {
            if (newTask.getStartTime().isPresent()) {
                checkingTheIntersection();
            }
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        taskIdCounter++;
        prioritizedTasks.add(newTask);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        newTask.setDuration(Duration.ofMinutes(newTask.getDuration().toMinutes()));
        newTask.setStartTime(newTask.getStartTime().orElse(LocalDateTime.now()));
        return newTask;
    }

    @Override
    public Epic addEpic(Epic newEpic) {
        try {
            if (newEpic.getStartTime().isPresent()) {
                checkingTheIntersection();
            }
        newEpic.setId(taskIdCounter);
        epics.put(newEpic.getId(), newEpic);
        taskIdCounter++;
        prioritizedTasks.add(newEpic);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        newEpic.setDuration(newEpic.getDuration());
        newEpic.setStartTime(newEpic.getStartTime().orElse(LocalDateTime.now()));

        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask newSubtask, Epic epic) {
        try {
            if (newSubtask.getStartTime().isPresent()) {
                checkingTheIntersection();
            }
        newSubtask.setId(taskIdCounter);
        epic.addSubtask(newSubtask);
        subtasks.put(taskIdCounter, newSubtask);
        taskIdCounter++;
            prioritizedTasks.add(newSubtask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        newSubtask.setDuration(Duration.ofMinutes(newSubtask.getDuration().toMinutes()));
        newSubtask.setStartTime(newSubtask.getStartTime().orElse(LocalDateTime.now()));

        return newSubtask;
    }

     @Override
     public Task updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
     }

     @Override
     public Subtask updateSubtask(Subtask updatedSubtask) {
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        Epic epic = updatedSubtask.getEpic();
        epic.updateStatus();
        return updatedSubtask;

     }

     @Override
    public Epic updateEpic(Epic updatedEpic) {
        epics.put(updatedEpic.getId(), updatedEpic);
        return updatedEpic;
    }

     @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

     @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        epics.remove(epicId);
      }
    }

    @Override
     public void deleteSubtaskById(int subtaskId) {
         Subtask subtask = subtasks.get(subtaskId);
         if (subtask != null) {
             Epic epic = subtask.getEpic();
             if (epic != null) {
                 epic.removeSubtask(subtaskId);
                 epic.updateStatus();
             }
         subtasks.remove(subtaskId);
         }
    }

     @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);

        return subtask;
    }

     @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);

        return epic;
    }

     @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);

        return task;
    }

     @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

     @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

     @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

     @Override
    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>(epic.getSubtasks());

        return  subtasksList;
    }

     @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

     @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.clear();
    }

     @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
        subtasks.clear();
    }

     @Override
    public HistoryManager getHistory() {

        return (HistoryManager) historyManager.getHistory();
    }

     @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    // Метод для проверки пересечения времени выполнения задач
    public void checkingTheIntersection() throws Exception {
        List<Task> list = getPrioritizedTasks().stream()
                .filter(task -> task.getStartTime().isPresent())
                .sorted(comparing(task -> task.getStartTime().orElse(LocalDateTime.MIN)))
                .collect(Collectors.toList());

        for (int i = 1; i < list.size(); i++) {
            LocalDateTime currentTaskEndTime = list.get(i - 1).getEndTime();
            LocalDateTime nextTaskStartTime = list.get(i).getStartTime().orElse(LocalDateTime.MAX);

            if (currentTaskEndTime.isAfter(nextTaskStartTime)) {
                throw new Exception(list.get(i - 1).getName() + " " + currentTaskEndTime +
                        " по времени выполнения пересекается с " + list.get(i).getName() + " " + nextTaskStartTime);
            }
        }
    }
}



