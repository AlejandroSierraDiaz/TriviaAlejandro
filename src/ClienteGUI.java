import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClienteGUI extends JFrame {

    private JPanel panelConexion;
    private JPanel panelJuego;

    private JTextField txtIp;
    private JTextField txtNick;

    private JLabel lblPregunta;
    private JButton btnA, btnB, btnC, btnD;
    private JTextArea txtLog;
    private JLabel lblStatus;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LectorServidorGUI lector;

    private Color bgDark = new Color(30, 30, 36);
    private Color bgPanel = new Color(43, 43, 54);
    private Color textLight = new Color(240, 240, 240);
    private Color accentBlue = new Color(77, 136, 255);
    private Color accentGreen = new Color(60, 180, 100);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }

    public ClienteGUI() {
        setTitle("Trivia Live - Cliente");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgDark);
        setLayout(new CardLayout());

        panelConexion = crearPanelConexion();
        panelJuego = crearPanelJuego();

        add(panelConexion, "CONEXION");
        add(panelJuego, "JUEGO");

        mostrarPanel("CONEXION");
    }

    private void mostrarPanel(String nombre) {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), nombre);
    }

    private JPanel crearPanelConexion() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(bgDark);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel titulo = new JLabel("TRIVIA LIVE");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setForeground(accentBlue);
        gbc.gridwidth = 2;
        p.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel lblIp = new JLabel("IP Servidor:");
        lblIp.setForeground(textLight);
        lblIp.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(lblIp, gbc);

        gbc.gridx = 1;
        txtIp = new JTextField("localhost", 15);
        txtIp.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(txtIp, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblNick = new JLabel("Tu Nick:");
        lblNick.setForeground(textLight);
        lblNick.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(lblNick, gbc);

        gbc.gridx = 1;
        txtNick = new JTextField(15);
        txtNick.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(txtNick, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnConectar = new JButton("CONECTAR");
        btnConectar.setBackground(accentGreen);
        btnConectar.setForeground(Color.WHITE);
        btnConectar.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnConectar.setFocusPainted(false);
        btnConectar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConectar.addActionListener(e -> conectarAlServidor());
        p.add(btnConectar, gbc);

        return p;
    }

    private JPanel crearPanelJuego() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(bgDark);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(bgDark);

        lblStatus = new JLabel("Esperando partida...", SwingConstants.CENTER);
        lblStatus.setForeground(accentGreen);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 18));
        panelNorte.add(lblStatus, BorderLayout.NORTH);

        lblPregunta = new JLabel("Conectate para comenzar", SwingConstants.CENTER);
        lblPregunta.setForeground(textLight);
        lblPregunta.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblPregunta.setBorder(new EmptyBorder(20, 0, 20, 0));
        panelNorte.add(lblPregunta, BorderLayout.CENTER);
        p.add(panelNorte, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 15, 15));
        panelBotones.setBackground(bgDark);

        btnA = crearBotonOpcion("A");
        btnB = crearBotonOpcion("B");
        btnC = crearBotonOpcion("C");
        btnD = crearBotonOpcion("D");

        panelBotones.add(btnA);
        panelBotones.add(btnB);
        panelBotones.add(btnC);
        panelBotones.add(btnD);
        p.add(panelBotones, BorderLayout.CENTER);

        txtLog = new JTextArea(8, 20);
        txtLog.setBackground(bgPanel);
        txtLog.setForeground(Color.LIGHT_GRAY);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtLog.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setBorder(BorderFactory.createLineBorder(accentBlue, 1));
        p.add(scroll, BorderLayout.SOUTH);

        return p;
    }

    private JButton crearBotonOpcion(String letra) {
        JButton btn = new JButton("Opcion " + letra);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(bgPanel);
        btn.setForeground(textLight);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setEnabled(false);
        btn.addActionListener(e -> enviarRespuesta(letra.toLowerCase()));
        return btn;
    }

    private void conectarAlServidor() {
        String ip = txtIp.getText().trim();
        String nick = txtNick.getText().trim();

        if (nick.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El Nick no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            socket = new Socket(ip, 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(nick);
            mostrarPanel("JUEGO");
            logMensaje("Conectado como: " + nick);

            lector = new LectorServidorGUI();
            lector.start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarRespuesta(String respuesta) {
        if (out != null) {
            out.println("RESPUESTA|" + respuesta);
            logMensaje("Has enviado la respuesta " + respuesta.toUpperCase() + ". Esperando a los demas...");
            activarBotones(false);
        }
    }

    public void logMensaje(String msj) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(msj + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    public void activarBotones(boolean activo) {
        SwingUtilities.invokeLater(() -> {
            btnA.setEnabled(activo);
            btnB.setEnabled(activo);
            btnC.setEnabled(activo);
            btnD.setEnabled(activo);
        });
    }

    private class LectorServidorGUI extends Thread {
        @Override
        public void run() {
            try {
                String msj;
                while ((msj = in.readLine()) != null) {
                    procesarMensaje(msj);
                }
            } catch (IOException e) {
                logMensaje("Desconectado del servidor.");
            }
        }

        private void procesarMensaje(String msj) {
            SwingUtilities.invokeLater(() -> {
                if (msj.startsWith("BIENVENIDO|")) {
                    lblStatus.setText(msj.substring(11));

                } else if (msj.equals("SERVIDOR_LLENO")) {
                    JOptionPane.showMessageDialog(ClienteGUI.this, "El servidor esta lleno.");
                    System.exit(0);

                } else if (msj.startsWith("INICIO|")) {
                    lblStatus.setText("Partida en curso");
                    logMensaje("\n★★★ " + msj.substring(7) + " ★★★");

                } else if (msj.startsWith("TIK|")) {
                    // MEJORA 1: UI intercepta los tiempos restantes del servidor periodicamente
                    lblStatus.setText("Tiempo restante: " + msj.substring(4) + " s");

                } else if (msj.startsWith("PREGUNTA|")) {
                    String[] partes = msj.split("\\|");
                    if (partes.length >= 7) {
                        lblPregunta.setText("<html><center>" + partes[1] + ". " + partes[2] + "</center></html>");
                        btnA.setText(partes[3]);
                        btnB.setText(partes[4]);
                        btnC.setText(partes[5]);
                        btnD.setText(partes[6]);
                        activarBotones(true);
                        // Reiniciar etiqueta
                        lblStatus.setText("Tiempo restante: Calculando...");
                    }

                } else if (msj.startsWith("TIEMPO|")) {
                    lblStatus.setText(msj.substring(7));
                    activarBotones(false);

                } else if (msj.startsWith("CORRECTA|")) {
                    logMensaje(msj.substring(9));

                } else if (msj.startsWith("RANKING|")) {
                    logMensaje("--- RANKING ---");
                    String[] lineas = msj.substring(8).split(";;");
                    for (String l : lineas) if (!l.isEmpty()) logMensaje(l);

                } else if (msj.startsWith("FIN|")) {
                    logMensaje("\n====== JUEGO FINALIZADO ======");
                    String[] lineas = msj.substring(4).split(";;");
                    for (String l : lineas) if (!l.isEmpty()) logMensaje(l);
                    lblStatus.setText("Partida finalizada. Ya puedes cerrar.");
                    lblPregunta.setText("¡Ver ganador en el panel de texto!");
                    activarBotones(false);
                } else {
                    logMensaje(msj);
                }
            });
        }
    }
}
