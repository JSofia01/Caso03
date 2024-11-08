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
import java.util.Map;

public class Server extends Thread {
    public static final int PORT = 3400;
    private static PrivateKey privateKey;
    public static PublicKey publicKey;
    private ServerSocket serverSocket = null;
    private int maxThreads;
    private int threadsNumber = 0;

    private static final Map<String, Integer> packageStatusMap = new HashMap<>();

    public Server(int maxThreads) throws IOException {
        this.maxThreads = maxThreads;
        System.out.println("Inicio del servidor principal");

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        initializePackages();
    }

    private void initializePackages() {
        packageStatusMap.put("user1-package1", PackageStatus.ENOFICINA);
        packageStatusMap.put("user1-package2", PackageStatus.RECOGIDO);
    }

    public static int getPackageStatus(String userId, String packageId) {
        return packageStatusMap.getOrDefault(userId + "-" + packageId, PackageStatus.DESCONOCIDO);
    }

    public static void generateAndSaveKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            try (ObjectOutputStream publicKeyOut = new ObjectOutputStream(new FileOutputStream("publicKey.ser"));
                 ObjectOutputStream privateKeyOut = new ObjectOutputStream(new FileOutputStream("privateKey.ser"))) {
                publicKeyOut.writeObject(publicKey);
                privateKeyOut.writeObject(privateKey);
            }

            System.out.println("Llaves RSA generadas y guardadas en archivos.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() {
        if (privateKey == null || publicKey == null) {
            System.out.println("Por favor, genere las llaves RSA antes de iniciar el servidor.");
            return;
        }

        System.out.println("Servidor escuchando en el puerto " + PORT);
        while (threadsNumber < maxThreads) {
            try {
                Socket socket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(socket, threadsNumber);
                threadsNumber++;
                serverHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        startServer(); // Llamar directamente al método de inicio sin el menú
    }
}
