import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class ServerProtocol {
    
    // Método para calcular el hash SHA-512
    public static byte[] hashSHA512(byte[] input) throws Exception {
        MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
        return sha512.digest(input);
    }

    public static void process(int id, PrintWriter pOut, BufferedReader pIn, SecretKey aesKey, SecretKey hmacKey) 
    throws Exception {
        // Recibir mensaje cifrado y HMAC del cliente
        String encryptedMessage = pIn.readLine();
        String hmacReceived = pIn.readLine();

        // Decodificar los valores recibidos
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] hmacBytes = Base64.getDecoder().decode(hmacReceived);
        
        // Verificar el HMAC recibido
        Mac hmac = Mac.getInstance("HmacSHA384");
        hmac.init(hmacKey);
        byte[] expectedHmac = hmac.doFinal(encryptedBytes);

        if (!MessageDigest.isEqual(expectedHmac, hmacBytes)) {
            pOut.println("Error: HMAC no coincide");
            return;
        }

        // Desencriptar el mensaje recibido
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // IV de 16 bytes
        cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        // Procesar el mensaje desencriptado
        String[] messageParts = new String(decryptedBytes).split(":");
        String userId = messageParts[0];
        String packageId = messageParts[1];

        // Obtener estado del paquete
        int status = Server.getPackageStatus(userId, packageId);
        String statusMessage = PackageStatus.getStatus(status);

        // Cifrar el estado para la respuesta
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
        byte[] encryptedResponse = cipher.doFinal(statusMessage.getBytes());

        // Generar HMAC para la respuesta cifrada
        hmac.init(hmacKey);
        byte[] responseHmac = hmac.doFinal(encryptedResponse);

        // Enviar respuesta cifrada y HMAC al cliente
        pOut.println(Base64.getEncoder().encodeToString(encryptedResponse));
        pOut.println(Base64.getEncoder().encodeToString(responseHmac));
    }
}
