package tarea3ejercicio1cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import tarea3ejercicio1cliente.utils.Entrada;

/**
 *
 * @author Enrique
 */
public class Tarea3Ejercicio1Cliente {

    private static boolean conectado = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (
            //Se crea el socket y los elementos de entrada y salida
            Socket socket = new Socket("localhost", 3500);  
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            //Se lee la línea de bienvenida
            System.out.println(in.readLine());
                //Se inicia el bucle que controla el procesa con un flag de condicionante
                while (conectado) {
                    int number;
                    //Se solicita el numero a ingresar
                    do {
                        System.out.println("\nIngrese un numero entre 1 y 100 (ingrese -777 para detener el servidor): ");
                        number = Entrada.entero();
                    } while (number != -777 && (number < 1 || number > 100));
                    //Se envía el número al servidor
                    out.println(number); 
                    //Se recibe la respuesta del servidor
                    String response = in.readLine();
                    //Se procesa la respuesta
                    switch (response) {
                        case "ACERTADO" -> {
                            System.out.println("Has adivinado el numero secreto!");
                            var a = in.readLine();
                            conectado = false; 
                            System.out.println("Conexion cerrada con el servidor.");
                        }
                        case "MAYOR" -> System.out.println("El numero secreto es menor.");
                        case "MENOR" -> System.out.println("El numero secreto es mayor.");
                        case "SOCKETCLOSED" -> {
                            conectado = false; 
                            System.out.println("Conexion cerrada con el servidor.");
                        }
                        case "SERVERCLOSED" -> {
                            conectado = false; 
                            System.out.println("Servidor Apagado.");
                        }
                        default -> {
                            conectado = false; 
                            System.out.println("Conexion cerrada con el servidor.");
                        }
                    }
                }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
