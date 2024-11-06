import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PublicKey;

public class Client extends Thread {
    public static final int PUERTO = 3400;
    public static final String SERVIDOR = "localhost";
    public static PublicKey publicKey;
    private int id;

    public Client(int pId) {
        id = pId;
        loadPublicKey();
    }

    private void loadPublicKey() {
        try (ObjectInputStream publicKeyIn = new ObjectInputStream(new FileInputStream("publicKey.ser"))) {
            publicKey = (PublicKey) publicKeyIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(SERVIDOR, PUERTO);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            ClientProtocol.process(id, stdIn, reader, writer, publicKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
