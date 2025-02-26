package tracker.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.adapters.DurationAdapter;
import tracker.adapters.LocalDateTimeAdapter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class Handler implements HttpHandler {
    enum Methods {
        POST,
        GET,
        DELETE;
    }

    protected  final Charset charset = StandardCharsets.UTF_8;

    protected Gson gson = new GsonBuilder()

            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    protected void writeResponse(HttpExchange exchange,
                                 String responseString,
                                 int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(charset);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }


    abstract  void handleGetRequest(HttpExchange httpExchange) throws IOException;

    abstract  void handlePostRequest(HttpExchange httpExchange);

    abstract  void handleDeleteRequest(HttpExchange httpExchange) throws IOException;


}

