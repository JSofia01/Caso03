import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerHandler extends Thread {
    private Socket clientSocket;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public ServerHandler(Socket clientSocket, PrivateKey privateKey, PublicKey publicKey) {
        this.clientSocket = clientSocket;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            ServerProtocol.process(writer, reader, privateKey, publicKey);

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
