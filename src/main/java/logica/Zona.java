/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author hecto
 */
public class Zona {
    private String id;
    //Nota: Si el programa va lento, sobre todo en calle principal usar ConcurrentHashMap.newKeySet()
    private CopyOnWriteArrayList<Niño> niños = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Demogorgon> demos = new CopyOnWriteArrayList<>();

    public Zona(String id) {
        this.id = id;
    }
    
    public void entrar(Niño n){
        niños.add(n);
    }
    
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
