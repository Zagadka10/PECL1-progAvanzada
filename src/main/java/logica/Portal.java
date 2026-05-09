package logica;

import java.util.concurrent.locks.*;

public class Portal {

    private final String nombre;
    private final Zona zonaDestino;
    private final int tamanoGrupo;

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

    public Portal(String nombre, Zona zonaDestino, int numeroGrupo, HawkinsLog log, GestorEventos gestor) {
        this.nombre = nombre;
        this.zonaDestino = zonaDestino;
        this.tamanoGrupo = numeroGrupo;
        this.log = log;
        this.gestor = gestor;
    }

    public void bajarUpsideDown(Niño niño) throws InterruptedException {
        cerrojo.lock();
        try {
            niñosEsperandoBajar++;
            // Si hay apagón, se quedan aquí esperando a que GestorEventos llame a signalAll()
            while (gestor.isApagonActivo()) {
                esperaApagon.await();
            }

            //Esperar a que haya quorum Y que ningún otro grupo esté cruzando
            while (niñosEsperandoBajar < tamanoGrupo || grupoCruzando) {
                // Si justo yo completo el grupo y nadie cruza, despierto a los demás
                if (niñosEsperandoBajar >= tamanoGrupo && !grupoCruzando) {
                    grupoCruzando = true;
                    niñosCruzandoBajada = tamanoGrupo; // Registramos cuántos van a cruzar
                    esperaFormarGrupo.signalAll(); // Despierta al resto del grupo
                    break;
                }
                esperaFormarGrupo.await();
            }

            niñosEsperandoBajar--; // Ya formo parte del grupo que cruza

            // Esperar mi turno individual para cruzar (y ceder prioridad a los que suben)
            while (portalOcupado || niñosVuelta > 0) {
                esperaCruzarBajada.await();
            }
            portalOcupado = true;
        } finally {
            cerrojo.unlock();
        }

        // Cruzar (Fuera del lock para que no se congele todo el programa)
        Thread.sleep(1000);

        cerrojo.lock();
        try {
            portalOcupado = false;
            niñosCruzandoBajada--;

            // Si fui el último del grupo en cruzar, el portal queda libre para otro grupo
            if (niñosCruzandoBajada == 0) {
                grupoCruzando = false;
                esperaFormarGrupo.signalAll(); // Aviso a los que intentan formar nuevo grupo
            }

            // Gestión de prioridades al liberar el portal
            if (niñosVuelta > 0) {
                esperaSubida.signal(); // Prioridad a los que vuelven
            } else {
                esperaCruzarBajada.signal(); // Paso al siguiente del grupo que baja
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
