/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cliente;

import java.rmi.Naming;
import comun.InterfaceHawkins;

/**
 *
 * @author hecto
 */
public class MainCliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Iniciando terminal del Gobierno...");
        System.out.println("Buscando conexión con el Laboratorio Hawkins...");

        try {
            // 1. Localizamos el objeto distribuido en la red usando nuestra interfaz común
            InterfaceHawkins servidor = (InterfaceHawkins) Naming.lookup("//localhost/ObjetoHawkins");
            
            System.out.println("¡Conexión establecida con éxito!\n");

            // 2. Le hacemos un par de preguntas al servidor en tiempo real
            System.out.println("--- DATOS CLASIFICADOS ---");
            System.out.println("Sangre recolectada por Vecna: " + servidor.getSangreVecna());
            System.out.println("Demogorgons creados: " + servidor.getCapturasTotales());
            System.out.println("Niños detectados en el Bosque: " + servidor.getNumeroNinosEnZona("Bosque"));
            System.out.println("¿Hay Apagón general?: " + (servidor.isApagonActivo() ? "SÍ" : "NO"));
            
            System.out.println("\nSi ves estos datos, la arquitectura RMI funciona perfectamente.");

        } catch (Exception e) {
            System.out.println("!!! Error de conexión: No se pudo contactar con el Servidor.");
            System.out.println("Asegúrate de que el Servidor (Main) está ejecutándose primero.");
            e.printStackTrace();
        }
    }
    
}
