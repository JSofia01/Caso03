import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerHandler extends Thread {
    private Socket clientSocket = null;
    private int id;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public ServerHandler(Socket pSocket, int pId) {
        this.clientSocket = pSocket;
        this.id = pId;
        loadKeys();
    }

    private void loadKeys() {
        try (ObjectInputStream privateKeyIn = new ObjectInputStream(new FileInputStream("privateKey.ser"));
             ObjectInputStream publicKeyIn = new ObjectInputStream(new FileInputStream("publicKey.ser"))) {
            privateKey = (PrivateKey) privateKeyIn.readObject();
            publicKey = (PublicKey) publicKeyIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setKeys(PrivateKey pPrivate, PublicKey pPublic) {
        privateKey = pPrivate;
        publicKey = pPublic;
    }

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            ServerProtocol.process(id, writer, reader, privateKey, publicKey);

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
