/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cliente;

import java.rmi.Naming;
import comun.InterfazHawkins;

/**
 *
 * @author hecto
 */
public class MainCliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Buscando conexión con el Laboratorio Hawkins...");

        try {
            // Localizamos el objeto en la red
            InterfazHawkins servidor = (InterfazHawkins) Naming.lookup("//localhost/ObjetoHawkins");
            System.out.println("¡Conexión establecida! Abriendo interfaz gráfica...");

            // Abrimos la ventana pasándole el acceso al servidor
            ClienteGUI ventana = new ClienteGUI(servidor);
            ventana.setVisible(true);

        } catch (Exception e) {
            System.out.println("!!! Error de conexión: No se pudo contactar con el Servidor.");
            e.printStackTrace();
        }
    }
}
