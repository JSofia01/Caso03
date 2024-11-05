import java.io.BufferedReader;
import java.io.PrintWriter;
import java.security.PublicKey;

public class ClientProtocol {
    public static void process(int id, BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut, PublicKey publicKey) 
    throws Exception {
        System.out.print("Ingrese el ID de usuario: ");
        String userId = stdIn.readLine();
        System.out.print("Ingrese el ID de paquete: ");
        String packageId = stdIn.readLine();

        pOut.println(userId);
        pOut.println(packageId);

        String status = pIn.readLine();
        System.out.println("Estado del paquete: " + status);
    }
}
