package tracker.controllers;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
public class Manager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subtasks;
    private Map<Integer, Epic> epics;
    private int taskIdCounter;


    public Manager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.taskIdCounter = 1;
    }
    public Task addTask(Task newTask) {
        newTask.setId(taskIdCounter);
        tasks.put(taskIdCounter, newTask);
        taskIdCounter++;
        return newTask;
    }
    public Subtask addSubtask(Subtask newSubtask, Epic epic) {
        newSubtask.setId(taskIdCounter);
        epic.addSubtask(newSubtask);
        tasks.put(taskIdCounter, newSubtask);
        taskIdCounter++;
        return newSubtask;
    }
    public Task updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void setStatusForSubtask(Subtask subtask, Status status) {
        subtask.setStatus(status);
    }
    public void updateEpicStatus(Epic epic) {
        epic.updateStatus();
    }

    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>(epic.getSubtasks());
        return  subtasksList;
    }
}