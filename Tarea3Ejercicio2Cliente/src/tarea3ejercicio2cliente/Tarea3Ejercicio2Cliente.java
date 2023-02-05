package tarea3ejercicio2cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import tarea3ejercicio2cliente.utils.Entrada;

/**
 *
 * @author eofernandez
 */
public class Tarea3Ejercicio2Cliente {

    private static final String DIRECTORIO_DESCARGAS = "descargas/";
    private static final String IP = "192.168.1.132";
    private static final int PUERTO = 5577;

    private static boolean conectado = true;
    private static String respuesta = "";
    private static String envio;

    private static DataInputStream din;
    private static DataOutputStream dout;

    public static void main(String[] args) {
        try (
                //Se crea un socket con la información de conexión
                Socket socket = new Socket(IP, PUERTO);) {
            //Se instancia los objetos para la entrada y salida de datos
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            //Se inicia el bucle de escucha con un flag como condicion
            while (conectado) {
                respuesta = din.readUTF();
                //Los if-else ejecutan las distintas posibilidades
                if (respuesta.endsWith("***")) {
                    System.out.println("\n" + respuesta.substring(0, respuesta.length() - 3));
                } else if (respuesta.endsWith("**?")) {
                    System.out.println(respuesta.substring(0, respuesta.length() - 3));
                } else if (respuesta.endsWith("**-")) {
                    System.out.print(respuesta.substring(0, respuesta.length() - 3));
                    envio = Entrada.cadena();
                    dout.writeUTF(envio);
                } else if (respuesta.endsWith("**+")) {
                    System.out.println("\n" + respuesta.substring(0, respuesta.length() - 3));
                    conectado = false;
                    dout.writeUTF("");
                } else if (respuesta.endsWith("*-time-*")) {
                    dout.writeUTF("");
                } else if (respuesta.endsWith("+-+")) {
                    getArchivo();
                } else {
                    System.out.println(respuesta);
                    envio = Entrada.cadena();
                    dout.writeUTF(envio);
                }

            }
            System.err.println("\nFin de la sesion");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    //Este método procesa los datos que llegan para realizar correctamente la descarga del archhivo
    private static void getArchivo() throws IOException {
        //Se crea la ruta donde se guardara el archivo
        String archivoACrear = DIRECTORIO_DESCARGAS + din.readUTF();
        //Si el archivo ya existe, se borrara para sobreescribirlo
        if (Files.deleteIfExists(Paths.get(archivoACrear))) {
            System.out.println("Se ha encontrado un archivo con el mismo nombre");
            System.out.println("Procediendo a sobreescribirlo");
        }
        //Se inicia el proceso
        try ( FileOutputStream fOutStream = new FileOutputStream(archivoACrear)) {
            //Se designa el tamaño del buffer
            byte[] buffer = new byte[1024];
            int leidos;
            //Se recibie el tamaño del archivo en bytes.
            long tamano = din.readLong();
            //Se recibe el archivo por bloques.
            while ((leidos = din.read(buffer)) <= tamano) {
                fOutStream.write(buffer, 0, leidos);

                String resp = din.readUTF();
                //Esta condicion es necesaria para sanber cuando debe acabar el bucle
                if (resp.equals("FIN")) {
                    System.out.println("Archivo recibido");
                    break;
                } else if (resp.equals("ERROR")) {
                    System.out.println("Error en la descarga del archivo");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
