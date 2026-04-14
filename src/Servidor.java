import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {

    static List<ClientHandler> salaEspera = new ArrayList<>();
    static int contadorSalas = 1;
    static int PUERTO = 1234;

    public static void main(String[] args) {
        System.out.println("=== SERVIDOR TRIVIA MULTISALA ===");
        System.out.println("Servidor iniciado en el puerto " + PUERTO);
        System.out.println("Esperando jugadores para la sala en cola...");
        System.out.println("Escribe START para iniciar una partida y atender a la siguiente tanda.");
        System.out.println("Escribe EXIT para cerrar el servidor.");

        try {
            ServerSocket server = new ServerSocket(PUERTO);

            LectorTeclado lector = new LectorTeclado();
            lector.start();

            // Bucle principal infinito: El servidor NUNCA para de aceptar jugadores (Mejora 3)
            while (true) {
                try {
                    Socket cliente = server.accept();
                    ClientHandler handler = new ClientHandler(cliente);
                    handler.start();
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) { }
                    
                    salaEspera.add(handler);
                    System.out.println("Jugador conectado: " + handler.nick + " (En cola: " + salaEspera.size() + ")");
                    handler.enviarMensaje("BIENVENIDO|Bienvenido " + handler.nick + ". Esperando a que el admin empiece la Sala " + contadorSalas + "...");
                } catch (IOException e) {
                    System.err.println("Error aceptando cliente: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error critico en el servidor: " + e.getMessage());
        }
    }

    public static synchronized void iniciarSala() {
        if (salaEspera.size() == 0) {
            System.out.println("No hay jugadores en espera. No se puede iniciar una sala vacia.");
            return;
        }

        System.out.println("=> Iniciando Sala " + contadorSalas + " con " + salaEspera.size() + " jugadores.");
        
        // Copiamos la lista de espera al hilo y limpiamos la de verdad para aceptar más gente
        List<ClientHandler> jugadoresSala = new ArrayList<>(salaEspera);
        salaEspera.clear();

        Partida partida = new Partida(jugadoresSala, contadorSalas);
        partida.start(); // El hilo secundario corre toda la lógica, no frena el Servidor

        contadorSalas++;
        System.out.println("=> Ahora el servidor esta llenando la lista de la Sala " + contadorSalas + ". Siguen pudiendo entrar jugadores nuevos.");
    }
}
