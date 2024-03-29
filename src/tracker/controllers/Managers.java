package tracker.controllers;

public class Managers {
    public static Manager getDefault() {
        return new InMemoryManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
