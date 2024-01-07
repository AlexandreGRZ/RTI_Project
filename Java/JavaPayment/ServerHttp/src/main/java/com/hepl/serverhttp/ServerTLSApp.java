package com.hepl.serverhttp;

import com.hepl.serverhttp.handlers.API;
import com.hepl.serverhttp.handlers.HandlerHtml;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class ServerTLSApp {
    private static final String RESOURCES_PATH = System.getProperty("user.dir") + "\\ServerHttp\\src\\main\\resources";
    private static final String MDP_KEYSTORE = "serveurwebcyril";
    private static final String MDP_CLE = MDP_KEYSTORE;
    private static final String KEYSTORE_FILEPATH = RESOURCES_PATH + "\\keystore\\serveurwebcomplementreseau.jks";

    public static void main(String[] args) throws Exception {
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(8080), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(KEYSTORE_FILEPATH), MDP_CLE.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SUNX509");
        keyManagerFactory.init(keyStore, MDP_KEYSTORE.toCharArray());
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

        httpsServer.createContext("/", new HandlerHtml(true));
        httpsServer.createContext("/index", new HandlerHtml(true));
        httpsServer.createContext("/index.html", new HandlerHtml(true));

        httpsServer.createContext("/getArticle", new API());
        httpsServer.createContext("/update", new API());

        System.out.println("Démarrage serveur...");
        httpsServer.start();
    }
}