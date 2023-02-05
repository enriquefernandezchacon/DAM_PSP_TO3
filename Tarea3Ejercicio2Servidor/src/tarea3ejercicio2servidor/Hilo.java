package tarea3ejercicio2servidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
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
    private static final String DIRECTORIO = "datos/";
    private int intentos = 0;
    private String respuesta;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private DataInputStream din = null;
    private DataOutputStream dout = null;

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
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());
        msjSinRetorno("BIENVENIDO CLIENTE " + numeroCliente);
        log("Socket para el cliente " + numeroCliente + " creado");
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
                msjSinRetorno("Acceso concedido");
            } else {
                msjSinRetorno("Acceso denegado, vuelva a intentarlo\n");
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
                    listarDirectorio();
                }
                case "cat" -> {
                    mostrarArchivo();
                }
                case "get" -> {
                    enviarArchivo();
                }
                case "stop" -> {
                    flagApagado = true;
                    Tarea3Ejercicio2Servidor.flagServidorApagado = true;
                    msjApagarServidor();
                    log("Apagando servidor desde el cliente " + numeroCliente);
                    Tarea3Ejercicio2Servidor.cerrarServidor(false);
                }
                case "time" -> {
                    calcularTiempoRespuesta();
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

    private void listarDirectorio() {
        msjSinRetorno("");
        File directorio = new File(DIRECTORIO);
        if (directorio.exists()) {
            for (File elemento : directorio.listFiles()) {
                msjSinRetorno(elemento.getName());
            }
        } else {
            msjSinRetorno("ERROR: El servidor no ha podido encontrar el directorio");
        }
    }

    private void mostrarArchivo() throws IOException {
        msjSinRetorno("");
        msjConRetornoSinLinea("Introduzca el normbre del archivo a leer: ");
        String resp = respuesta();
        File archivo = new File(DIRECTORIO + resp);
        if (archivo.exists()) {
            msjSinRetorno("");
            Scanner input = new Scanner(archivo);
            while (input.hasNextLine()) {
                msjSinRetorno(input.nextLine());
            }
        } else {
            msjSinRetorno("ERROR: El servidor no ha podido encontrar el archivo");
        }
    }

    private void enviarArchivo() throws IOException {
        //msjSinRetorno("");
        //msjConRetornoSinLinea("Introduzca el normbre del archivo a descargar: ");
        msjEnvioArhivo();
        dout.writeUTF("1");
        dout.writeUTF("Respuesta: ");
        respuesta = din.readUTF();
        dout.writeUTF(respuesta);
        File archivo = new File(DIRECTORIO + respuesta);
        if (archivo.exists()) {
            msjEnvioArhivo();
            try (FileInputStream fis = new FileInputStream(archivo)) {
                byte[] receivedData = new byte[1024];
                int i;
                
                dout.writeLong(fis.getChannel().size());
                
                while((i = fis.read(receivedData)) != -1) {
                    dout.write(receivedData, 0, i);
                }
                
                dout.flush();
                dout.writeUTF("FIN");
                
            } catch (IOException e) {
                dout.writeUTF("ERROR");
            }
        } else {
            msjSinRetorno("ERROR: El servidor no ha podido encontrar el archivo");
        }
    }

    private void calcularTiempoRespuesta() throws IOException {
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        msjCalculoTiempo();// llamamos a la tarea
        var a = respuesta();
        time_end = System.currentTimeMillis();

        if (a != null) {
            msjSinRetorno("La transmision ha tardado " + (time_end - time_start) + " milisegundos");
        } else {
            msjSinRetorno("ERROR: No se ha podido realizar la prueba");
        }

    }

    private String mostrarMenu() throws IOException {
        msjSinRetornoDobleLinea("OPCIONES");
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

    private void msjCalculoTiempo() {
        out.println("*-time-*");
    }

    private void msjEnvioArhivo() {
        out.println("+-+");
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
