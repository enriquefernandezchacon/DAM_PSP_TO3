package tarea3ejercicio2servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eofernandez
 */
public class Hilo extends Thread {

    private final Socket socket; // Socket del cliente
    private final int numeroSecreto; // Número secreto del servidor
    private static boolean flagApagado = false;
    private final int numero;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public Hilo(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
        this.numeroSecreto = new Random().nextInt(100) + 1; // Genera un número aleatorio entre 1 y 100
        try {
            asignarRecursos();
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void asignarRecursos() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println("BIENVENIDO CLIENTE " + numero);
    }

    @Override
    public void run() {
        try {
            login();
            servidor();
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void login() {

    }

    private void servidor() throws IOException, InterruptedException {

        while (!flagApagado) {
            System.out.println("Esperando respuesta del cliente " + numero);
            String respuesta = in.readLine();
            System.out.println("Procesando respuesta del cliente " + numero);
            Thread.sleep(1000);
            switch (respuesta) {
                case "ls" -> {
                }
                case "cat" -> {
                }
                case "get" -> {
                }
                case "stop" -> {
                }
                case "time" -> {
                }
                case "exit" -> {
                    flagApagado = true;
                    Tarea3Ejercicio2Servidor.flagServidorApagado = true;
                    out.println("SERVERCLOSED");
                    Tarea3Ejercicio2Servidor.cerrarServidor();
                }
                default ->
                    out.println("¿?");
            }
        }
        out.println("SOCKETCLOSED");

        in.close();
        out.close();
        socket.close();
        System.out.println("Conexion cerrada con el cliente " + numero);

    }
}
