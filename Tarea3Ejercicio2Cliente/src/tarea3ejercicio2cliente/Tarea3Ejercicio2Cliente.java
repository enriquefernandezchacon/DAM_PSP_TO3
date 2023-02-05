package tarea3ejercicio2cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    private static final String IP = "192.168.1.132";
    private static final int PUERTO = 5577;

    public static void main(String[] args) {
        try (
                 Socket socket = new Socket(IP, PUERTO);  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
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
                } else if (respuesta.endsWith("*-time-*")) {
                    out.println("");
                } else if (respuesta.endsWith("+-+")) {
                    getArchivo(socket);
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

    private static void getArchivo(Socket socket) throws IOException {
        DataInputStream din = new DataInputStream(socket.getInputStream());
        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
        //dout.writeUTF(IP);
        try (FileOutputStream archivo = new FileOutputStream(din.readUTF())) {
            byte[] buffer = new byte[1024]; // Recibirá el contenido del archivo en bloques de 4096 bytes.
            int leidos;

            // recibimos el tamaño del archivo en bytes.
            long tamano = din.readLong();

            // Recibimos el contenido del archivo en bloques de 4096 bytes y lo guardamos en el archivo creado.
            while ((leidos = din.read(buffer)) <= tamano) {

                archivo.write(buffer, 0, leidos);

                if (din.readUTF().equals("FIN")) {
                    System.out.println("Archivo recibido");
                    break;
                } else if (din.readUTF().equals("ERROR")) {
                    System.out.println("Error en la descarga del archivo");
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
