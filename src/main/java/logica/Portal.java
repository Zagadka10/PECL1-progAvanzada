/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.*;

/**
 *
 * @author hecto
 */
public class Portal {

    private final String nombre;
    private final Zona zonaDestino;

    private final CyclicBarrier quorum;

    private final ReentrantLock cerrojo = new ReentrantLock();
    private final Condition esperaBajada = cerrojo.newCondition();
    private final Condition esperaSubida = cerrojo.newCondition();
    private boolean portalOcupado;
    private int niñosvuelta = 0;

    public Portal(String nombre, Zona zonaDestino, int numeroGrupo) {
        this.nombre = nombre;
        this.zonaDestino = zonaDestino;
        this.quorum = new CyclicBarrier(numeroGrupo);
    }

    public void bajarUpsideDown(Niño niño) throws InterruptedException {
        try {
            //formamos los grupos
            quorum.await();
            cerrojo.lock();
            try {
                while (portalOcupado || niñosvuelta > 0) {
                    esperaBajada.await();
                }
                portalOcupado = true;
            } finally {
                cerrojo.unlock();
            }
            
            Thread.sleep(1000);
            
            //Libera los  portales con prioridad.
            cerrojo.lock();
            try{
                portalOcupado = false;
                if (niñosvuelta > 0){
                    esperaSubida.signal();
                }else{
                    esperaBajada.signal();
                }
            }finally{
                cerrojo.unlock();
            }
        } catch (BrokenBarrierException b) {
            Thread.currentThread().interrupt();
            System.out.println(b.getMessage());
        }
    }

    public Zona getZonaDestino() {
        return zonaDestino;
    }

    public String getNombre() {
        return nombre;
    }
    
    
}
