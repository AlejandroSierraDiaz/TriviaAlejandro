import java.io.*;
import java.util.*;

public class Partida extends Thread {

    private List<ClientHandler> jugadores;
    private List<Pregunta> preguntas;
    static int TIEMPO_RESPUESTA = 15;
    private int idSala;

    public Partida(List<ClientHandler> jugadoresJugando, int idSala) {
        this.jugadores = jugadoresJugando;
        this.idSala = idSala;
        this.preguntas = new ArrayList<>();
    }

    @Override
    public void run() {
        cargarPreguntas(); 
        
        // --- MEJORA 4: Desordenar preguntas ---
        Collections.shuffle(preguntas);

        System.out.println("\n=== SALA " + idSala + " COMIENZA con " + jugadores.size() + " jugador(es) ===");
        enviarATodos("INICIO|La trivia va a comenzar! Estas en la Sala " + idSala);
        esperar(3000);

        for (int i = 0; i < preguntas.size(); i++) {
            jugarPregunta(preguntas.get(i), i + 1);

            if (i < preguntas.size() - 1) {
                esperar(3000);
            }
        }

        String rankingFinal = construirRankingFinal();
        System.out.println("\n[Sala " + idSala + "]" + rankingFinal.replace(";;", "\n"));
        enviarATodos("FIN|" + rankingFinal);

        // --- MEJORA 5: Guardar informe de partida en XML ---
        guardarResultadosXML();

        esperar(2000);
        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).cerrar();
        }
        System.out.println("Sala " + idSala + " finalizada. Informe XML generado.");
    }

    private void cargarPreguntas() {
        try (BufferedReader br = new BufferedReader(new FileReader("preguntas.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 6) {
                    preguntas.add(new Pregunta(datos[0], datos[1], datos[2], datos[3], datos[4], datos[5]));
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar la base de datos de preguntas.txt en Sala " + idSala);
        }
    }

    private void jugarPregunta(Pregunta pregunta, int numPregunta) {
        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).resetRespuesta();
        }

        String msgPregunta = "PREGUNTA|" + numPregunta + "|" + pregunta.texto
                + "|" + pregunta.opcionA + "|" + pregunta.opcionB
                + "|" + pregunta.opcionC + "|" + pregunta.opcionD;

        enviarATodos(msgPregunta);
        System.out.println("[Sala " + idSala + "] Pregunta " + numPregunta + " enviada.");

        int esperaMaxima = TIEMPO_RESPUESTA * 2;
        int iteraciones = 0;

        while (iteraciones < esperaMaxima) {
            
            // --- MEJORA 6: Enviar temporizador de forma regular a los clientes (cada segundo) ---
            if (iteraciones % 2 == 0) {
                int segRestantes = TIEMPO_RESPUESTA - (iteraciones / 2);
                enviarATodos("TIK|" + segRestantes);
            }

            boolean todosRespondieron = true;
            for (int j = 0; j < jugadores.size(); j++) {
                if (!jugadores.get(j).haRespondido()) {
                    todosRespondieron = false;
                    break;
                }
            }
            if (todosRespondieron) {
                break;
            }
            esperar(500);
            iteraciones++;
        }

        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).aceptarRespuesta = false;
        }

        enviarATodos("TIEMPO|Se acabo el tiempo!");

        for (int j = 0; j < jugadores.size(); j++) {
            ClientHandler jugador = jugadores.get(j);
            String resp = jugador.getRespuesta();
            if (resp != null && resp.equalsIgnoreCase(pregunta.correcta)) {
                jugador.puntuacion++;
            }
        }

        enviarATodos("CORRECTA|La respuesta correcta era: " + pregunta.correcta + ") " + pregunta.getTextoOpcion(pregunta.correcta));

        String ranking = construirRanking(numPregunta);
        enviarATodos("RANKING|" + ranking);
    }

    private void guardarResultadosXML() {
        String nombreFichero = "resultado_sala_" + idSala + ".xml";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreFichero))) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<partida id_sala=\"" + idSala + "\">");
            
            List<ClientHandler> ordenados = new ArrayList<>();
            for (int i = 0; i < jugadores.size(); i++) {
                ordenados.add(jugadores.get(i));
            }
            ordenarPorPuntuacion(ordenados);

            writer.println("    <jugadores>");
            for (int i = 0; i < ordenados.size(); i++) {
                ClientHandler j = ordenados.get(i);
                writer.println("        <jugador posicion=\"" + (i+1) + "\">");
                writer.println("            <nick>" + j.nick + "</nick>");
                writer.println("            <puntuacion>" + j.puntuacion + "</puntuacion>");
                writer.println("            <termino_desconectado>" + j.desconectado + "</termino_desconectado>");
                writer.println("        </jugador>");
            }
            writer.println("    </jugadores>");
            writer.println("</partida>");
        } catch (IOException e) {
            System.err.println("Error al guardar XML: " + e.getMessage());
        }
    }

    private void enviarATodos(String mensaje) {
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).enviarMensaje(mensaje);
        }
    }

    private String construirRanking(int numPregunta) {
        List<ClientHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            if(!jugadores.get(i).desconectado) { 
                ordenados.add(jugadores.get(i));
            }
        }
        ordenarPorPuntuacion(ordenados);

        String resultado = "--- Ranking tras pregunta " + numPregunta + " ---";
        for (int i = 0; i < ordenados.size(); i++) {
            resultado += ";;" + (i + 1) + ". " + ordenados.get(i).nick + " - " + ordenados.get(i).puntuacion + " punto(s)";
        }
        return resultado;
    }

    private String construirRankingFinal() {
        List<ClientHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            if(!jugadores.get(i).desconectado) {
                ordenados.add(jugadores.get(i));
            }
        }
        ordenarPorPuntuacion(ordenados);

        String resultado = "=== RANKING FINAL ===";
        for (int i = 0; i < ordenados.size(); i++) {
            resultado += ";;" + (i + 1) + ". " + ordenados.get(i).nick + " - " + ordenados.get(i).puntuacion + " punto(s)";
        }
        if(ordenados.size() > 0) {
            resultado += ";;;;Ganador: " + ordenados.get(0).nick + " con " + ordenados.get(0).puntuacion + " punto(s)!";
        }
        return resultado;
    }

    private void ordenarPorPuntuacion(List<ClientHandler> lista) {
        for (int i = 0; i < lista.size() - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < lista.size(); j++) {
                if (lista.get(j).puntuacion > lista.get(maxIdx).puntuacion) {
                    maxIdx = j;
                }
            }
            ClientHandler temp = lista.get(i);
            lista.set(i, lista.get(maxIdx));
            lista.set(maxIdx, temp);
        }
    }

    private void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) { }
    }
}
