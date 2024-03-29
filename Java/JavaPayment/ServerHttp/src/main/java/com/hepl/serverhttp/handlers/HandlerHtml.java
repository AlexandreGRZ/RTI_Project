package com.hepl.serverhttp.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static com.hepl.serverhttp.handlers.Util.Erreur404;

public class HandlerHtml implements HttpHandler {

    private final String RESOURCES_PATH = System.getProperty("user.dir") + "\\ServerHttp\\src\\main\\resources";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        System.out.println(uriPath);

        if (uriPath.equals("/") || uriPath.endsWith(".html")) {
            File file = new File(RESOURCES_PATH + "\\html\\index.html");
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi page html");
        } else Erreur404(exchange);
    }
}
