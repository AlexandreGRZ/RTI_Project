package com.hepl.serverhttp.handlers;

import com.hepl.serverhttp.db.DbConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class API implements HttpHandler {

    private final DbConnection dbConnection;

    public API() throws SQLException, IOException {
        dbConnection = new DbConnection();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("api");
        if (exchange.getRequestMethod().equals("GET") && exchange.getRequestURI().getPath().equals("/getArticle"))
            getArticleEndpoints(exchange);
        else if (exchange.getRequestMethod().equals("POST") && exchange.getRequestURI().getPath().equals("/update"))
            updateEndpoints(exchange);
        else Erreur404(exchange);
    }

    private void updateEndpoints(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseFormData(exchange.getRequestURI().getQuery());
        if (params.containsKey("id") && params.containsKey("price") && params.containsKey("quantity")) {
            System.out.println("oui");
            try {
                dbConnection.update(Integer.parseInt(params.get("id")),
                        Float.parseFloat(params.get("price")),
                        Integer.parseInt(params.get("quantity")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            sendResponse(exchange, 200, "oui");
        } else sendResponse(exchange, 400, "non");
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        System.out.println("Envoi de la réponse (" + status + ") : --" + response + "--");
        exchange.sendResponseHeaders(status, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> parameters = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    private void getArticleEndpoints(HttpExchange exchange) {
        System.out.println("GET request articles");
        try {
            String articles = dbConnection.getArticles();
            sendJsonResponse(exchange, articles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        exchange.sendResponseHeaders(200, json.length());
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(json.getBytes());
        outputStream.close();
        System.out.println("Envoi articles format json");
    }

    private void Erreur404(HttpExchange exchange) throws IOException {
        String response = "requête incorrecte";
        exchange.sendResponseHeaders(404, response.length());
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        System.out.println("KO");
    }
}
