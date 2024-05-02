package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private Map<Integer, Subtask> subtasks;

    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new HashMap<>();
        this.setStatus(Status.NEW);
    }


    public Epic(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, description, Status.NEW,  startTime, duration);
        subtasks = new TreeMap<>();
        this.duration = duration != null ? duration : Duration.ofMinutes(0);

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
        } else if (subtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.NEW)) {
            setStatus(Status.NEW);
        } else {
            boolean allDone = subtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE);
            setStatus(allDone ? Status.DONE : Status.IN_PROGRESS);
        }
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
         return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty()) {
            return LocalDateTime.MAX;
        }

        List<Subtask> subtaskList = subtasks.values()
                .stream()
                .sorted(Comparator.comparing(Subtask::getEndTime).reversed())
                .collect(Collectors.toList());

        endTime = subtaskList.get(0).getEndTime();
        return endTime;
    }

    @Override
    public Duration getDuration() {
        if (subtasks == null || subtasks.isEmpty()) {
            return Duration.ZERO;
        }

        Duration totalDuration = Duration.ZERO;
        for (Subtask subtask : subtasks.values()) {
            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        return totalDuration;
    }

    @Override
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        if (subtasks == null) {
            setStartTime(LocalDateTime.MAX);
            return Optional.of(startTime);
        }

        if (subtasks.isEmpty()) {
            return Optional.ofNullable(startTime);
        }
        List<Subtask> subtaskList = subtasks.values()
                .stream()
                .sorted(Comparator.comparing(task -> task.getStartTime().orElse(null), Comparator.nullsLast(LocalDateTime::compareTo)))
                .collect(Collectors.toList());

        startTime = subtaskList.get(0).getStartTime().get();

        return Optional.of(startTime);
    }
}
