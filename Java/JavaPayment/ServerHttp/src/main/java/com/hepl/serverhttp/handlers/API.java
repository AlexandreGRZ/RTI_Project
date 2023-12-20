package com.hepl.serverhttp.handlers;

import com.hepl.serverhttp.db.DbConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class API implements HttpHandler {

    private final DbConnection dbConnection;

    public API() throws SQLException, IOException {
        dbConnection = new DbConnection();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(exchange.getRequestURI().getPath().equals("/getArticle")){
            System.out.println("GET request articles");
            try {
                String articles = dbConnection.getArticles();
                sendJsonResponse(exchange, articles);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else Erreur404(exchange);
    }

    private void sendJsonResponse(HttpExchange exchange, String json)throws IOException{
        exchange.sendResponseHeaders(200, json.length());
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(json.getBytes());
        outputStream.close();
        System.out.println("Envoi articles format json");
    }

    private void Erreur404(HttpExchange exchange)throws IOException {
        String reponse = "requête incorrecte";
        exchange.sendResponseHeaders(404, reponse.length());
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        OutputStream os = exchange.getResponseBody();
        os.write(reponse.getBytes());
        os.close();
        System.out.println("KO");
    }
}
