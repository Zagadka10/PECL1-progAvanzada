package logica;

import java.util.concurrent.atomic.AtomicInteger;

public class Niño extends Thread {
    private boolean capturado = false;
    private final String id;
    private final Zona sotanoByers, callePrincipal, radioWSQK, colmena;
    private final Portal[] portales;
    private final HawkinsLog log; 
    
    // Nuevos atributos
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

    @Override
    public void run() {
        try {
            callePrincipal.entrar(this);
            Thread.sleep(3 + (int) (Math.random() * 3000)); //3-5s
            callePrincipal.salir(this);

            while (!Thread.currentThread().isInterrupted()) {
                //Pausa desde el modulo remoto
                gestor.comprobarPausa();
                
                sotanoByers.entrar(this);
                log.escribir(id + " entra a Sotano Byers.");
                Thread.sleep(1000 + (int)(Math.random() * 2000)); //1-2s
                sotanoByers.salir(this);
                
                //Selecciona portal, espera quorum segun dnd vaya y entra
                Thread.sleep((1 + (int) (Math.random() * 2)) * 1000); 
                sotanoByers.salir(this);

                // EVENTO: APAGÓN (Los niños no pueden usar los portales)
                if (gestor.isApagonActivo()) {
                    log.escribir(id + " pospone su viaje debido al Apagón.");
                    Thread.sleep(1000); 
                    continue; 
                }

                Portal portalElegido = portales[(int) (Math.random() * portales.length)];
                portalElegido.bajarUpsideDown(this);
                Zona zonaElegida = portalElegido.getZonaDestino();
                zonaElegida.entrar(this);
                log.escribir(id + " explorando " + zonaElegida.getId() + ".");
                
                // Tiempo en el Upside Down: 3 a 5 segundos
                long estancia = (3 + (long)(Math.random() * 3)) * 1000; 
                
                // EVENTO: TORMENTA (Tiempo de recolección duplicado)]
                if (gestor.isTormentaActiva()) {
                    estancia *= 2; 
                }

                Thread.sleep(estancia);
                
                // Si ha sido capturado, el wait() de serCapturado() bloqueó el hilo.
                // Esta comprobación evita que intente regresar si el demogorgon se lo llevó.
                if (!capturado) {
                    zonaElegida.salir(this);
                    log.escribir(id + " regresa a Hawkins con sangre.");
                    portalElegido.volverHawkins(this);

                    // Deposita la sangre de forma segura[cite: 2]
                    sangreVecna.incrementAndGet();

                    radioWSQK.entrar(this);
                    log.escribir(id + " entra a Radio WSQK.");
                    Thread.sleep((2 + (int) (Math.random() * 3)) * 1000); 
                    radioWSQK.salir(this);
                    
                    callePrincipal.entrar(this);
                    log.escribir(id + " entra a la CALLE PRINCIPAL.");
                    Thread.sleep((3 + (int) (Math.random() * 3)) * 1000); 
                    callePrincipal.salir(this);
                }
            }
        } catch (InterruptedException ie) {
            log.escribir(ie.getMessage() + " Para el niño " + id);
        }
    }
}