import java.io.*;
import java.net.*;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("=== CLIENTE TRIVIA ===");

        try {
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Introduce la IP del servidor (localhost para local): ");
            String ip = teclado.readLine();
            System.out.print("Introduce tu nick: ");
            String nick = teclado.readLine();

            Socket socket = new Socket(ip, 1234);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Enviar nick al servidor
            out.println(nick);
            System.out.println("Conectado al servidor como: " + nick);

            // Hilo para leer mensajes del servidor
            LectorServidor lectorServidor = new LectorServidor(in);
            lectorServidor.start();

            // Bucle principal: leer respuestas del teclado y enviarlas
            String linea;
            while ((linea = teclado.readLine()) != null) {
                out.println("RESPUESTA|" + linea);

                // Comprobar si el hilo lector ha terminado (juego acabado)
                if (!lectorServidor.isAlive()) {
                    break;
                }
            }

            socket.close();

        } catch (IOException e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }
}
