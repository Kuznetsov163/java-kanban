import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks;
    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new HashMap<>();
        this.setStatus(Status.NEW);
    }
    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }
    public void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
        updateStatus();
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
    private void updateStatus() {
        boolean allDone = subtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
        setStatus(allDone ? Status.DONE : Status.IN_PROGRESS);
    }
}