import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        // Iniciar el servidor en un hilo separado para mostrar el menú
        Server server = new Server(10); // Limite inicial de conexiones concurrentes
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Esperar hasta que el servidor esté listo para aceptar conexiones
        System.out.println("Esperando a que el servidor esté listo para aceptar conexiones...");
        serverThread.join(); // Esto asegura que el menú del servidor haya sido ejecutado

        // Solicitar el número de delegados después de que el servidor esté listo
        Scanner scanner = new Scanner(System.in);
        System.out.print("Por favor, ingresa la cantidad de delegados deseados: ");
        int delegados = scanner.nextInt();

        // Iniciar los clientes
        System.out.println("Iniciando clientes...");
        for (int i = 0; i < delegados; i++) {
            Client client = new Client(i);
            client.start();
        }

        scanner.close();
    }
}
