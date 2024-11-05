import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server extends Thread {
    public static final int PORT = 3400;
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private ServerSocket serverSocket = null;
    private int maxThreads;
    private int threadsNumber = 0;

    // Estructura para almacenar los estados de los paquetes
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

        // Inicializar algunos paquetes para pruebas
        initializePackages();
    }

    // Inicializar algunos paquetes con datos de prueba
    private void initializePackages() {
        packageStatusMap.put("user1-package1", PackageStatus.ENOFICINA);
        packageStatusMap.put("user1-package2", PackageStatus.RECOGIDO);
        // Agrega más datos de prueba si es necesario
    }

    // Método para obtener el estado de un paquete basado en userId y packageId
    public static int getPackageStatus(String userId, String packageId) {
        String key = userId + "-" + packageId;
        return packageStatusMap.getOrDefault(key, PackageStatus.DESCONOCIDO);
    }

    // Método para acceder a la clave privada
    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    // Método para acceder a la clave pública
    public static PublicKey getPublicKey() {
        return publicKey;
    }

    // Método para cargar llaves desde archivos
    private void loadKeys() {
        try {
            privateKey = KeyLoaderUtil.loadPrivateKey("privateKey.txt");
            publicKey = KeyLoaderUtil.loadPublicKey("publicKey.txt");
            System.out.println("Llaves cargadas desde archivos .txt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para mostrar el menú y ejecutar las opciones seleccionadas
    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Generar pareja de llaves RSA y guardarlas en archivos");
            System.out.println("2. Cargar llaves desde archivos y aceptar conexiones de clientes");
            System.out.println("3. Salir");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    try {
                        KeyGeneratorUtil.generateAndSaveKeys();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    loadKeys();
                    startServer();
                    break;
                case 3:
                    System.out.println("Saliendo del servidor...");
                    exit = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                    break;
            }
        }

        scanner.close();
    }

    // Método para iniciar el servidor y manejar las conexiones de clientes
    private void startServer() {
        if (privateKey == null || publicKey == null) {
            System.out.println("Por favor, cargue las llaves antes de iniciar el servidor.");
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
        showMenu();
    }
}
