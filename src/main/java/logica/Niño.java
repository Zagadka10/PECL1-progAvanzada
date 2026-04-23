
package logica;

/**
 *
 * @author hecto
 */
public class Niño extends Thread {
    private boolean capturado = false;
    private final String id;
    private final Zona sotanoByers, callePrincipal, radioWSQK, colmena;

    public Niño(String id, Zona sotanoByers, Zona callePrincipal, Zona radioWSQK, Zona colmena) {
        this.id = String.format("N%04d", id);
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
        this.radioWSQK = radioWSQK;
        this.colmena = colmena;
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
            Thread.sleep((3 + (int) (Math.random() * 3)) * 1000); //3-5s
            callePrincipal.salir(this);

            while (!Thread.currentThread().isInterrupted()) {
                //accede a sotano byers
                sotanoByers.entrar(this);
                Thread.sleep((1 + (int) (Math.random() * 2)) * 1000); //1-2s
                sotanoByers.salir(this);

                //Selecciona portal y espera quorum segun dnd vaya
                
                
                //Movidas del Upside-down
                
                
                //vuelta a hawkins
                

                //Radio WSQK
                
                
                radioWSQK.entrar(this);
                Thread.sleep((2 + (int) (Math.random() * 3)) * 1000); //2-4s
                radioWSQK.salir(this);
                
                //Bulteritoss vuelven a Calle Principal
                callePrincipal.entrar(this);
                Thread.sleep((3 + (int) (Math.random() * 3)) * 1000); //3-5s
                callePrincipal.salir(this);

            }
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage() + "Para el niño " + id);
        }
    }

}
