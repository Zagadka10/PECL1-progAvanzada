
package logica;

/**
 *
 * @author hecto
 */
public class Niño extends Thread {
    private boolean capturado = false;
    private final String id;
    private final Zona sotanoByers, callePrincipal, radioWSQK, colmena;
    private final Portal[] portales;
    private final HawkinsLog log; 


    public Niño(String id, Zona sotanoByers, Zona callePrincipal, Zona radioWSQK, Zona colmena, Portal[] portales, HawkinsLog log) {
        this.id = String.format("N%04d", id);
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
        this.radioWSQK = radioWSQK;
        this.colmena = colmena;
        this.portales = portales;
        this.log = log;
    }

    // El Demogorgon llama a este método si gana el combate 
    public synchronized void serCapturado() {
        this.capturado = true;
        // El hilo se queda bloqueado aquí. No hará nada más en su 'run' 
        // hasta que alguien lo despierte.
        try {
            while (capturado) {
                wait(); 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Método que usaremos en la Fase 3 (Evento de Eleven) [cite: 93]
    public synchronized void serLiberado() {
        this.capturado = false;
        notifyAll(); // Despierta al niño para que salga del wait() y siga su ciclo
    }

    @Override
    public void run() {
        try {
            //Inician actividad en calle principal
            callePrincipal.entrar(this);
            Thread.sleep(3 + (int) (Math.random() * 3000)); //3-5s
            callePrincipal.salir(this);

            while (!Thread.currentThread().isInterrupted()) {
                //accede a sotano byers
                sotanoByers.entrar(this);
                log.escribir(id + " entra a Sotano Byers.");
                Thread.sleep(1000 + (int)(Math.random() * 2000)); //1-2s
                sotanoByers.salir(this);
                
                //Selecciona portal, espera quorum segun dnd vaya y entra
                Portal portalElegido = portales[(int) (Math.random() * portales.length)];
                portalElegido.bajarUpsideDown(this);
                Zona zonaElegida = portalElegido.getZonaDestino();
                zonaElegida.entrar(this);
                log.escribir(id + " explorando " + zonaElegida.getId() + ".");
                
                //Movidas del Upside-down TODO
                int estancia = 3 + (int)(Math.random() * 2);
                
                if (capturado){
                    zonaElegida.salir(this);
                    colmena.entrar(this);
                    log.escribir("¡¡¡El niño " + id + "ha sido capturado!!!");
                }
                
                //vuelta a hawkins
                zonaElegida.salir(this);
                log.escribir(id + " regresa a Hawkins con sangre.");
                portalElegido.volverHawkins(this);

                //Radio WSQK
                radioWSQK.entrar(this);
                log.escribir(id + " entra a Radio WSQK.");
                Thread.sleep(2 + (int) (Math.random() * 3000)); //2-4s
                radioWSQK.salir(this);
                
                //Bulteritoss vuelven a Calle Principal
                callePrincipal.entrar(this);
                log.escribir(id + " entra a la CALLE PRINCIPAL.");
                Thread.sleep(3 + (int) (Math.random() * 3000)); //3-5s
                callePrincipal.salir(this);

            }
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage() + "Para el niño " + id);
        }
    }

}
