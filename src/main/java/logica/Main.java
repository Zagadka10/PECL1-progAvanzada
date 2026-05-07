package logica;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        
        // Inicializamos el sistema de logs 
        HawkinsLog log = new HawkinsLog();
        log.escribir("INICIA LA BATALLA DE HAWKINS");

        // 1. ZONAS SEGURAS (Hawkins)
        Zona callePrincipal = new Zona("Calle Principal");
        Zona sotanoByers = new Zona("Sótano Byers");
        Zona radioWSQK = new Zona("Radio WSQK");

        // 2. ZONAS INSEGURAS (Upside Down)
        Zona bosque = new Zona("Bosque");
        Zona laboratorio = new Zona("Laboratorio");
        Zona centroComercial = new Zona("Centro Comercial");
        Zona alcantarillado = new Zona("Alcantarillado");
        Zona colmena = new Zona("Colmena");

        // Agrupamos las zonas inseguras en una lista para pasársela a los demogorgons
        ArrayList<Zona> zonasUpsideDown = new ArrayList<>();
        zonasUpsideDown.add(bosque);
        zonasUpsideDown.add(laboratorio);
        zonasUpsideDown.add(centroComercial);
        zonasUpsideDown.add(alcantarillado);

        // 3. PORTALES
        // Asignamos las capacidades exactas que pide el enunciado para cada grupo
        Portal portalBosque = new Portal("Portal Bosque", bosque, 2, log);
        Portal portalLab = new Portal("Portal Laboratorio", laboratorio, 3, log);
        Portal portalCentro = new Portal("Portal C. Comercial", centroComercial, 4, log);
        Portal portalAlcantarilla = new Portal("Portal Alcantarillado", alcantarillado, 2, log);

        Portal[] portales = {portalBosque, portalLab, portalCentro, portalAlcantarilla};
        
        // --- GESTOR DE EVENTOS Y SANGRE ---
        AtomicInteger sangreVecna = new AtomicInteger(0);
        GestorEventos gestor = new GestorEventos(log, colmena, callePrincipal, sangreVecna);
        gestor.start();

        // 4. DEMOGORGON ALPHA
        // ¡CORREGIDO! Creamos el primer demogorgon pasándole el 'gestor'
        Demogorgon alpha = new Demogorgon("D0000", zonasUpsideDown, colmena, log, gestor);
        alpha.start();
        log.escribir("Vecna ha soltado al Demogorgon Alpha");

        // 5. GENERACIÓN ESCALONADA DE NIÑOS
        // El sistema debe generar 1.500 niños
        for (int i = 1; i <= 1500; i++) {
            // ¡CORREGIDO! Pasamos 'gestor' y 'sangreVecna' al final del constructor
            Niño n = new Niño(String.valueOf(i), sotanoByers, callePrincipal, radioWSQK, colmena, portales, log, gestor, sangreVecna);
            n.start();

            try {
                // Intervalo aleatorio de entre 0,5 y 2 segundos (500 a 2000 milisegundos)
                long tiempoEspera = 500 + (long)(Math.random() * 1501);
                Thread.sleep(tiempoEspera);
            } catch (InterruptedException e) {
                System.out.println("Interrupción en la creación de niños: " + e.getMessage());
            }
        }
        
        log.escribir("Todos los niños se han generado.");
    }
}