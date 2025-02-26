package tracker.server;

import tracker.exceptions.ManagerSaveException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String url;
    private String token;

    public KVTaskClient(String url) {

        this.url = url;
        token = registerAPIToken(url);

    }

    public String registerAPIToken(String url) {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(url + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                token = response.body();
            } else {
                throw new ManagerSaveException("Ошибка при регистрации API токена. Код состояния:" + response.statusCode());
            }

        } catch (InterruptedException | IOException e) {
            throw new ManagerSaveException("Ошибка регистрации API токена " + e.getMessage());
        }

        return token;

    }

    public void put(String key, String value) throws ManagerSaveException {

        HttpClient client = HttpClient.newHttpClient();

        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + token);

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value, DEFAULT_CHARSET);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());

            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Сохранение закончилось неудачно. Причина: " + e.getMessage());
        }

    }

    public String load(String key) {

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + token);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Загрузка закончилась неудачно. Причина: " + e.getMessage());
        }

        return response != null ? response.body() : "null";

    }

}
