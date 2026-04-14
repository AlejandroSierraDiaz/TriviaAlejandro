import java.io.*;

public class LectorTeclado extends Thread {

    @Override
    public void run() {
        try {
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String linea;
            while ((linea = teclado.readLine()) != null) {
                if (linea.equalsIgnoreCase("START")) {
                    Servidor.iniciarSala();
                } else if (linea.equalsIgnoreCase("EXIT")) {
                    System.out.println("Apagando Servidor...");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo teclado");
        }
    }
}
