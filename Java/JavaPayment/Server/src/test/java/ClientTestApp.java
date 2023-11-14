import java.net.Socket;

public class ClientTestApp {
    public static void main(String[] args) throws Exception {
        Socket[] socket = new Socket[10];

        for (Socket s :
                socket) {
            s = new Socket("127.0.0.1", 8080);
        }


        Thread.sleep(1000);

        for (Socket s :
                socket) {
            if (s != null) {

                s.close();
            }
        }
    }
}
