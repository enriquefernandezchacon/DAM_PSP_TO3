package tarea3ejercicio2servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eofernandez
 */
public class Tarea3Ejercicio2Servidor {

    private static final int PUERTO = 5577;
    private static int contadorClientes = 0;
    public static boolean flagServidorApagado = false;
    private static ServerSocket servidor = null;
    
    public static void main(String[] args) {
        try {
            // Crear un objeto de la clase ServerSocket para escuchar conexiones entrantes
            servidor = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado");

            // Bucle infinito para aceptar conexiones de clientes
            while (!flagServidorApagado) {
                // Aceptar una conexión entrante
                Socket cliente = servidor.accept();
                cliente.setSoLinger(true, 10);
                contadorClientes++;
                System.out.println("Cliente " + contadorClientes + " conectado desde " + cliente.getInetAddress().getHostAddress());

                // Crear un objeto de la clase ClientHandler para atender al cliente
                Hilo clientHandler = new Hilo(cliente, contadorClientes);
                // Iniciar el hilo de ejecución del ClientHandler
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error al aceptar conexion entrante: " + e.getMessage());
        } finally {
            cerrarServidor(true);
        }
    }
    
    public static void cerrarServidor(Boolean error) {
        try {
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Tarea3Ejercicio2Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (error) {
            System.err.println("Servidor apagado por error interno");
        } else {
            System.out.println("Servidor apagado");
        }
        // cerrar la conexión de todos los hilos hijo lanzados desde el main()
        System.exit(0);
    }
    
}
