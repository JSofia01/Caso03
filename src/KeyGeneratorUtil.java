import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyGeneratorUtil {

    public static void generateAndSaveKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Guardar la clave pública en un archivo .txt
        try (FileWriter writer = new FileWriter("publicKey.txt")) {
            writer.write(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        }

        // Guardar la clave privada en un archivo .txt
        try (FileWriter writer = new FileWriter("privateKey.txt")) {
            writer.write(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        }

        System.out.println("Claves generadas y guardadas en archivos .txt.");
    }
}
