import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;

public class Server extends Thread {
    public static final int PORT = 3400;

    private static PrivateKey privateKey;
    public static PublicKey publicKey;
    private static HashMap<String, PackageData> packageTable = new HashMap<>();

    static {
        // Inicializar la tabla de paquetes con datos predefinidos
        packageTable.put("cliente1-paquete1", new PackageData("cliente1", "paquete1", PackageStatus.ENOFICINA));
        packageTable.put("cliente2-paquete2", new PackageData("cliente2", "paquete2", PackageStatus.RECOGIDO));
    }

    public Server() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto: " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ServerHandler(clientSocket, privateKey, publicKey).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PackageData getPackageStatus(String clientId, String packageId) {
        String key = clientId + "-" + packageId;
        return packageTable.getOrDefault(key, new PackageData(clientId, packageId, PackageStatus.DESCONOCIDO));
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione una opción:");
        System.out.println("1. Generar y guardar claves RSA");
        System.out.println("2. Iniciar servidor");

        int opcion = scanner.nextInt();
        
        if (opcion == 1) {
            generarYGuardarClavesRSA();
        } else if (opcion == 2) {
            new Server().start();
        } else {
            System.out.println("Opción no válida.");
        }
        scanner.close();
    }

    static void generarYGuardarClavesRSA() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            // Guardar clave privada en un archivo
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("privateKey.ser"))) {
                out.writeObject(privateKey);
            }
            System.out.println("Clave privada guardada en 'privateKey.ser'");

            // Guardar clave pública en un archivo
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("publicKey.ser"))) {
                out.writeObject(publicKey);
            }
            System.out.println("Clave pública guardada en 'publicKey.ser'");
            
            System.out.println("Pares de llaves RSA generados y guardados en archivos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class PackageData {
        String clientId;
        String packageId;
        int status;

        public PackageData(String clientId, String packageId, int status) {
            this.clientId = clientId;
            this.packageId = packageId;
            this.status = status;
        }
    }
}
