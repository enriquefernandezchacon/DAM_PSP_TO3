package tarea3ejercicio1servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author Enrique
 */
public class Hilo extends Thread {

    private final Socket socket; // Socket del cliente
    private final int numeroSecreto; // Número secreto del servidor
    private static boolean flagApagado = false;
    private int numero;

    public Hilo(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
        this.numeroSecreto = new Random().nextInt(100) + 1; // Genera un número aleatorio entre 1 y 100
    }

    @Override
    public void run() {
        try {
            // Obtiene los flujos de entrada y salida del socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (!flagApagado) {
                System.out.println("A la espera de datos desde el cliente " + numero);
                // Recibe el número enviado por el cliente
                int number = Integer.parseInt(in.readLine());

                // Si se ha recibido el código de parada, termina el bucle while
                if (number == -777) {
                    flagApagado = true;
                    Tarea3Ejercicio1Servidor.flagApagado = true;
                    out.println("SERVERCLOSED");
                    Tarea3Ejercicio1Servidor.cerrarServidor();
                    break;
                // Compara el número recibido con el número secreto y envía la respuesta al cliente
                } else if (number < numeroSecreto) {
                    out.println("MENOR");
                } else if (number > numeroSecreto) {
                    out.println("MAYOR");
                } else if (number == numeroSecreto) {
                    out.println("ACERTADO");
                    break;
                }
            }

            out.println("SOCKETCLOSED");
            in.close();
            out.close();
            socket.close();
            System.out.println("Conexion cerrada con el cliente " + numero);
        } catch (IOException e) {
        }
    }
}
