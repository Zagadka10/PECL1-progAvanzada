package logica;

import java.util.concurrent.CopyOnWriteArrayList;


public class Zona {
    private String id;
    //Nota: Si el programa va lento, sobre todo en calle principal usar ConcurrentHashMap.newKeySet()
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
    
    // Gestión de Demogorgons (Sincronizada) [cite: 43]
    public synchronized void entrarDemogorgon(Demogorgon d) { demos.add(d); }
    public synchronized void salirDemogorgon(Demogorgon d) { demos.remove(d); }
}
