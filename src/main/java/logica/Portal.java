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
    private int niñosVuelta = 0;
    private HawkinsLog log;

    public Portal(String nombre, Zona zonaDestino, int numeroGrupo, HawkinsLog log) {
        this.nombre = nombre;
        this.zonaDestino = zonaDestino;
        this.quorum = new CyclicBarrier(numeroGrupo);
        this.log = log;
    }

    public void bajarUpsideDown(Niño niño) throws InterruptedException {
        try {
            //formamos los grupos
            quorum.await();
            cerrojo.lock();
            try {
                while (portalOcupado || niñosVuelta > 0) {
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
                if (niñosVuelta > 0){
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
    
    public void volverHawkins(Niño n) throws InterruptedException{
        cerrojo.lock();
        try{
            niñosVuelta++;
            while(portalOcupado){
                esperaSubida.await();
            }
            portalOcupado = true;
        }finally{
            cerrojo.unlock();
        }
        
        Thread.sleep(1000);
        
        cerrojo.lock();
        try{
            portalOcupado = false;
            niñosVuelta--;
            if(niñosVuelta > 0){
                esperaSubida.signal();
            }else{
                esperaBajada.signal();
            }
        }finally{
            cerrojo.unlock();
        }
    }

    public Zona getZonaDestino() {
        return zonaDestino;
    }

    public String getNombre() {
        return nombre;
    }
  
}
