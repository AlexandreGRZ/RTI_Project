package com.hepl.serverhttp;

import com.hepl.serverhttp.handlers.*;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class ServerTLSApp {
    private static final String RESOURCES_PATH = System.getProperty("user.dir") + "\\ServerHttp\\src\\main\\resources";
    private static final String MDP_KEYSTORE = "keystoremdp";
    private static final String MDP_CLE = MDP_KEYSTORE;
    private static final String KEYSTORE_FILEPATH = RESOURCES_PATH + "\\keystore\\server_keystore.jks";

    public static void main(String[] args) throws Exception {
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(8080), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(KEYSTORE_FILEPATH), MDP_CLE.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SUNX509");
        keyManagerFactory.init(keyStore, MDP_KEYSTORE.toCharArray());
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

        // HTML Handler
        httpsServer.createContext("/", new HandlerHtml());
        httpsServer.createContext("/index", new HandlerHtml());
        httpsServer.createContext("/index.html", new HandlerHtml());

        // CSS Handler
        httpsServer.createContext("/css", new HandlerCss());
        // JS Handler
        httpsServer.createContext("/js", new HandlerJs());
        // Images Handler
        httpsServer.createContext("/images", new HandlerImages());
        // Ico Handler
        httpsServer.createContext("/favicon.ico", new HandlerIco());

        // API Handlers
        httpsServer.createContext("/getArticle", new API());
        httpsServer.createContext("/update", new API());

        System.out.println("Démarrage serveur...");
        httpsServer.start();
    }
}