/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

/**
 *
 * @author hecto
 */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import comun.InterfazHawkins;
import static java.util.Collections.sort;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementación del objeto remoto. Se encarga de consultar la lógica local y
 * devolver los datos al cliente.
 */
public class Servidor extends UnicastRemoteObject implements InterfazHawkins {

    private final ArrayList<Zona> zonas;
    private final Portal[] portales;
    private final GestorEventos gestor;
    private final AtomicInteger sangreVecna;
    private final AtomicInteger capturasTotales;
    private final CopyOnWriteArrayList<Demogorgon> listaDemogorgons;

    // El constructor recibe todo lo que el Main ha inicializado
    public Servidor(ArrayList<Zona> zonas, Portal[] portales, GestorEventos gestor,
            AtomicInteger sangreVecna, AtomicInteger capturasTotales, CopyOnWriteArrayList<Demogorgon> listaDemogorgons) throws RemoteException {
        super(); // Llama al constructor de UnicastRemoteObject para exportar el objeto
        this.zonas = zonas;
        this.portales = portales;
        this.gestor = gestor;
        this.sangreVecna = sangreVecna;
        this.capturasTotales = capturasTotales;
        this.listaDemogorgons = listaDemogorgons;
    }

    @Override
    public int getTotalNinosHawkins() throws RemoteException {
        // Sumamos niños en las 3 zonas seguras
        int suma = 0;
        for (Zona z : zonas) {
            if (z.getId().equals("Calle Principal")
                    || z.getId().equals("Sótano Byers")
                    || z.getId().equals("Radio WSQK")) {
                suma += z.getNumeroNiños();
            }
        }
        return suma;
    }

    @Override
    public int getNinosEnPortal(int numeroPortal) throws RemoteException {
        if (numeroPortal >= 1 && numeroPortal <= portales.length) {
            return portales[numeroPortal - 1].getNumeroNinosActuales();
        }
        return 0;
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
    public String getNombreTopDemogorgon(int posicion) throws RemoteException {
        ArrayList<Demogorgon> ordenados = obtenerRanking();
        if (posicion <= ordenados.size()) {
            return ordenados.get(posicion - 1).getIdentificador();
        }
        return "---";
    }

    @Override
    public int getCapturasTopDemogorgon(int posicion) throws RemoteException {
        ArrayList<Demogorgon> ordenados = obtenerRanking();
        if (posicion <= ordenados.size()) {
            return ordenados.get(posicion - 1).getCapturasIndividuales();
        }
        return 0;
    }

    private ArrayList<Demogorgon> obtenerRanking() {
        // 1. Hacemos una copia de la lista para no desordenar la lista original del servidor
        ArrayList<Demogorgon> listaOrdenada = new ArrayList<>(listaDemogorgons);

        // 2. Ordenamos la copia de mayor a menor (descendente)
        sort(listaOrdenada, new Comparator<Demogorgon>() {
            @Override
            public int compare(Demogorgon d1, Demogorgon d2) {
                // Comparamos d2 con d1 para que el mayor quede arriba
                return Integer.compare(d2.getCapturasIndividuales(), d1.getCapturasIndividuales());
            }
        });

        // 3. Extraemos solo los 3 primeros (o menos, si todavía no han nacido 3 demogorgons)
        ArrayList<Demogorgon> top3 = new ArrayList<>();
        int limite = Math.min(3, listaOrdenada.size()); // Para evitar errores si hay menos de 3
        
        for (int i = 0; i < limite; i++) {
            top3.add(listaOrdenada.get(i));
        }

        return top3;
    }

    @Override
    public String getTipoEventoActivo() throws RemoteException {
        return gestor.getNombreEventoActual();
    }

    @Override
    public int getTiempoRestanteEvento() throws RemoteException {
        return gestor.getTiempoRestante();
    }

    @Override
    public void pausarSimulacion() throws RemoteException {
        gestor.setPausado(true);
    }

    @Override
    public void reanudarSimulacion() throws RemoteException {
        gestor.setPausado(false);
    }

    // Métodos GUI pueda necesitar
    //@Override
    public int getSangreVecna() throws RemoteException {
        return sangreVecna.get();
    }

    //@Override
    public int getCapturasTotales() throws RemoteException {
        return capturasTotales.get();
    }
}
