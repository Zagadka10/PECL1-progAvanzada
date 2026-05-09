package logica;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        // Inicializamos el sistema de logs 
        HawkinsLog log = new HawkinsLog();
        log.escribir("INICIA LA BATALLA DE HAWKINS");

        // ZONAS SEGURAS (Hawkins)
        Zona callePrincipal = new Zona("Calle Principal");
        Zona sotanoByers = new Zona("Sótano Byers");
        Zona radioWSQK = new Zona("Radio WSQK");

        // ZONAS INSEGURAS (Upside Down)
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

        Portal[] portales = new Portal[4];

        // --- GESTOR DE EVENTOS Y SANGRE ---
        AtomicInteger sangreVecna = new AtomicInteger(0);
        GestorEventos gestor = new GestorEventos(log, colmena, callePrincipal, sangreVecna, portales);

        // PORTALES (Cambio de formato porque gestor argumento de portal)
        // Asignamos las capacidades exactas que pide el enunciado para cada grupo
        Portal portalBosque = new Portal("Portal Bosque", bosque, 2, log, gestor);
        Portal portalLab = new Portal("Portal Laboratorio", laboratorio, 3, log, gestor);
        Portal portalCentro = new Portal("Portal C. Comercial", centroComercial, 4, log, gestor);
        Portal portalAlcantarilla = new Portal("Portal Alcantarillado", alcantarillado, 2, log, gestor);

        portales[0] = portalBosque;
        portales[1] = portalLab;
        portales[2] = portalCentro;
        portales[3] = portalAlcantarilla;

        AtomicInteger capturasTotales = new AtomicInteger(0);

        ArrayList<Zona> todasLasZonas = new ArrayList<>();
        todasLasZonas.add(callePrincipal);
        todasLasZonas.add(sotanoByers);
        todasLasZonas.add(radioWSQK);
        todasLasZonas.add(bosque);
        todasLasZonas.add(laboratorio);
        todasLasZonas.add(centroComercial);
        todasLasZonas.add(alcantarillado);
        todasLasZonas.add(colmena);
        
        CopyOnWriteArrayList<Demogorgon> listaDemogorgons = new CopyOnWriteArrayList<>();

        //Para RMI
        try {
            // Creamos la instancia del objeto remoto (el "recepcionista")
            Servidor srv = new Servidor(todasLasZonas, portales, gestor, sangreVecna, capturasTotales, listaDemogorgons);

            // Arrancamos el registro RMI en el puerto 1099
            LocateRegistry.createRegistry(1099);

            // Registramos el objeto con un nombre para que el cliente lo encuentre
            Naming.rebind("//localhost/ObjetoHawkins", srv);

            log.escribir(">>> SERVIDOR RMI: Objeto distribuido registrado correctamente en puerto 1099");
        } catch (Exception e) {
            log.escribir("!!! ERROR RMI: No se pudo registrar el servidor: " + e.getMessage());
            e.printStackTrace();
        }

        gestor.start();

        // DEMOGORGON ALPHA
        Demogorgon alpha = new Demogorgon("D0000", zonasUpsideDown, colmena, log, gestor, capturasTotales);
        alpha.start();
        log.escribir("Vecna ha soltado al Demogorgon Alpha");
        //Lista para  estadisticas de clase cliente.
        listaDemogorgons.add(alpha);

        // GENERACIÓN ESCALONADA DE NIÑOS
        // El sistema debe generar 1.500 niños
        for (int i = 1; i <= 1500; i++) {
            
            Niño n = new Niño(String.valueOf(i), sotanoByers, callePrincipal, radioWSQK, colmena, portales, log, gestor, sangreVecna);
            n.start();

            try {
                // Intervalo aleatorio de entre 0,5 y 2 segundos (500 a 2000 milisegundos)
                long tiempoEspera = 500 + (long) (Math.random() * 1501);
                Thread.sleep(tiempoEspera);
            } catch (InterruptedException e) {
                System.out.println("Interrupción en la creación de niños: " + e.getMessage());
            }
        }

        log.escribir("Todos los niños se han generado.");
    }
}
