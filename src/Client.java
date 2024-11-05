import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

public class Client extends Thread {
    public static final int PORT = 3400;
    public static final String SERVER = "localhost";
    public static PublicKey publicKey;
    private int id;

    public Client(int pId) {
        id = pId;
        loadPublicKey();
    }

    private void loadPublicKey() {
        try {
            publicKey = KeyLoaderUtil.loadPublicKey("publicKey.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(SERVER, PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            ClientProtocol.process(id, stdIn, reader, writer, publicKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
