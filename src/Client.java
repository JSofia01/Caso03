import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

            // Proceso de intercambio de claves Diffie-Hellman
            DiffieHellman dh = new DiffieHellman();
            BigInteger clientDH = dh.calcularmodp(BigInteger.valueOf(id));
            writer.println(clientDH.toString());

            // Recibir clave del servidor
            BigInteger serverDH = new BigInteger(reader.readLine());
            BigInteger sharedSecret = dh.calcularz(serverDH, BigInteger.valueOf(id));

            // Generar claves AES y HMAC
            byte[] sharedSecretHash = ServerProtocol.hashSHA512(sharedSecret.toByteArray());
            SecretKey aesKey = new SecretKeySpec(sharedSecretHash, 0, 16, "AES");
            SecretKey hmacKey = new SecretKeySpec(sharedSecretHash, 16, 16, "HmacSHA384");

            ClientProtocol.process(id, stdIn, reader, writer, aesKey, hmacKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
