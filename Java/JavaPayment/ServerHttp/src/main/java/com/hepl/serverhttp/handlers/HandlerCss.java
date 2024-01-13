package com.hepl.serverhttp.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static com.hepl.serverhttp.handlers.Util.Erreur404;

public class HandlerCss implements HttpHandler {
    private final String RESOURCES_PATH = System.getProperty("user.dir") + "\\ServerHttp\\src\\main\\resources";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        if (uriPath.endsWith(".css")) {
            File file = new File(RESOURCES_PATH + uriPath.replace("/", "\\"));
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "text/css");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi page css");
        } else Erreur404(exchange);
    }
}
