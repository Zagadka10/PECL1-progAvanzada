/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author hecto
 */
public class Niño extends Thread{
    private final String id;
    private final Zona sotanoByers, callePrincipal;

    public Niño(String id, Zona sotanoByers, Zona callePrincipal) {
        this.id = id;
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                //Inician actividad en calle principal
                callePrincipal.entrar(this);
                Thread.sleep((1 + (int) (Math.random() * 2)) * 1000); //1-2s
                callePrincipal.salir(this);

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
