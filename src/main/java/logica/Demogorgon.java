package logica;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Demogorgon extends Thread {

    private final String id;
    private final ArrayList<Zona> zonasUpsideDown;
    private final Zona colmena;
    private final AtomicInteger capturas;
    private Zona zonaActual;
    private final Random random;
    private final HawkinsLog log;
    private final GestorEventos gestor; // Atributo añadido
    private int capturasIndividuales;

    // Constructor actualizado
    public Demogorgon(String id, ArrayList<Zona> zonasUpsideDown, Zona colmena, HawkinsLog log, GestorEventos gestor, AtomicInteger capturas) {
        this.id = id;
        this.zonasUpsideDown = zonasUpsideDown;
        this.colmena = colmena;
        this.random = new Random();
        this.log = log;
        this.gestor = gestor;
        this.capturas = capturas;
        this.capturasIndividuales = 0;
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

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

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
                    long tiempoPatrulla = 4000 + random.nextInt(1001);

                    // EVENTO: TORMENTA (Tiempo entre ataques/patrulla se reduce a la mitad)
                    if (gestor.isTormentaActiva()) {
                        tiempoPatrulla /= 2;
                    }

                    Thread.sleep(tiempoPatrulla);
                }

                int capturasGlobales = capturas.incrementAndGet();
                if (capturasGlobales % 8 == 0) {
                    // Generar nuevo demogorgon
                    Demogorgon nuevoD = new Demogorgon("D" + String.format("%04d", capturasGlobales / 8), zonasUpsideDown, colmena, log, gestor, capturas);
                    nuevoD.start();
                    log.escribir("¡Vecna ha creado un nuevo Demogorgon! (" + nuevoD.getIdentificador() + ")");
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
            zonaActual.salir(objetivo);
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
                // Calculamos el nuevo ID (D0001, D0002...) basándonos en la división
                int nuevoIdNum = total / 8;
                String nuevoId = String.format("D%04d", nuevoIdNum);

                // El Demogorgon instancia al nuevo clon pasándole los mismos recursos compartidos
                Demogorgon nuevoDemo = new Demogorgon(nuevoId, zonasUpsideDown, colmena, log, gestor, capturas);
                nuevoDemo.start();
                log.escribir(id + " ha capturado a " + objetivo.getIdNino() + " (capturas: " + capturas + ")");
            }
        }
    }
}
