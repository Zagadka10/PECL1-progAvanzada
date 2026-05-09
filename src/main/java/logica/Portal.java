package logica;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.*;

public class Portal {

    private final String nombre;
    private final Zona zonaDestino;
    private final int tamanoGrupo;
    
    private final CyclicBarrier quorum;

    private final ReentrantLock cerrojo = new ReentrantLock();
    // Colas de espera
    private final Condition esperaFormarGrupo = cerrojo.newCondition();
    private final Condition esperaCruzarBajada = cerrojo.newCondition();
    private final Condition esperaSubida = cerrojo.newCondition();
    Condition esperaApagon = cerrojo.newCondition();

    // Contadores de estado
    private int niñosEsperandoBajar = 0;
    private int niñosCruzandoBajada = 0;
    private int niñosVuelta = 0;
    private boolean portalOcupado = false;
    private boolean grupoCruzando = false;

    private HawkinsLog log;
    private final GestorEventos gestor;

    public Portal(String nombre, Zona zonaDestino, int tamanoGrupo, HawkinsLog log, GestorEventos gestor) {
        this.nombre = nombre;
        this.zonaDestino = zonaDestino;
        this.tamanoGrupo = tamanoGrupo;
        this.log = log;
        this.gestor = gestor;
        this.quorum = new CyclicBarrier(tamanoGrupo);
    }

    public void bajarUpsideDown(Niño niño) throws InterruptedException {
        //comprobar apagon
        cerrojo.lock();
        try {
            while (gestor.isApagonActivo()) {
                esperaApagon.await();
            }
        } finally {
            cerrojo.unlock();
        }

        // ESPERAR AL GRUPO (fuera del cerrojo general)
        try {
            quorum.await(); 
        } catch (BrokenBarrierException e) {
            System.out.println("La barrera del portal se rompió: " + e.getMessage());
        }

        // PASO FÍSICO POR EL PORTAL 
        cerrojo.lock();
        try {
            niñosCruzandoBajada++;
            while (portalOcupado || niñosVuelta > 0) {
                esperaCruzarBajada.await();
            }
            portalOcupado = true;
        } finally {
            cerrojo.unlock();
        }

        // Cruzar (Simula el tiempo de viaje físico)
        Thread.sleep(1000);

        cerrojo.lock();
        try {
            portalOcupado = false;
            niñosCruzandoBajada--;

            // Si hay niños esperando para subir a Hawkins, les damos prioridad
            if (niñosVuelta > 0) {
                esperaSubida.signal();
            } else {
                esperaCruzarBajada.signal(); // Si no, que pase el siguiente de mi grupo que baja
            }
        } finally {
            cerrojo.unlock();
        }
    }

    public void volverHawkins(Niño n) throws InterruptedException {
        cerrojo.lock();
        try {
            niñosVuelta++;
            while (portalOcupado) {
                esperaSubida.await();
            }
            portalOcupado = true;
        } finally {
            cerrojo.unlock();
        }

        Thread.sleep(1000);

        cerrojo.lock();
        try {
            portalOcupado = false;
            niñosVuelta--;

            // Si hay más esperando subir, les doy paso. Si no, doy paso a los que bajan.
            if (niñosVuelta > 0) {
                esperaSubida.signal();
            } else {
                esperaCruzarBajada.signal();
            }
        } finally {
            cerrojo.unlock();
        }
    }

    //método que llamará el Gestor cuando vuelva la luz
    public void finApagon() {
        cerrojo.lock();
        try {
            esperaApagon.signalAll(); // Despierta a TODOS los niños bloqueados por el apagón
        } finally {
            cerrojo.unlock();
        }
    }
    
    public int getNumeroNinosActuales() {
        cerrojo.lock();
        try {
            // Sumamos todos los niños que están interactuando con el portal
            return niñosEsperandoBajar + niñosCruzandoBajada + niñosVuelta;
        } finally {
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
