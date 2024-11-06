import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.PublicKey;
import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class ClientProtocol {
    public static void process(int id, BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut, SecretKey aesKey, SecretKey hmacKey) 
    throws Exception {
        System.out.print("Ingrese el ID de usuario: ");
        String userId = stdIn.readLine();
        System.out.print("Ingrese el ID de paquete: ");
        String packageId = stdIn.readLine();

        // Cifrar el mensaje con AES
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // IV de 16 bytes (inicialización simplificada)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
        String message = userId + ":" + packageId;
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());

        // Generar HMAC
        Mac hmac = Mac.getInstance("HmacSHA384");
        hmac.init(hmacKey);
        byte[] hmacValue = hmac.doFinal(encryptedMessage);

        // Enviar el mensaje cifrado y el HMAC
        pOut.println(Base64.getEncoder().encodeToString(encryptedMessage));
        pOut.println(Base64.getEncoder().encodeToString(hmacValue));

        // Recibir respuesta
        String encryptedResponse = pIn.readLine();
        String hmacResponse = pIn.readLine();

        // Validar HMAC de la respuesta
        byte[] responseBytes = Base64.getDecoder().decode(encryptedResponse);
        byte[] receivedHmacBytes = Base64.getDecoder().decode(hmacResponse);
        hmac.init(hmacKey);
        byte[] expectedHmacBytes = hmac.doFinal(responseBytes);

        if (MessageDigest.isEqual(expectedHmacBytes, receivedHmacBytes)) {
            // Desencriptar la respuesta
            cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
            byte[] decryptedResponse = cipher.doFinal(responseBytes);
            String status = new String(decryptedResponse);
            System.out.println("Estado del paquete: " + status);
        } else {
            System.out.println("Error en la consulta: HMAC no coincide.");
        }
    }
}
