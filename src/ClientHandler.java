import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    private Socket cliente;
    BufferedReader in;
    PrintWriter out;
    String nick;
    int puntuacion;
    private String respuesta;
    private boolean respondido;
    boolean aceptarRespuesta;

    public ClientHandler(Socket socket) {
        this.cliente = socket;
        this.puntuacion = 0;
        this.respuesta = null;
        this.respondido = false;
        this.aceptarRespuesta = false;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            out = new PrintWriter(cliente.getOutputStream(), true);

            // El primer mensaje del cliente es su nick
            nick = in.readLine();

            // Bucle de lectura de respuestas
            String linea;
            while ((linea = in.readLine()) != null) {
                if (linea.startsWith("RESPUESTA|") && aceptarRespuesta) {
                    String[] partes = linea.split("\\|");
                    if (partes.length >= 2) {
                        respuesta = partes[1].trim();
                        respondido = true;
                        aceptarRespuesta = false;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Jugador " + nick + " desconectado.");
        }
    }

    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    public void resetRespuesta() {
        respuesta = null;
        respondido = false;
        aceptarRespuesta = true;
    }

    public boolean haRespondido() {
        return respondido;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void cerrar() {
        try {
            if (cliente != null) {
                cliente.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar socket de " + nick);
        }
    }
}
