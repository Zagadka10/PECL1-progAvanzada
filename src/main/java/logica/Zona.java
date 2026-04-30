package logica;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;


public class Zona {
    private String id;
    private CopyOnWriteArrayList<Niño> niños = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Demogorgon> demos = new CopyOnWriteArrayList<>();

    public Zona(String id) {
        this.id = id;
    }
    
    // Devuelve cuántos niños hay ahora mismo para que el Demogorgon decida si atacar
    public synchronized int getNumeroNiños() { return niños.size(); }

    // Elige una víctima al azar de la lista actual
    public synchronized Niño obtenerNiñoAleatorio() {
        if (niños.isEmpty()) return null;
        int index = (int) (Math.random() * niños.size());
        return niños.get(index);
    }
    
    public void entrar(Niño n){
        niños.add(n);
    }
    
    public void salir(Niño n){
        niños.remove(n);
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
    public synchronized void entrarDemogorgon(Demogorgon d) { demos.add(d); }
    public synchronized void salirDemogorgon(Demogorgon d) { demos.remove(d); }
    
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
}
