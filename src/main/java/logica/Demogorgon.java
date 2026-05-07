package logica;

import java.util.ArrayList;
import java.util.Random;

public class Demogorgon extends Thread {
    private final String id;
    private final ArrayList<Zona> zonasUpsideDown;
    private final Zona colmena;
    private int capturas;
    private Zona zonaActual;
    private final Random random;
    private final HawkinsLog log; 
    private final GestorEventos gestor; // Atributo añadido

    // Constructor actualizado
    public Demogorgon(String id, ArrayList<Zona> zonasUpsideDown, Zona colmena, HawkinsLog log, GestorEventos gestor) {
        this.id = id;
        this.zonasUpsideDown = zonasUpsideDown;
        this.colmena = colmena;
        this.capturas = 0;
        this.random = new Random();
        this.log = log;
        this.gestor = gestor; 
    }

    public String getIdentificador() { return id; }
    public int getCapturas() { return capturas; }
    public Zona getZonaActual() { return zonaActual; }

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
                    
                    // EVENTO: TORMENTA (Tiempo entre ataques/patrulla se reduce a la mitad)[cite: 2]
                    if (gestor.isTormentaActiva()) {
                        tiempoPatrulla /= 2;
                    }
                    
                    Thread.sleep(tiempoPatrulla);
                }
            }
        } catch (InterruptedException e) {
            log.escribir("El demogorgon " + id + " ha sido interrumpido.");
        }
    }

    private Zona elegirSiguienteZona() {
        // EVENTO: RED MENTAL (Van a la zona con más niños)[cite: 2]
        if (gestor.isRedMentalActiva()) {
            return obtenerZonaConMasNiños();
        } 
        
        // EVENTO: APAGÓN (No pueden cambiar de zona)[cite: 2]
        if (gestor.isApagonActivo() && zonaActual != null) {
            return zonaActual;
        }

        // Movimiento aleatorio normal[cite: 2]
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
            
            capturas++;
            log.escribir(id + " ha capturado a " + objetivo.getIdNino() + " (capturas: " + capturas + ")"); 
        } 
    }
}