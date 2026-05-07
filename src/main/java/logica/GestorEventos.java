package logica;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorEventos extends Thread {
    // Flags de los eventos
    private boolean apagonActivo = false;
    private boolean tormentaActiva = false;
    private boolean elevenActiva = false;
    private boolean redMentalActiva = false;

    private final HawkinsLog log;
    private final Zona colmena;
    private final Zona callePrincipal;
    private final AtomicInteger sangreVecna;
    private final Portal[] portales;

    public GestorEventos(HawkinsLog log, Zona colmena, Zona callePrincipal, AtomicInteger sangreVecna, Portal[] portales ) {
        this.log = log;
        this.colmena = colmena;
        this.callePrincipal = callePrincipal;
        this.sangreVecna = sangreVecna;
        this.portales = portales;
    }

    // Getters sincronizados para que los hilos consulten qué pasa
    public synchronized boolean isApagonActivo() { return apagonActivo; }
    public synchronized boolean isTormentaActiva() { return tormentaActiva; }
    public synchronized boolean isElevenActiva() { return elevenActiva; }
    public synchronized boolean isRedMentalActiva() { return redMentalActiva; }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 1. Tiempo entre eventos: 30 a 60 segundos
                long tiempoEspera = 30000 + (long)(Math.random() * 30000);
                Thread.sleep(tiempoEspera);

                // 2. Elegir un evento al azar (0 a 3)
                int tipoEvento = (int) (Math.random() * 4);
                activarEvento(tipoEvento);

                // 3. Duración del evento: 5 a 10 segundos[cite: 2]
                long duracionEvento = 5000 + (long)(Math.random() * 5000);
                Thread.sleep(duracionEvento);

                // 4. Terminar evento
                desactivarEventos();
            }
        } catch (InterruptedException e) {
            log.escribir("Gestor de Eventos en interrupción.");
        }
    }

    private synchronized void activarEvento(int tipo) {
        switch (tipo) {
            case 0:
                apagonActivo = true;
                log.escribir("APAGÓN DEL LABORATORIO");
                break;
            case 1:
                tormentaActiva = true;
                log.escribir("TORMENTA DEL UPSIDE DOWN");
                break;
            case 2:
                elevenActiva = true;
                log.escribir("INTERVENCIÓN DE ELEVEN");
                ejecutarMilagroEleven(); // Eleven actúa de inmediato
                break;
            case 3:
                redMentalActiva = true;
                log.escribir("LA RED MENTAL");
                break;
        }
    }

    private synchronized void desactivarEventos() {
        if (apagonActivo) {
        log.escribir("FIN EVENTO: El Apagón ha acabado, los portales se reactivan.");
        apagonActivo = false;
        // Avisamos a todos los portales de que pueden despertar a los niños
        for (Portal p : portales) {
            p.finApagon();
        }
    }
        if (apagonActivo) log.escribir("FIN EVENTO: El Apagón ha acabado");
        if (tormentaActiva) log.escribir("FIN EVENTO: La Tormenta ha acabado");
        if (elevenActiva) log.escribir("FIN EVENTO: Eleven deja de usar sus poderes");
        if (redMentalActiva) log.escribir("FIN EVENTO: La Red Mental se desconecta");

        apagonActivo = false;
        tormentaActiva = false;
        elevenActiva = false;
        redMentalActiva = false;
    }

    private void ejecutarMilagroEleven() {
        int sangreDisponible = sangreVecna.get(); // Leemos del AtomicInteger
        
        if (sangreDisponible > 0) {
            // Saca a los niños de la colmena[cite: 2]
            ArrayList<Niño> rescatados = colmena.rescatarNiños(sangreDisponible);
            
            if (!rescatados.isEmpty()) {
                // Restamos la sangre gastada de forma segura
                sangreVecna.addAndGet(-rescatados.size()); 
                log.escribir("Eleven rescata a " + rescatados.size() + " niños. Sangre restante: " + sangreVecna.get());
                
                for (Niño n : rescatados) {
                    callePrincipal.entrar(n); // Vuelven a la zona segura[cite: 2]
                    n.serLiberado(); // Los despertamos de su wait()
                }
            }
        }
    }
}