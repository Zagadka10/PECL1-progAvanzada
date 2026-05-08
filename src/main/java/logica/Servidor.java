/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author hecto
 */
import comun.InterfaceHawkins;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementación del objeto remoto. Se encarga de consultar la lógica local y
 * devolver los datos al cliente.
 */
public class Servidor extends UnicastRemoteObject implements InterfaceHawkins {

    private final ArrayList<Zona> zonas;
    private final Portal[] portales;
    private final GestorEventos gestor;
    private final AtomicInteger sangreVecna;
    private final AtomicInteger capturasTotales;

    // El constructor recibe todo lo que el Main ha inicializado
    public Servidor(ArrayList<Zona> zonas, Portal[] portales, GestorEventos gestor, 
            AtomicInteger sangreVecna, AtomicInteger capturasTotales) throws RemoteException {
        super(); // Llama al constructor de UnicastRemoteObject para exportar el objeto
        this.zonas = zonas;
        this.portales = portales;
        this.gestor = gestor;
        this.sangreVecna = sangreVecna;
        this.capturasTotales = capturasTotales;
    }

    @Override
    public int getSangreVecna() throws RemoteException {
        return sangreVecna.get();
    }

    @Override
    public int getCapturasTotales() throws RemoteException {
        return capturasTotales.get();
    }

    @Override
    public boolean isApagonActivo() throws RemoteException {
        return gestor.isApagonActivo();
    }

    @Override
    public boolean isTormentaActiva() throws RemoteException {
        return gestor.isTormentaActiva();
    }

    @Override
    public boolean isElevenActiva() throws RemoteException {
        return gestor.isElevenActiva();
    }

    @Override
    public boolean isRedMentalActiva() throws RemoteException {
        return gestor.isRedMentalActiva();
    }

    @Override
    public int getNumeroNinosEnZona(String nombreZona) throws RemoteException {
        for (Zona z : zonas) {
            if (z.getId().equalsIgnoreCase(nombreZona)) {
                return z.getNumeroNiños();
            }
        }
        return 0;
    }

    @Override
    public int getNumeroDemogorgonsEnZona(String nombreZona) throws RemoteException {
        for (Zona z : zonas) {
            if (z.getId().equalsIgnoreCase(nombreZona)) {
                return z.getNumeroDemogorgons();
            }
        }
        return 0;
    }

    @Override
    public boolean isPortalAbierto(String nombrePortal) throws RemoteException {
        for (Portal p : portales) {
            if (p.getNombre().equalsIgnoreCase(nombrePortal)) {
                // El portal está abierto si NO hay apagón
                return !gestor.isApagonActivo();
            }
        }
        return false;
    }
}

