package tracker.handlers;

import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.Manager;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Subtask;
import java.io.IOException;

public class SubtaskHandler extends Handler {

    private Manager manager;


    public SubtaskHandler(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Соединение установлено!");

        Methods methods = Methods.valueOf(httpExchange.getRequestMethod());
        try {

            switch (methods) {
                case GET:
                    handleGetRequest(httpExchange);
                    break;
                case POST:
                    handlePostRequest(httpExchange);
                    break;
                case DELETE:
                    handleDeleteRequest(httpExchange);
                    break;
                default:
                    httpExchange.sendResponseHeaders(405,0);
            }
        } catch (JsonParseException | IOException exp) {
            System.out.println(exp.getMessage());
        }
    }

    @Override
    public void handleGetRequest(HttpExchange exchange) throws IllegalArgumentException, IOException {

        if (exchange.getRequestURI().getQuery() == null) {
            try {
                writeResponse(exchange, gson.toJson(manager.getSubtasks()), 200);
            } catch (RuntimeException | IOException exp) {
                throw new ManagerSaveException("Ошибка при получении подзадач ", exp);
            }
        } else {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1].trim());
            if (!manager.getSubtasks().contains(id)) {
                writeResponse(exchange, "Подзадача с id = " + id + "отсутсвует", 204);
            } else {
                writeResponse(exchange, gson.toJson(manager.getSubtaskById(id)), 200);
            }
        }
    }

    @Override
    public void handlePostRequest(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), charset);

            Subtask subtask = gson.fromJson(body, Subtask.class);
            System.out.println(subtask.getId());
            if (!manager.getSubtasks().contains(subtask.getId())) {
                manager.addSubtask(subtask,subtask.getEpic());
                writeResponse(exchange, "Подзадача " + subtask.getId() + " создана.\n" + body, 200);
            } else {
                manager.updateTask(subtask);
                writeResponse(exchange, "Подзадача " + subtask.getId() + " обновлена.\n" + body, 200);
            }
        } catch (RuntimeException | IOException exp) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных", exp);
        }
    }


    @Override

    public void handleDeleteRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            try {
                manager.deleteAllSubtasks();
                writeResponse(exchange, "Все задачи успешно удалены", 200);
            } catch (IllegalArgumentException | IOException exp) {
                System.out.println(exp.getMessage());
            }
        } else {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1].trim());
            if (manager.getSubtasks().contains(id)) {
                manager.deleteSubtaskById(id);
                writeResponse(exchange, "Подзадача с id = " + id + " успешно удалена", 200);
            } else {
                writeResponse(exchange, "Подзадача с id = " + id + " отсутсвтует", 204);
            }
        }
    }
}