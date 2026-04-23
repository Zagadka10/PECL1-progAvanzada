/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author hecto
 */
public class Niño extends Thread {

    private final String id;
    private final Zona sotanoByers, callePrincipal, radioWSQK, colmena;

    public Niño(String id, Zona sotanoByers, Zona callePrincipal, Zona radioWSQK, Zona colmena) {
        this.id = String.format("N%04d", id);
        this.sotanoByers = sotanoByers;
        this.callePrincipal = callePrincipal;
        this.radioWSQK = radioWSQK;
        this.colmena = colmena;
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
