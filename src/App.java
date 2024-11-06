import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Seleccione una opción:");
        System.out.println("1. Generar claves RSA del servidor y guardarlas en archivos");
        System.out.println("2. Iniciar el servidor y clientes");

        int opcion = scanner.nextInt();
        
        switch (opcion) {
            case 1:
                System.out.println("Generando claves RSA del servidor...");
                Server.generarYGuardarClavesRSA();
                System.out.println("Claves generadas y guardadas en archivos 'publicKey.ser' y 'privateKey.ser'.");
                break;

            case 2:
                System.out.print("Ingrese la cantidad de clientes (delegados) a iniciar: ");
                int delegados = scanner.nextInt();

                new Thread(() -> {
                    try {
                        new Server().start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                for (int i = 0; i < delegados; i++) {
                    new Thread(() -> {
                        try {
                            new Client().main(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                break;

            default:
                System.out.println("Opción no válida.");
                break;
        }
        
        scanner.close();
    }
}
