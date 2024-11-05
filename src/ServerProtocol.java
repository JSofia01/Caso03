import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ServerProtocol {
    public static void process(int id, PrintWriter pOut, BufferedReader pIn, PrivateKey privateKey, PublicKey publicKey) 
    throws Exception {
        // Ejemplo de autenticación y solicitud de estado
        String userId = pIn.readLine();
        String packageId = pIn.readLine();

        int status = Server.getPackageStatus(userId, packageId);
        String statusMessage = PackageStatus.getStatus(status);

        pOut.println(statusMessage);
    }
}
