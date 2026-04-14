public class Pregunta {

    String texto;
    String opcionA;
    String opcionB;
    String opcionC;
    String opcionD;
    String correcta;

    public Pregunta(String texto, String opcionA, String opcionB, String opcionC, String opcionD, String correcta) {
        this.texto = texto;
        this.opcionA = opcionA;
        this.opcionB = opcionB;
        this.opcionC = opcionC;
        this.opcionD = opcionD;
        this.correcta = correcta;
    }

    public String getTextoOpcion(String letra) {
        switch (letra) {
            case "a": return opcionA.substring(3);
            case "b": return opcionB.substring(3);
            case "c": return opcionC.substring(3);
            case "d": return opcionD.substring(3);
            default: return "";
        }
    }
}
