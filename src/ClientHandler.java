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
    public boolean desconectado; // <--- MEJORA 2: Control de caídas

    public ClientHandler(Socket socket) {
        this.cliente = socket;
        this.puntuacion = 0;
        this.respuesta = null;
        this.respondido = false;
        this.aceptarRespuesta = false;
        this.desconectado = false;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            out = new PrintWriter(cliente.getOutputStream(), true);

            nick = in.readLine();
            if(nick == null) throw new IOException("Nick nulo"); // Desconexion prematura

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

            // Si sale del while, el cliente cerro la conexion
            marcarDesconectado();

        } catch (IOException e) {
            marcarDesconectado();
            System.out.println("Jugador " + nick + " desconectado abruptamente.");
        }
    }

    private void marcarDesconectado() {
        this.desconectado = true;
        this.respondido = true; // Auto-responder para no bloquear el bucle de la partida
    }

    public void enviarMensaje(String mensaje) {
        if (out != null && !desconectado) {
            out.println(mensaje);
        }
    }

    public void resetRespuesta() {
        if(!desconectado) {
            respuesta = null;
            respondido = false;
            aceptarRespuesta = true;
        }
    }

    public boolean haRespondido() {
        return respondido;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void cerrar() {
        try {
            if (cliente != null && !cliente.isClosed()) {
                cliente.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar socket de " + nick);
        }
    }
}
