package tarea3ejercicio1servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enrique
 */
public class Tarea3Ejercicio1Servidor {

    private static final int PUERTO = 3500;
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
                //Se acepta la conexión entrante
                Socket cliente = servidor.accept();
                //Informacion de la conexion
                System.out.println("Conexion aceptada desde " + cliente.getInetAddress().getHostAddress());
                //Se crea un objeto que gestiona todo el proceso
                Hilo hilo = new Hilo(cliente, ++contadorClientes);
                //Se inicia el hilo de ejecución
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("Error al aceptar conexion entrante: " + e.getMessage());
        } finally {
            //Cuando el flag cambia y termina el bucle de escucha, se procede a apagar el servidor
            cerrarServidor();
        }
    }

    public static void cerrarServidor() {
        try {
            servidor.close();
        } catch (IOException ex) {
            Logger.getLogger(Tarea3Ejercicio1Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Servidor apagado desde el cliente");
        //Cierra la conexión de todos los hilos hijo lanzados desde el main
        System.exit(0);
    }
}

