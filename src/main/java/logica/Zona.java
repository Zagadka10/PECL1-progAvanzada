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
    private CopyOnWriteArrayList<Niño> niños = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Demogorgon> demos = new CopyOnWriteArrayList<>();
    
    public void entrar(Niño n){
        niños.add(n);
    }
    
    public void salir(Niño n){
        niños.remove(n);
    }
    
}
