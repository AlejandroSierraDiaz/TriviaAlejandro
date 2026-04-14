import java.io.*;

public class LectorTeclado extends Thread {

    @Override
    public void run() {
        try {
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String linea;
            while ((linea = teclado.readLine()) != null) {
                if (linea.equalsIgnoreCase("START")) {
                    if (Servidor.jugadores.size() == 0) {
                        System.out.println("No hay jugadores conectados. Esperando...");
                    } else {
                        Servidor.partidaIniciada = true;
                        try {
                            Servidor.server.close();
                        } catch (IOException e) {
                            System.err.println("Error al cerrar servidor");
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo teclado");
        }
    }
}
