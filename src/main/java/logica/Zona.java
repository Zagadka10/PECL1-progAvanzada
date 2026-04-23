package logica;
import java.util.ArrayList;

public class Zona {
<<<<<<< HEAD
    private String id;
    //Nota: Si el programa va lento, sobre todo en calle principal usar ConcurrentHashMap.newKeySet()
    private CopyOnWriteArrayList<Niño> niños = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Demogorgon> demos = new CopyOnWriteArrayList<>();

    public Zona(String id) {
        this.id = id;
    }
=======
    private ArrayList<Niño> niños = new ArrayList<>();
    private ArrayList<Demogorgon> demos = new ArrayList<>();

    // Gestión de Niños 
    public synchronized void entrar(Niño n) { niños.add(n); }
    public synchronized void salir(Niño n) { niños.remove(n); }
>>>>>>> 880138890e59a9347df0fc1559159d92ad96f6d6
    
    // Devuelve cuántos niños hay ahora mismo para que el Demogorgon decida si atacar
    public synchronized int getNumeroNiños() { return niños.size(); }

    // Elige una víctima al azar de la lista actual
    public synchronized Niño obtenerNiñoAleatorio() {
        if (niños.isEmpty()) return null;
        int index = (int) (Math.random() * niños.size());
        return niños.get(index);
    }
<<<<<<< HEAD
    
    public void salir(Niño n){
        niños.remove(n);
    }
    
    public void entrar(Demogorgon d) {
        demos.remove(d);
    }

    public void salir(Demogorgon d) {
        demos.remove(d);
    }
    
}
=======

    // Gestión de Demogorgons (Sincronizada) [cite: 43]
    public synchronized void entrarDemogorgon(Demogorgon d) { demos.add(d); }
    public synchronized void salirDemogorgon(Demogorgon d) { demos.remove(d); }
}
>>>>>>> 880138890e59a9347df0fc1559159d92ad96f6d6
