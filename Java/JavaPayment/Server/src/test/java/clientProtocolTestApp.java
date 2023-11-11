import com.hepl.protocol.requests.*;
import com.hepl.protocol.responses.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class clientProtocolTestApp {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("127.0.0.1", 8080);
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


        LoginRequest loginRequest = new LoginRequest("cyril", "abc123");
        objectOutputStream.writeObject(loginRequest);
        LoginResponse loginResponse = (LoginResponse) objectInputStream.readObject();
        System.out.println("login status : " + loginResponse.isSuccess());

        if (!loginResponse.isSuccess()) {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
            return;
        }

        LogoutRequest logoutRequest = new LogoutRequest();
        objectOutputStream.writeObject(logoutRequest);
        objectOutputStream.flush();
        objectOutputStream.close();
        objectInputStream.close();
        socket.close();
    }
}
