package tracker.model;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


public class Task {
    private String name;
    private String description;
    public int id;
    private Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration != null ? duration : Duration.ofMinutes(0);
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        if (status == null) {
            setStatus(Status.NEW);
        }
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

     @Override
     public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
     }

     @Override
     public int hashCode() {
        return Objects.hash(id);
     }

     public TaskType getType() {
        return TaskType.TASK;
     }

    public Optional<LocalDateTime> getStartTime() {
        if (startTime == null) {
            setStartTime(LocalDateTime.MAX);
            return Optional.ofNullable(startTime);
        }
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        if (duration == null) {
            setDuration(Duration.ofMinutes(0));
            return duration;
        }
        return duration;
    }


    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {

        return startTime.plus(duration);
    }
 }
