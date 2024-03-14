/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.servidor5;

import java.io.*;
import java.net.*;

public class Servidor5 {
    public static void main(String[] args) {
        crearArchivoUsuariosSiNoExiste();
        
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Servidor en línea. Esperando conexiones...");
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket);
                ClienteHandler clienteHandler = new ClienteHandler(socket);
                Thread thread = new Thread(clienteHandler);
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void crearArchivoUsuariosSiNoExiste() {
        File archivoUsuarios = new File("usuarios.txt");
        if (!archivoUsuarios.exists()) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(archivoUsuarios));
                writer.write("anthony1,prueba1\n");
                writer.write("anthony2,prueba2\n");
                writer.write("anthony3,prueba3\n");
                writer.close();
                System.out.println("Archivo usuarios.txt creado con usuarios y contraseñas predeterminados.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class ClienteHandler implements Runnable {
    private final Socket socket;
    
    public ClienteHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter salida = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            String usuario = entrada.readLine();
            String contrasena = entrada.readLine();

            if (verificarCredenciales(usuario, contrasena)) {
                salida.write("Inicio de sesión confirmado\n");
            } else {
                salida.write("Error: Las credenciales son inválidas\n");
            }
            
            salida.flush();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private boolean verificarCredenciales(String usuario, String contrasena) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("usuarios.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(usuario) && parts[1].equals(contrasena)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}