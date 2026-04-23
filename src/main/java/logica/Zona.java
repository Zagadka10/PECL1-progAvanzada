/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logica;

import java.util.ArrayList;

/**
 *
 * @author hecto
 */
public class Zona {
    private ArrayList<Niño> niños = new ArrayList<>();
    private ArrayList<Demogorgon> demos = new ArrayList<>();
    
    public void entrar(Niño n){
        niños.add(n);
    }
    
    public void salir(Niño n){
        niños.remove(n);
    }
}
