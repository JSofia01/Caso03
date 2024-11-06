import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 3400;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String clientId = "cliente1";
            String packageId = "paquete1";
            String consult = clientId + "-" + packageId;

            byte[] iv = new byte[16];
            byte[] K_AB1 = new byte[16];
            byte[] K_AB2 = new byte[16];
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec keySpec = new SecretKeySpec(K_AB1, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encryptedConsult = cipher.doFinal(consult.getBytes());
            out.println(Base64.getEncoder().encodeToString(encryptedConsult));

            Mac mac = Mac.getInstance("HmacSHA384");
            mac.init(new SecretKeySpec(K_AB2, "HmacSHA384"));
            byte[] hmac = mac.doFinal(consult.getBytes());
            out.println(Base64.getEncoder().encodeToString(hmac));

            String encryptedResponse = in.readLine();
            byte[] decryptedResponse = cipher.doFinal(Base64.getDecoder().decode(encryptedResponse));
            String estado = new String(decryptedResponse);

            System.out.println("Estado del paquete: " + estado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
