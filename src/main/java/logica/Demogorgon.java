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
    

    public Demogorgon(String id, ArrayList<Zona> zonasUpsideDown, Zona colmena) {
        this.id = id;
        this.zonasUpsideDown = zonasUpsideDown;
        this.colmena = colmena;
        this.capturas = 0;
        this.random = new Random();
    }

    public String getIdentificador() { return id; }
    public int getCapturas() { return capturas; }
    public Zona getZonaActual() { return zonaActual; }

    @Override
    public void run() {
        try {
            // 3. Bucle de vida infinito del hilo
            while (!Thread.currentThread().isInterrupted()) {
                
                //PARÁLISIS (Esto lo usamos cuando hagamos los eventos)
                // Si Eleven interviene, el demogorgon se pausa por completo.
                /* if (gestorEventos.isIntervencionEleven()) {
                    Thread.sleep(1000); // Espera activa o usar wait()
                    continue; 
                } */

                // MOVIMIENTO
                // Si hay "Apagón", no pueden cambiar de zona.
                // Si hay "Red Mental", van a la zona con más niños.
                // Por defecto: movimiento aleatorio.
                Zona siguienteZona = elegirSiguienteZona();
                
                if (zonaActual != null) {
                    zonaActual.salirDemogorgon(this);
                }
                
                zonaActual = siguienteZona;
                zonaActual.entrarDemogorgon(this);
                
                // System.out.println(id + " ha llegado a " + zonaActual.getNombre()); // Sustituir por Log

                // Busqueda de niños y ataque
                int numNinosEnZona = zonaActual.getNumeroNiños();
                
                if (numNinosEnZona > 0) {
                    // Hay niños: Intentar capturar a uno aleatorio
                    Niño objetivo = zonaActual.obtenerNiñoAleatorio();
                    
                    if (objetivo != null) {
                        realizarAtaque(objetivo);
                    }
                } else {
                    // No hay niños: Patrullar
                    // Tiempo aleatorio entre 4 y 5 segundos
                    long tiempoPatrulla = 4000 + random.nextInt(1001);
                    
                    // Si hay Tormenta, el tiempo entre ataques se reduce a la mitad
                    /* if (gestorEventos.isTormentaUpsideDown()) {
                        tiempoPatrulla /= 2;
                    } */
                    
                    Thread.sleep(tiempoPatrulla);
                }
            }
        } catch (InterruptedException e) {
            System.out.println("El demogorgon " + id + " ha sido interrumpido.");
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private Zona elegirSiguienteZona() {
        // Lógica para el evento de la RED MENTAL
        /* if (gestorEventos.isRedMental()) {
            return obtenerZonaConMasNiños();
        } */
        
        // Lógica para el evento de APAGÓN DEL LABORATORIO
        /* if (gestorEventos.isApagon() && zonaActual != null) {
            return zonaActual; // Se queda en la misma zona
        } */

        // Movimiento aleatorio por defecto
        return zonasUpsideDown.get(random.nextInt(zonasUpsideDown.size()));
    }

    private void realizarAtaque(Niño objetivo) throws InterruptedException {
        // Duración del ataque: entre 0,5 y 1,5 segundos
        long tiempoAtaque = 500 + random.nextInt(1001);
        Thread.sleep(tiempoAtaque);

        // Probabilidad de que el niño resista (2/3 de resistir, por tanto 1/3 de ser capturado)
        // random.nextInt(3) genera 0, 1 o 2. Si es 0 (1/3 de probabilidad), el ataque tiene éxito.
        boolean capturaExitosa = (random.nextInt(3) == 0);

        if (capturaExitosa) {
            // El ataque tiene éxito
            zonaActual.salir(objetivo); // Lo sacamos de la zona actual
            colmena.entrar(objetivo);   // Lo metemos en la colmena
            objetivo.serCapturado();    // Avisamos al hilo del niño que ha sido capturado
            
            // Tiempo en depositar al niño: entre 0,5 y 1 segundos
            long tiempoDeposito = 500 + random.nextInt(501);
            Thread.sleep(tiempoDeposito);
            
            capturas++;
            // System.out.println(id + " ha capturado a " + objetivo.getIdentificador() + " (capturas: " + capturas + ")"); // Log
        } else {
            // El ataque fracasa, el niño permanece en el área
            // System.out.println(id + " falló al atacar a " + objetivo.getIdentificador()); // Log
        }
    }
}