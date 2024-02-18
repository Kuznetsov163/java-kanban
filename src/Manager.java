import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
public class Manager {
    private Map<Integer, Task> tasks;
    private int taskIdCounter;
    public Manager() {
        this.tasks = new HashMap<>();
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
    public Map<Integer, Task> getAllTasks() {
        return tasks;
    }
    public void setStatusForSubtask(Subtask subtask, Status status) {
        subtask.setStatus(status);
    }
    public void updateEpicStatus(Epic epic) {
        boolean allDone = epic.getSubtasks().values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        epic.setStatus(allDone ? Status.DONE : Status.IN_PROGRESS);
    }
    public List<Subtask> getAllSubtasksForEpic(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>(epic.getSubtasks().values());
        return subtasksList;
    }
}