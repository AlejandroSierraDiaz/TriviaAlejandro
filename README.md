# TriviaAlejandro - Servidor de Preguntas y Respuestas 

Un excelente servidor basado 100% en Java puro y Arquitectura Cliente-Servidor mediante **TCP Sockets y Concurrencia (Multihilo)**. El proyecto simula una sala de juegos "Trivia" completamente en vivo, capaz de mantener partidas simultáneas, control de interfaz y temporizadores asíncronos.

---

###  Características Principales (7 Grandes Mejoras)

1. **Frontend Swing (Modo Oscuro):** Interfaz gráfica interactiva y visual atractiva, en `ClienteGUI`, que sustituye los comandos de texto por ventanas estéticas usando puramente componentes nativos de Java (`javax.swing`).
2. **Tic-Tac en Tiempo Real:** El servidor bombardea una señal (`TIK|<ms>`) para que la pantalla de los clientes dibuje una cuenta regresiva que incrementa la tensión del juego.
3. **Múltiples Salas Simultáneas:** Tras el comando de arranque del inicio `START`, el hilo principal se desatasca instantáneamente admitiendo a más nuevos jugadores mientras el hilo hijo `Partida.java (extends Thread)` orquesta una partida paralela en una sala separada.
4. **Base de Datos Desacoplada (TXT):** Todas las preguntas están extraídas y modeladas elegantemente desde el archivo `preguntas.txt`. Modificar el trivial no recaba la obligatoriedad de recompilar.
5. **Autoguardado XML:** Tan pronto un Trivial finaliza, se estructura un sistema de reporte permanente en una etiqueta XML automática exportada al disco (`resultado_sala_X.xml`).
6. **Robustez ante Caídas / Anticuelgues:** Un bloque de _try/catch_ modificado blinda el `ClientHandler` marcando `desconectado = true`. Si alguien tira de su cable de red y muere su socket repentinamente, la red no lo sufrirá esperando respuestas imposibles.
7. **Barajado Inteligente:** Uso magistral de la utilidad `Collections.shuffle` que se entromete antes de lanzar a un grupo a una sala creando infinitas sesiones sin repetir el orden.

---

### 💻 ¿Cómo arrancar el Servidor?

1. Entra a la ruta del directorio usando una consola (`cd src`).
2. Compila todos los ficheros `.java`: 
   ```bash
   javac *.java
   ```
3. Ejecuta el centro de control del servidor: 
   ```bash
   java Servidor
   ```

### 🎮 ¿Cómo comenzar a jugar?

1. Abre múltiples ventanas extras de la terminal (tantos jugadores como quieras ser).
2. Para ejecutar una interfaz molona de escritorio ejecuta:
   ```bash
   java ClienteGUI
   ```
3. Alternativamente, si deseas apostar por lo retro e interactuar desde la propia terminal, arranca:
   ```bash
   java Cliente
   ```
   
> **Nota de Administración:** Una vez estés satisfecho con las personas que se han puesto a rellenar las salas de la sala de espera actual, regresa a tu terminal central (`Servidor`) y grita la palabra mágica `START`. Tus jugadores comenzarán. ¡Puedes repetir este proceso para montar una Sala 2 en paralelo mientras juegas a la Sala 1!
