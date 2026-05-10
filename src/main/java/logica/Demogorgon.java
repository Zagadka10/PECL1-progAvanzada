package logica;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Demogorgon extends Thread {

    private final String id;
    private final ArrayList<Zona> zonasUpsideDown;
    private final Zona colmena;
    private final AtomicInteger capturas;
    private Zona zonaActual;
    private final Random random;
    private final HawkinsLog log;
    private final GestorEventos gestor; 
    private int capturasIndividuales;
    private final CopyOnWriteArrayList<Demogorgon> listaDemogorgons; 

    // Constructor actualizado
    public Demogorgon(String id, ArrayList<Zona> zonasUpsideDown, Zona colmena,
            HawkinsLog log, GestorEventos gestor, AtomicInteger capturas, CopyOnWriteArrayList<Demogorgon> listaDemogorgons) {
        this.id = id;
        this.zonasUpsideDown = zonasUpsideDown;
        this.colmena = colmena;
        this.random = new Random();
        this.log = log;
        this.gestor = gestor;
        this.capturas = capturas;
        this.capturasIndividuales = 0;
        this.listaDemogorgons = listaDemogorgons;
    }

    public String getIdentificador() {
        return id;
    }

    public AtomicInteger getCapturas() {
        return capturas;
    }

    public Zona getZonaActual() {
        return zonaActual;
    }

    public int getCapturasIndividuales() {
        return capturasIndividuales;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                //Pausa ddesde el modulo remoto
                gestor.comprobarPausa();
                // EVENTO: INTERVENCIÓN DE ELEVEN (Parálisis total)
                if (gestor.isElevenActiva()) {
                    Thread.sleep(1000);
                    continue;
                }

                Zona siguienteZona = elegirSiguienteZona();

                if (zonaActual != null) {
                    zonaActual.salirDemogorgon(this);
                }

                zonaActual = siguienteZona;
                zonaActual.entrarDemogorgon(this);

                int numNinosEnZona = zonaActual.getNumeroNiños();

                if (numNinosEnZona > 0) {
                    Niño objetivo = zonaActual.obtenerNiñoAleatorio();
                    if (objetivo != null) {
                        realizarAtaque(objetivo);
                    }
                } else {
                    long tiempoPatrullaSegundos = 4 + random.nextInt(2); // 4 a 5 segs

                    if (gestor.isTormentaActiva()) {
                        tiempoPatrullaSegundos /= 2;
                    }
                    
                    // El monstruo comprueba la pausa en cada paso de su patrulla
                    while (tiempoPatrullaSegundos > 0) {
                        gestor.comprobarPausa();
                        Thread.sleep(1000);
                        tiempoPatrullaSegundos--;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.escribir("El demogorgon " + id + " ha sido interrumpido.");
        }
    }

    private Zona elegirSiguienteZona() {
        // EVENTO: RED MENTAL (Van a la zona con más niños)
        if (gestor.isRedMentalActiva()) {
            return obtenerZonaConMasNiños();
        }

        // EVENTO: APAGÓN (No pueden cambiar de zona)
        if (gestor.isApagonActivo() && zonaActual != null) {
            return zonaActual;
        }

        // Movimiento aleatorio normal
        return zonasUpsideDown.get(random.nextInt(zonasUpsideDown.size()));
    }

    // Método auxiliar para el evento Red Mental
    private Zona obtenerZonaConMasNiños() {
        Zona zonaMasLlena = zonasUpsideDown.get(0);
        int maxNinos = zonaMasLlena.getNumeroNiños();

        for (Zona z : zonasUpsideDown) {
            if (z.getNumeroNiños() > maxNinos) {
                maxNinos = z.getNumeroNiños();
                zonaMasLlena = z;
            }
        }
        return zonaMasLlena;
    }

    private void realizarAtaque(Niño objetivo) throws InterruptedException {
        long tiempoAtaque = 500 + random.nextInt(1001);
        Thread.sleep(tiempoAtaque);

        boolean capturaExitosa = (random.nextInt(3) == 0); // 1/3 de probabilidad de éxito

        if (capturaExitosa) {
            if (!objetivo.isCapturado() && zonaActual.salir(objetivo)) {
                colmena.entrar(objetivo);
                objetivo.serCapturado();

                long tiempoDeposito = 500 + random.nextInt(501);
                Thread.sleep(tiempoDeposito);

                capturasIndividuales++; //guardamos las individuales

                // Suma 1 de forma segura y obtiene el total exacto en ese milisegundo
                int total = capturas.incrementAndGet();
                log.escribir(id + " ha capturado a " + objetivo.getIdNino() + ". (Captura Global: " + total + ")");

                // Si el total es múltiplo de 8, invocamos a un nuevo Demogorgon
                if (total % 8 == 0) {
                    // Calculamos el nuevo ID 
                    int nuevoIdNum = total / 8;
                    String nuevoId = String.format("D%04d", nuevoIdNum);

                    // El Demogorgon instancia al nuevo clon pasándole los mismos recursos compartidos
                    Demogorgon nuevoDemo = new Demogorgon(nuevoId, zonasUpsideDown, colmena, log, gestor, capturas, listaDemogorgons);
                    listaDemogorgons.add(nuevoDemo);
                    nuevoDemo.start();
                    log.escribir(id + " ha capturado a " + objetivo.getIdNino() + " (capturas: " + capturas + ")");
                }
            } else {
                log.escribir(id + " intentó capturar a " + objetivo.getIdNino() + " pero ya no estaba.");
            }
        }
    }
}
