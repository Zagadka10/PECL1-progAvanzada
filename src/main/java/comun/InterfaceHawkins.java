/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package comun;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author hecto
 */
public interface InterfaceHawkins extends Remote{
    // DATOS GLOBALES 
    int getSangreVecna() throws RemoteException;
    int getCapturasTotales() throws RemoteException; // Para saber cuántos Demogorgons se han creado

    // ESTADO DE LOS EVENTOS 
    boolean isApagonActivo() throws RemoteException;
    boolean isTormentaActiva() throws RemoteException;
    boolean isElevenActiva() throws RemoteException;
    boolean isRedMentalActiva() throws RemoteException;

    // DATOS DE LAS ZONAS 
    // Le pasaremos el nombre exacto de la zona y nos devolverá la cantidad
    int getNumeroNinosEnZona(String nombreZona) throws RemoteException;
    int getNumeroDemogorgonsEnZona(String nombreZona) throws RemoteException;
    
    // DATOS DEL PORTAL 
    // Opcional pero muy útil para la GUI: saber si un portal está abierto o apagado
    boolean isPortalAbierto(String nombrePortal) throws RemoteException;
}
