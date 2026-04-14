import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

    static List<ClientHandler> jugadores = new ArrayList<>();
    static boolean partidaIniciada = false;
    static ServerSocket server;

    static int PUERTO = 1234;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR TRIVIA ===");
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
        System.out.println("Esperando jugadores... (Maximo 10)");
        System.out.println("Escribe START para comenzar la partida.");

        try {
            server = new ServerSocket(PUERTO);

            // Hilo para leer el teclado del administrador
            LectorTeclado lector = new LectorTeclado();
            lector.start();

            // Fase 1: Aceptar jugadores hasta que el admin escriba START
            while (!partidaIniciada) {
                try {
                    Socket cliente = server.accept();
                    if (jugadores.size() >= 10) {
                        PrintWriter outTemp = new PrintWriter(cliente.getOutputStream(), true);
                        outTemp.println("SERVIDOR_LLENO");
                        cliente.close();
                    } else {
                        ClientHandler handler = new ClientHandler(cliente);
                        handler.start();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // Interrupcion ignorada
                        }
                        jugadores.add(handler);
                        System.out.println("Jugador conectado: " + handler.nick + " (" + jugadores.size() + "/10)");
                        handler.enviarMensaje("BIENVENIDO|Bienvenido " + handler.nick + ". Esperando a que comience la partida...");
                    }
                } catch (IOException e) {
                    // El ServerSocket fue cerrado por LectorTeclado al escribir START
                    if (partidaIniciada) {
                        break;
                    }
                }
            }

            // Fase 2: Iniciar la partida
            Partida.iniciar(jugadores);

            System.out.println("Servidor cerrado.");

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

        System.exit(0);
    }
}
