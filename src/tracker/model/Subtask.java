package tracker.model;

public class Subtask extends Task {
    public Epic epic;
    public Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
    }
    public Epic getEpic() {
        return epic;
    }
    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}