import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerHandler extends Thread {
    private Socket clientSocket = null;
    private int id;

    public ServerHandler(Socket pSocket, int pId) {
        this.clientSocket = pSocket;
        this.id = pId;
    }

    @Override
    public void run() {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrivateKey privateKey = Server.getPrivateKey();
            PublicKey publicKey = Server.getPublicKey();

            ServerProtocol.process(id, writer, reader, privateKey, publicKey);

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
