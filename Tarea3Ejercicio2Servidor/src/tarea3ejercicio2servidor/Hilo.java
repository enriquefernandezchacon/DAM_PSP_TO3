package tarea3ejercicio2servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eofernandez
 */
public class Hilo extends Thread {

    private final Socket socket; // Socket del cliente
    private boolean flagApagado = true;
    private final int numeroCliente;
    private static final int INTENTOS_MAXIMOS = 3;
    private static final String USER = "enrique";
    private static final String PASS = "1234";
    private int intentos = 0;
    private String respuesta;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public Hilo(Socket socket, int numeroCliente) {
        this.socket = socket;
        this.numeroCliente = numeroCliente;
        try {
            asignarRecursos();
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void asignarRecursos() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        msjSinRetorno("BIENVENIDO CLIENTE " + numeroCliente);
        log("Socket para el cliente " + numeroCliente + "creado");
    }

    @Override
    public void run() {
        try {
            login();
            servidor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void login() throws IOException {
        String usuario, contrasena;
        while (intentos < INTENTOS_MAXIMOS && flagApagado) {
            msjConRetornoSinLinea("Introduzca su nombre de usuario: ");
            usuario = respuesta();
            msjConRetornoSinLinea("Introduzca la contraseña: ");
            contrasena = respuesta();
            if (contrasena.equals(PASS) && usuario.equals(USER)) {
                flagApagado = false;
                msjSinRetornoDobleLinea("Acceso concedido");
            } else {
                msjSinRetornoDobleLinea("Acceso denegado, vuelva a intentarlo");
            }
            intentos++;
        }

        if (intentos == INTENTOS_MAXIMOS && flagApagado) {
            msjSinRetorno("Ha superado el numero maximo de intentos");
            msjCerrarSocket();
            log("El cliente " + numeroCliente + " ha excedido el login");
        } else if (!flagApagado) {
            log("El cliente " + numeroCliente + " se ha logueado con exito");
        }
    }

    private void servidor() throws IOException, InterruptedException {

        while (!flagApagado) {
            System.out.println("Esperando respuesta del cliente " + numeroCliente);
            respuesta = mostrarMenu();
            switch (respuesta) {
                case "ls" -> {
                }
                case "cat" -> {
                }
                case "get" -> {
                }
                case "stop" -> {
                    flagApagado = true;
                    Tarea3Ejercicio2Servidor.flagServidorApagado = true;
                    msjApagarServidor();
                    log("Apagando servidor desde el cliente " + numeroCliente);
                    Tarea3Ejercicio2Servidor.cerrarServidor(false);
                }
                case "time" -> {
                }
                case "exit" -> {
                    flagApagado = true;
                    msjCerrarSocket();
                    log("El cliente " + numeroCliente + " se ha desconectado");
                }
                default ->
                    msjSinRetornoDobleLinea("Comando desconocido, vuelva a intentarlo");
            }
        }

        in.close();
        out.close();
        socket.close();
        System.out.println("Conexion cerrada con el cliente " + numeroCliente);
    }
    
    private String mostrarMenu() throws IOException{
        msjSinRetorno("OPCIONES");
        msjSinRetorno("ls   - Muestra los archivos del directorio");
        msjSinRetorno("cat  - Muesta el contenido de un archivo");
        msjSinRetorno("get  - Descarga un fichero");
        msjSinRetorno("stop - Para el servidor y salir del cliente");
        msjSinRetorno("time - Calculo de tiempo de respuesta del servidor");
        msjSinRetorno("exit - Salir del programa");
        msjConRetornoSinLinea("Elija su opcion: ");
        return respuesta();
    }

    private void msjConRetorno(String mensaje) {
        out.println(mensaje);
    }

    private void msjSinRetorno(String mensaje) {
        out.println(mensaje + "**?");
    }
    
    private void msjSinRetornoDobleLinea(String mensaje) {
        out.println(mensaje + "***");
    }

    private void msjConRetornoSinLinea(String mensaje) {
        out.println(mensaje + "**-");
    }

    private void msjCerrarSocket() {
        out.println("Cerrando conexion con el servidor**+");
    }
    
    private void msjApagarServidor() {
        out.println("Apagando el servidor**+");
    }
    
    private String respuesta() throws IOException {
        return in.readLine();
    }

    private void log(String mensaje) {
        System.out.println(mensaje);
    }
}
