package logica;

import java.util.concurrent.atomic.AtomicInteger;

public class Niño extends Thread {

    private boolean capturado = false;
    private final String id;
    private final Zona sotanoByers, callePrincipal, radioWSQK, colmena;
    private final Portal[] portales;
    private final HawkinsLog log;
    private final GestorEventos gestor;
    private final AtomicInteger sangreVecna;

    // Constructor actualizado
    public Niño(String id, Zona sotanoByers, Zona callePrincipal, Zona radioWSQK, Zona colmena, Portal[] portales, HawkinsLog log, GestorEventos gestor, AtomicInteger sangreVecna) {
        // Corregido el String.format para evitar excepciones convirtiéndolo a número primero
        this.id = String.format("N%04d", Integer.parseInt(id));
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
        this.radioWSQK = radioWSQK;
        this.colmena = colmena;
        this.portales = portales;
        this.log = log;
        this.gestor = gestor;
        this.sangreVecna = sangreVecna;
    }

    // Añadido getter para que el Demogorgon pueda registrar quién es su víctima
    public String getIdNino() {
        return this.id;
    }

    public synchronized void serCapturado() {
        // El Demogorgon llama aquí. Solo ponemos la bandera, SIN wait().
        this.capturado = true;
    }

    //El niño lo llama para congelarse a sí mismo
    public synchronized void esperarRescate() {
        try {
            while (this.capturado) {
                this.wait();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //Demogorgon compruebe si ya lo ha cazado otro
    public boolean isCapturado() {
        return capturado;
    }

    public synchronized void serLiberado() {
        this.capturado = false;
        this.notifyAll(); //envolver el rescate en el mismo objeto de sincronizacion
    }

    //Necesario dividir los sleeps para pausar la ejecucion en remoto
    private void dormirConPausa(long milisegundos) throws InterruptedException {
        long iteraciones = milisegundos / 500; // Troceamos el tiempo en siestas de medio segundo
        for (int i = 0; i < iteraciones; i++) {
            gestor.comprobarPausa(); // Miramos el semáforo
            Thread.sleep(500);
        }
    }

    @Override
    public void run() {
        try {
            gestor.comprobarPausa();
            
            callePrincipal.entrar(this);
            dormirConPausa(3000 + (long) (Math.random() * 2000)); // 3-5s
            callePrincipal.salir(this);

            while (!Thread.currentThread().isInterrupted()) {
                gestor.comprobarPausa();

                sotanoByers.entrar(this);
                log.escribir(id + " entra a Sotano Byers.");
                dormirConPausa(1000 + (long) (Math.random() * 1000)); // 1-2s
                sotanoByers.salir(this);

                dormirConPausa(1000 + (long) (Math.random() * 1000)); // 1-2s antes del portal

                // EVENTO: APAGÓN 
                if (gestor.isApagonActivo()) {
                    log.escribir(id + " pospone su viaje debido al Apagón.");
                    dormirConPausa(1000);
                    continue;
                }

                Portal portalElegido = portales[(int) (Math.random() * portales.length)];
                portalElegido.bajarUpsideDown(this);
                Zona zonaElegida = portalElegido.getZonaDestino();
                zonaElegida.entrar(this);
                log.escribir(id + " explorando " + zonaElegida.getId() + ".");

                long estancia = 3000 + (long) (Math.random() * 2000); // 3-5s
                if (gestor.isTormentaActiva()) {
                    estancia *= 2;
                }
                dormirConPausa(estancia);

                // --- COMPROBACIÓN DE CAPTURA (Soluciona a los niños fantasma) ---
                if (capturado) {
                    esperarRescate(); // ¡Se congela aquí hasta que Eleven lo salve!

                    log.escribir(id + " celebra su rescate en la Calle Principal.");
                    dormirConPausa(2000); // 2 segundos de celebración
                    callePrincipal.salir(this);

                    continue; // Vuelve a empezar el ciclo limpio
                }

                zonaElegida.salir(this);
                log.escribir(id + " regresa a Hawkins con sangre.");
                portalElegido.volverHawkins(this);

                sangreVecna.incrementAndGet();

                radioWSQK.entrar(this);
                log.escribir(id + " entra a Radio WSQK.");
                dormirConPausa(2000 + (long) (Math.random() * 1000)); // 2-3s
                radioWSQK.salir(this);

                callePrincipal.entrar(this);
                log.escribir(id + " entra a la CALLE PRINCIPAL.");
                dormirConPausa(3000 + (long) (Math.random() * 2000)); // 3-5s
                callePrincipal.salir(this);
            }
        } catch (InterruptedException ie) {
            log.escribir("Hilo interrumpido para el niño " + id);
        }
    }
}
