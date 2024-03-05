package tracker.controllers;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
public class InMemoryManager implements Manager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int taskIdCounter;
    private List<Task> history;


    public InMemoryManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.taskIdCounter = 1;
        this.history = new ArrayList<>();
    }
    @Override
    public Task addTask(Task newTask) {
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        taskIdCounter++;
        return newTask;
    }
    @Override
    public Epic addEpic(Epic newEpic) {
        newEpic.setId(taskIdCounter);
        epics.put(newEpic.getId(), newEpic);
        taskIdCounter++;
        return newEpic;
    }
    @Override
    public Subtask addSubtask(Subtask newSubtask, Epic epic) {
        newSubtask.setId(taskIdCounter);
        epic.addSubtask(newSubtask);
        subtasks.put(taskIdCounter, newSubtask);
        taskIdCounter++;
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
                subtasks.remove(subtask.getId());}
        epics.remove(epicId);
    }}

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

    }}
    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            updateHistory(subtask.getEpic());
        }
        return subtask;
    }
    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            updateHistory(epic);
        }
        return epic;
    }
    @Override
    public Task getTaskById( int taskId) {
        Task task = tasks.get( taskId);
        if (task != null) {
            updateHistory(task);
        }
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
    public void setStatusForSubtask(Subtask subtask, Status status) {
        subtask.setStatus(status);
    }
    @Override
    public void updateEpicStatus(Epic epic) {
        epic.updateStatus();
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
    public List<Task> getHistory() {
        int startIndex = Math.max(0, history.size() - 10);
        return new ArrayList<>(history.subList(startIndex, history.size()));
    }

    private void updateHistory(Task task) {
        if (task != null && !history.contains(task)) {
            history.add(task);
            if (history.size() > 10) {
                history.remove(0);
            }
        }
    }
}



