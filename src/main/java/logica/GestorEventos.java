package logica;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorEventos extends Thread {

    // Flags de los eventos
    private boolean apagonActivo = false;
    private boolean tormentaActiva = false;
    private boolean elevenActiva = false;
    private boolean redMentalActiva = false;

    //Variables para el servidor
    private String nombreEventoActual = "Sin evento activo";
    private int tiempoRestante = 0;
    private boolean pausado = false;

    private final HawkinsLog log;
    private final Zona colmena;
    private final Zona callePrincipal;
    private final AtomicInteger sangreVecna;
    private final Portal[] portales;

    public GestorEventos(HawkinsLog log, Zona colmena, Zona callePrincipal, AtomicInteger sangreVecna, Portal[] portales) {
        this.log = log;
        this.colmena = colmena;
        this.callePrincipal = callePrincipal;
        this.sangreVecna = sangreVecna;
        this.portales = portales;
        this.setDaemon(true);
    }

    // Getters sincronizados para que los hilos consulten qué pasa
    public synchronized boolean isApagonActivo() {
        return apagonActivo;
    }

    public synchronized boolean isTormentaActiva() {
        return tormentaActiva;
    }

    public synchronized boolean isElevenActiva() {
        return elevenActiva;
    }

    public synchronized boolean isRedMentalActiva() {
        return redMentalActiva;
    }

    //Getter para RMI 
    public synchronized String getNombreEventoActual() {
        return nombreEventoActual;
    }

    public synchronized int getTiempoRestante() {
        return tiempoRestante;
    }

    // Modificamos el setter para que despierte a los hilos al reanudar
    public synchronized void setPausado(boolean estado) {
        this.pausado = estado;
        if (!pausado) {
            this.notifyAll(); 
        }
    }

    public synchronized void comprobarPausa() throws InterruptedException {
        while (pausado) {
            this.wait(); // Si hay pausa, el hilo que llame a esto se queda congelado
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                comprobarPausa();
                
                // Tiempo entre eventos: Bucle de 1 en 1 segundo
                long tiempoEspera = 30 + (long) (Math.random() * 30); // 30 a 60 seg
                while (tiempoEspera > 0) {
                    comprobarPausa(); // Escucha al botón cada segundo
                    Thread.sleep(1000);
                    tiempoEspera--;
                }

                // Elegir un evento al azar
                int tipoEvento = (int) (Math.random() * 4);
                activarEvento(tipoEvento);

                // Duración del evento 
                long duracionEvento = 5 + (long) (Math.random() * 5); // 5 a 10 seg
                tiempoRestante = (int) duracionEvento;

                while (tiempoRestante > 0) {
                    comprobarPausa(); // ¡ESTO CONGELA EL RELOJ DE TU GUI!
                    Thread.sleep(1000);
                    tiempoRestante--; 
                }
                
                // Terminar evento
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
                nombreEventoActual = "Apagón";
                log.escribir("APAGÓN DEL LABORATORIO");
                break;
            case 1:
                tormentaActiva = true;
                nombreEventoActual = "Tormenta";
                log.escribir("TORMENTA DEL UPSIDE DOWN");
                break;
            case 2:
                elevenActiva = true;
                nombreEventoActual = "Milagro Eleven";
                log.escribir("INTERVENCIÓN DE ELEVEN");
                ejecutarMilagroEleven(); // Eleven actúa de inmediato
                break;
            case 3:
                redMentalActiva = true;
                nombreEventoActual = "Red Mental";
                log.escribir("LA RED MENTAL");
                break;
        }
    }

    private synchronized void desactivarEventos() {
        if (apagonActivo) {
            log.escribir("FIN EVENTO: El Apagón ha acabado, los portales se reactivan.");
            apagonActivo = false;
            for (Portal p : portales) {
                p.finApagon();
            }
        }
        if (tormentaActiva) {
            log.escribir("FIN EVENTO: La Tormenta ha acabado");
        }
        if (elevenActiva) {
            log.escribir("FIN EVENTO: Eleven deja de usar sus poderes");
        }
        if (redMentalActiva) {
            log.escribir("FIN EVENTO: La Red Mental se desconecta");
        }

        apagonActivo = false;
        tormentaActiva = false;
        elevenActiva = false;
        redMentalActiva = false;
        nombreEventoActual = "Sin evento activo";
    }

    private void ejecutarMilagroEleven() {
        int sangreDisponible = sangreVecna.get();
        if (sangreDisponible > 0) {
            ArrayList<Niño> rescatados = colmena.rescatarNiños(sangreDisponible);
            if (!rescatados.isEmpty()) {
                sangreVecna.addAndGet(-rescatados.size());
                log.escribir("Eleven rescata a " + rescatados.size() + " niños. Sangre restante: " + sangreVecna.get());
                for (Niño n : rescatados) {
                    callePrincipal.entrar(n);
                    n.serLiberado();
                }
            }
        }
    }
}
