package tracker.controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateTimeAdapter;
import tracker.server.KVTaskClient;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient client;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManager(String url) {
        super(new InMemoryHistoryManager(), null);
        client = new KVTaskClient(url);
    }


    public void load() {
        client.load("tasks");
        client.load("epics");
        client.load("subtasks");
        client.load("history");
    }

    @Override
    public void save() {
        client.put("tasks", gson.toJson(tasks));
        client.put("epics", gson.toJson(epics));
        client.put("subtasks", gson.toJson(subtasks));
        client.put("history", gson.toJson(getHistory()));
    }
}
