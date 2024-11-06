import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        // Iniciar el servidor en un hilo separado
        Server server = new Server(10); // Limite inicial de conexiones concurrentes
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Menú para opciones iniciales
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSeleccione una opción:");
        System.out.println("1. Generar pareja de llaves RSA y guardarlas en archivos");
        System.out.println("2. Iniciar servidor y aceptar conexiones de clientes");

        int option = scanner.nextInt();
        if (option == 1) {
            Server.generateAndSaveKeys();
            System.out.println("Llaves RSA generadas y guardadas en archivos.");
        }

        // Esperar hasta que el servidor esté listo para aceptar conexiones
        System.out.println("Servidor escuchando en el puerto 3400...");

        // Solicitar el número de delegados después de que el servidor esté listo
        System.out.print("Por favor, ingresa la cantidad de delegados deseados: ");
        int delegados = scanner.nextInt();

        // Iniciar los clientes en el mismo hilo principal
        System.out.println("Iniciando clientes...");
        for (int i = 0; i < delegados; i++) {
            Client client = new Client(i);
            client.start(); // Ejecuta cada cliente en un hilo separado
        }

        // Cerrar el escáner
        scanner.close();
    }
}
