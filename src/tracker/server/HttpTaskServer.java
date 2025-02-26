package tracker.server;

import com.sun.net.httpserver.HttpServer;
import tracker.controllers.Manager;
import tracker.handlers.EpicHandler;
import tracker.handlers.SubtaskHandler;
import tracker.handlers.TaskHandler;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;
    private static final int PORT = 8070;


    public HttpTaskServer(Manager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks/task", new TaskHandler(manager));
        server.createContext("/tasks/subtask", new SubtaskHandler(manager));
        server.createContext("/tasks/epic", new EpicHandler(manager));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

}
