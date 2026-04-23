
package logica;

public class Niño extends Thread{
    private boolean capturado = false;
    private final String id;
    private final Zona sotanoByers, callePrincipal;

    public Niño(String id, Zona sotanoByers, Zona callePrincipal) {
        this.id = id;
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
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
            Thread.sleep((1 + (int) (Math.random() * 2)) * 1000); //1-2s
            callePrincipal.salir(this);
            while (!Thread.currentThread().isInterrupted()) {
                //accede a sotano byers
                sotanoByers.entrar(this);
                Thread.sleep((1 + (int) (Math.random() * 2)) * 1000); //1-2s
                sotanoByers.salir(this);

                //Selecciona portal y espera quorum segun dnd vaya
            }
        } catch (InterruptedException ie) {
            ie.getMessage();
        }
    }

}
