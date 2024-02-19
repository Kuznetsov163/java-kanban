package tracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


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

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }


    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
        } else {
            boolean allDone = subtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
            setStatus(allDone ? Status.DONE : Status.IN_PROGRESS);
        }
    }
}
