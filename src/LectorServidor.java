import java.io.*;
import java.net.*;

public class LectorServidor extends Thread {

    private BufferedReader in;

    public LectorServidor(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while ((mensaje = in.readLine()) != null) {

                if (mensaje.startsWith("BIENVENIDO|")) {
                    String texto = mensaje.substring("BIENVENIDO|".length());
                    System.out.println(texto);

                } else if (mensaje.equals("SERVIDOR_LLENO")) {
                    System.out.println("El servidor esta lleno. Intentalo mas tarde.");
                    break;

                } else if (mensaje.startsWith("INICIO|")) {
                    String texto = mensaje.substring("INICIO|".length());
                    System.out.println("\n========================================");
                    System.out.println("  " + texto);
                    System.out.println("========================================");

                } else if (mensaje.startsWith("PREGUNTA|")) {
                    String[] partes = mensaje.split("\\|");
                    if (partes.length >= 7) {
                        System.out.println("\n----------------------------------------");
                        System.out.println("  PREGUNTA " + partes[1]);
                        System.out.println("----------------------------------------");
                        System.out.println(partes[2]);
                        System.out.println("  " + partes[3]);
                        System.out.println("  " + partes[4]);
                        System.out.println("  " + partes[5]);
                        System.out.println("  " + partes[6]);
                        System.out.println("----------------------------------------");
                        System.out.print("Tu respuesta (a/b/c/d): ");
                    }

                } else if (mensaje.startsWith("TIK|")) {
                    // Muestra el tiempo restante solo en múltiplos de 5 para no hacer spam en la consola
                    int seg = Integer.parseInt(mensaje.substring(4));
                    if (seg % 5 == 0 && seg > 0) {
                        System.out.println("   [Quedan " + seg + " segundos...]");
                    }

                } else if (mensaje.startsWith("TIEMPO|")) {
                    String texto = mensaje.substring("TIEMPO|".length());
                    System.out.println("\n** " + texto + " **");

                } else if (mensaje.startsWith("CORRECTA|")) {
                    String texto = mensaje.substring("CORRECTA|".length());
                    System.out.println(texto);

                } else if (mensaje.startsWith("RANKING|")) {
                    String texto = mensaje.substring("RANKING|".length());
                    String[] lineas = texto.split(";;");
                    System.out.println();
                    for (int i = 0; i < lineas.length; i++) {
                        System.out.println(lineas[i]);
                    }

                } else if (mensaje.startsWith("FIN|")) {
                    String texto = mensaje.substring("FIN|".length());
                    String[] lineas = texto.split(";;");
                    System.out.println("\n========================================");
                    for (int i = 0; i < lineas.length; i++) {
                        System.out.println(lineas[i]);
                    }
                    System.out.println("========================================");
                    System.out.println("\nGracias por jugar!");
                    break;

                } else {
                    System.out.println(mensaje);
                }
            }
        } catch (IOException e) {
            System.err.println("Conexion con el servidor perdida.");
        }
    }
}
