package tarea3ejercicio2cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import tarea3ejercicio2cliente.utils.Entrada;

/**
 *
 * @author eofernandez
 */
public class Tarea3Ejercicio2Cliente {

    private static boolean conectado = true;
    private static String respuesta = "";
    private static String envio;


    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 5577);  
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                while (conectado) {
                    respuesta = in.readLine();
                    if (respuesta.endsWith("***")) {
                        System.out.println("\n" + respuesta.substring(0, respuesta.length() - 3));
                    } else if (respuesta.endsWith("**?")) {
                        System.out.println(respuesta.substring(0, respuesta.length() - 3));
                    } else if (respuesta.endsWith("**-")) {
                        System.out.print(respuesta.substring(0, respuesta.length() - 3));
                        envio = Entrada.cadena();
                        out.println(envio);
                    } else if (respuesta.endsWith("**+")) {
                        System.out.println("\n" + respuesta.substring(0, respuesta.length() - 3));
                        conectado = false;
                        out.println("");
                    } else {
                        System.out.println(respuesta);
                        envio = Entrada.cadena();
                        out.println(envio);
                    }
                    
                }
                System.err.println("\nFin de la sesion");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
