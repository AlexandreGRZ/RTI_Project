import com.hepl.protocol.requests.*;
import com.hepl.protocol.responses.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientProtocolTestApp {
    public static void main(String[] args) throws Exception {
        System.out.println("Socket creation");
        Socket socket = new Socket("127.0.0.1", 8080);
        System.out.println("Getting output stream");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Getting input stream");
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());


        System.out.println("login request creation");
        LoginRequest loginRequest = new LoginRequest("Cyril", "abc123");
        System.out.println("Sending request");
        objectOutputStream.writeObject(loginRequest);
        System.out.println("Waiting reponse...");
        LoginResponse loginResponse = (LoginResponse) objectInputStream.readObject();
        System.out.println("login status : " + loginResponse.isSuccess());

        if (!loginResponse.isSuccess()) {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
            return;
        }

        System.out.println("Sending logout request");
        LogoutRequest logoutRequest = new LogoutRequest();
        objectOutputStream.writeObject(logoutRequest);
        objectOutputStream.flush();

        Thread.sleep(1000);
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
        System.out.println("Closing client");
    }
}
