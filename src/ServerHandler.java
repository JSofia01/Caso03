// Código modificado para manejo de cliente con intercambio de claves Diffie-Hellman
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

    @Override
    public void run() {
        try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            DiffieHellman dh = new DiffieHellman();
            BigInteger serverDH = dh.calcularmodp(BigInteger.valueOf(id));
            writer.println(serverDH.toString());

            BigInteger clientDH = new BigInteger(reader.readLine());
            BigInteger sharedSecret = dh.calcularz(clientDH, BigInteger.valueOf(id));

            byte[] sharedSecretHash = ServerProtocol.hashSHA512(sharedSecret.toByteArray());
            SecretKey aesKey = new SecretKeySpec(sharedSecretHash, 0, 16, "AES");
            SecretKey hmacKey = new SecretKeySpec(sharedSecretHash, 16, 16, "HmacSHA384");

            ServerProtocol.process(id, writer, reader, aesKey, hmacKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
