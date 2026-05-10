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
public interface InterfazHawkins extends Remote{
    //RESUMEN HAWKINS 
    int getTotalNinosHawkins() throws RemoteException; 

    // PORTALES
    // Le pasaremos un 1, 2, 3 o 4 y nos dirá los niños que hay cruzando
    int getNinosEnPortal(int numeroPortal) throws RemoteException; 

    // UPSIDE DOWN
    int getNumeroNinosEnZona(String nombreZona) throws RemoteException;
    int getNumeroDemogorgonsEnZona(String nombreZona) throws RemoteException;
    // TOP 3
    // La posición será 1, 2 o 3. Separamos el nombre de las capturas para tus labels
    String getNombreTopDemogorgon(int posicion) throws RemoteException;
    int getCapturasTopDemogorgon(int posicion) throws RemoteException;

    // EVENTOS 
    String getTipoEventoActivo() throws RemoteException;
    int getTiempoRestanteEvento() throws RemoteException; 

    // CONTROL DE SIMULACIÓN 
    void pausarSimulacion() throws RemoteException;
    void reanudarSimulacion() throws RemoteException;
}
