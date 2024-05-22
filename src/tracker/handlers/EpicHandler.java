package tracker.handlers;


import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.Manager;
import tracker.exceptions.ManagerSaveException;
import tracker.model.Epic;

import java.io.IOException;

public class EpicHandler extends Handler {
    private Manager manager;

    public EpicHandler(Manager manager) {
        this.manager = manager;
    }


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
                writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
            } catch (RuntimeException | IOException exp) {
                throw new ManagerSaveException("Ошибка при получении эпиков ", exp);
            }
        } else {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1].trim());
            if (!manager.getSubtasks().contains(id)) {
                writeResponse(exchange, "Эпик с id = " + id + "отсутсвует", 204);
            } else {
                writeResponse(exchange, gson.toJson(manager.getEpicById(id)), 200);
            }
        }
    }

    @Override
    public void handlePostRequest(HttpExchange exchange) {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), charset);

            Epic epic = gson.fromJson(body, Epic.class);
            System.out.println(epic.getId());
            if (!manager.getEpics().contains(epic.getId())) {
                manager.addEpic(epic);
                writeResponse(exchange, "Эпик " + epic.getId() + " создан.\n" + body, 200);
            } else {
                manager.updateEpic(epic);
                writeResponse(exchange, "Эпик " + epic.getId() + " обновлен.\n" + body, 200);
            }
        } catch (RuntimeException | IOException exp) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных", exp);
        }
    }

    @Override

    public void handleDeleteRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            try {
                manager.deleteAllEpics();
                writeResponse(exchange, "Все эпики успешно удалены", 200);
            } catch (IllegalArgumentException | IOException exp) {
                System.out.println(exp.getMessage());
            }
        } else {
            String[] query = exchange.getRequestURI().getQuery().split("=");
            int id = Integer.parseInt(query[1].trim());
            if (manager.getEpics().contains(id)) {
                manager.deleteEpicById(id);
                writeResponse(exchange, "Эпик с id = " + id + " успешно удалена", 200);
            } else {
                writeResponse(exchange, "Эпик с id = " + id + " отсутсвтует", 204);
            }
        }
    }

}