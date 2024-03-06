package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history;

    public InMemoryHistoryManager()  {
        this.history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null && !history.contains(task)) {
            history.add(task);
            if (history.size() > 10) {
                history.remove(0);
            }
        }
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
