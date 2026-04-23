package logica;
import java.util.ArrayList;

public class Zona {
    private ArrayList<Niño> niños = new ArrayList<>();
    private ArrayList<Demogorgon> demos = new ArrayList<>();

    // Gestión de Niños 
    public synchronized void entrar(Niño n) { niños.add(n); }
    public synchronized void salir(Niño n) { niños.remove(n); }
    
    // Devuelve cuántos niños hay ahora mismo para que el Demogorgon decida si atacar
    public synchronized int getNumeroNiños() { return niños.size(); }

    // Elige una víctima al azar de la lista actual
    public synchronized Niño obtenerNiñoAleatorio() {
        if (niños.isEmpty()) return null;
        int index = (int) (Math.random() * niños.size());
        return niños.get(index);
    }

    // Gestión de Demogorgons (Sincronizada) [cite: 43]
    public synchronized void entrarDemogorgon(Demogorgon d) { demos.add(d); }
    public synchronized void salirDemogorgon(Demogorgon d) { demos.remove(d); }
}