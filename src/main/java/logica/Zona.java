package logica;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

public class Zona {

    private final String id;
    private final CopyOnWriteArrayList<Niño> niños = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Demogorgon> demos = new CopyOnWriteArrayList<>();

    public Zona(String id) {
        this.id = id;
    }
    
    //método para servidor
    public synchronized int getNumeroDemogorgons() {
        return demos.size();
    }
    
    // Devuelve cuántos niños hay ahora mismo para que el Demogorgon decida si atacar
    public synchronized int getNumeroNiños() {
        return niños.size();
    }

    // Elige una víctima al azar de la lista actual
    public synchronized Niño obtenerNiñoAleatorio() {
        if (niños.isEmpty()) {
            return null;
        }
        int index = (int) (Math.random() * niños.size());
        return niños.get(index);
    }

    public void entrar(Niño n) {
        niños.add(n);
    }

    public boolean salir(Niño n) {
       return niños.remove(n);
    }

    public void entrar(Demogorgon d) {
        demos.add(d);
    }

    public void salir(Demogorgon d) {
        demos.remove(d);
    }

    public String getId() {
        return id;
    }

    // Gestión de Demogorgons 
    public synchronized void entrarDemogorgon(Demogorgon d) {
        demos.add(d);
    }

    public synchronized void salirDemogorgon(Demogorgon d) {
        demos.remove(d);
    }

    // Método para que Eleven saque niños de la Colmena
    public synchronized ArrayList<Niño> rescatarNiños(int maximo) {
        ArrayList<Niño> rescatados = new ArrayList<>();
        int cantidad = Math.min(maximo, niños.size());
        for (int i = 0; i < cantidad; i++) {
            // remove(0) va sacando siempre al primero de la lista
            rescatados.add(niños.remove(0));
        }
        return rescatados;
    }
    // Devuelve un texto con los IDs de los niños 
    public synchronized String getListaIdsNinos() {
        if (niños.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < niños.size(); i++) {
            sb.append(niños.get(i).getIdNino());
            if (i < niños.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    // Devuelve un texto con los IDs de los demogorgons 
    public synchronized String getListaIdsDemos() {
        if (demos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < demos.size(); i++) {
            sb.append(demos.get(i).getIdentificador());
            if (i < demos.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
