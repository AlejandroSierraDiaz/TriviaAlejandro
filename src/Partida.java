import java.util.*;

public class Partida {

    static Pregunta[] preguntas = {
        new Pregunta("¿Cual es la capital de Francia?", "a) Madrid", "b) Paris", "c) Roma", "d) Berlin", "b"),
        new Pregunta("¿Cuantos planetas tiene el sistema solar?", "a) 7", "b) 9", "c) 8", "d) 10", "c"),
        new Pregunta("¿En que anio llego el hombre a la Luna?", "a) 1965", "b) 1969", "c) 1972", "d) 1960", "b"),
        new Pregunta("¿Cual es el oceano mas grande del mundo?", "a) Atlantico", "b) Indico", "c) Artico", "d) Pacifico", "d"),
        new Pregunta("¿Que lenguaje de programacion creo James Gosling?", "a) Python", "b) C++", "c) Java", "d) JavaScript", "c")
    };

    static int TIEMPO_RESPUESTA = 15;

    public static void iniciar(List<ClientHandler> jugadores) {
        System.out.println("\n=== COMIENZA LA TRIVIA con " + jugadores.size() + " jugador(es) ===");
        enviarATodos(jugadores, "INICIO|La trivia va a comenzar! " + jugadores.size() + " jugador(es) conectados.");
        esperar(3000);

        for (int i = 0; i < preguntas.length; i++) {
            jugarPregunta(jugadores, preguntas[i], i + 1);

            if (i < preguntas.length - 1) {
                esperar(3000);
            }
        }

        // Ranking final
        String rankingFinal = construirRankingFinal(jugadores);
        System.out.println("\n" + rankingFinal.replace(";;", "\n"));
        enviarATodos(jugadores, "FIN|" + rankingFinal);

        // Cerrar conexiones
        esperar(2000);
        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).cerrar();
        }
        System.out.println("Partida finalizada.");
    }

    public static void jugarPregunta(List<ClientHandler> jugadores, Pregunta pregunta, int numPregunta) {
        // Resetear respuestas
        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).resetRespuesta();
        }

        // Construir mensaje de pregunta
        String msgPregunta = "PREGUNTA|" + numPregunta + "|" + pregunta.texto
                + "|" + pregunta.opcionA + "|" + pregunta.opcionB
                + "|" + pregunta.opcionC + "|" + pregunta.opcionD;

        enviarATodos(jugadores, msgPregunta);
        System.out.println("\nPregunta " + numPregunta + ": " + pregunta.texto);
        System.out.println("Esperando respuestas... (" + TIEMPO_RESPUESTA + " segundos)");

        // Esperar respuestas con contador (cada iteracion = 500ms)
        int esperaMaxima = TIEMPO_RESPUESTA * 2;
        int iteraciones = 0;

        while (iteraciones < esperaMaxima) {
            boolean todosRespondieron = true;
            for (int j = 0; j < jugadores.size(); j++) {
                if (!jugadores.get(j).haRespondido()) {
                    todosRespondieron = false;
                    break;
                }
            }
            if (todosRespondieron) {
                System.out.println("Todos los jugadores han respondido.");
                break;
            }
            esperar(500);
            iteraciones++;
        }

        // Cerrar recepcion de respuestas
        for (int j = 0; j < jugadores.size(); j++) {
            jugadores.get(j).aceptarRespuesta = false;
        }

        enviarATodos(jugadores, "TIEMPO|Se acabo el tiempo!");

        // Evaluar respuestas
        for (int j = 0; j < jugadores.size(); j++) {
            ClientHandler jugador = jugadores.get(j);
            String resp = jugador.getRespuesta();
            if (resp != null && resp.equalsIgnoreCase(pregunta.correcta)) {
                jugador.puntuacion++;
            }
        }

        // Enviar respuesta correcta
        enviarATodos(jugadores, "CORRECTA|La respuesta correcta era: " + pregunta.correcta + ") " + pregunta.getTextoOpcion(pregunta.correcta));

        // Construir y enviar ranking
        String ranking = construirRanking(jugadores, numPregunta);
        System.out.println(ranking.replace(";;", "\n"));
        enviarATodos(jugadores, "RANKING|" + ranking);
    }

    public static void enviarATodos(List<ClientHandler> jugadores, String mensaje) {
        for (int i = 0; i < jugadores.size(); i++) {
            jugadores.get(i).enviarMensaje(mensaje);
        }
    }

    public static String construirRanking(List<ClientHandler> jugadores, int numPregunta) {
        List<ClientHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            ordenados.add(jugadores.get(i));
        }
        ordenarPorPuntuacion(ordenados);

        String resultado = "--- Ranking tras pregunta " + numPregunta + " ---";
        for (int i = 0; i < ordenados.size(); i++) {
            resultado += ";;" + (i + 1) + ". " + ordenados.get(i).nick + " - " + ordenados.get(i).puntuacion + " punto(s)";
        }
        return resultado;
    }

    public static String construirRankingFinal(List<ClientHandler> jugadores) {
        List<ClientHandler> ordenados = new ArrayList<>();
        for (int i = 0; i < jugadores.size(); i++) {
            ordenados.add(jugadores.get(i));
        }
        ordenarPorPuntuacion(ordenados);

        String resultado = "=== RANKING FINAL ===";
        for (int i = 0; i < ordenados.size(); i++) {
            resultado += ";;" + (i + 1) + ". " + ordenados.get(i).nick + " - " + ordenados.get(i).puntuacion + " punto(s)";
        }
        resultado += ";;;;Ganador: " + ordenados.get(0).nick + " con " + ordenados.get(0).puntuacion + " punto(s)!";
        return resultado;
    }

    public static void ordenarPorPuntuacion(List<ClientHandler> lista) {
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

    public static void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            // Interrupcion ignorada
        }
    }
}
