package client;

import java.io.IOException;

import client.observer.MensajeRecibido;

public class MainCliente {

    public static void main(String[] args) {
        String host = "192.168.1.69";
        int puerto = 5000;
        MensajeRecibido publicador = new MensajeRecibido();
        HiloChatTCP recep;
        try {
            recep = new HiloChatTCP(puerto, host, publicador);
            ChatClientUI ui = new ChatClientUI(recep);
            publicador.events.subscribe("general", ui);
            new Thread(recep).start();
        } catch (IOException e) {
            System.err.println("Imposible abrir el socket...");
            e.printStackTrace();
        }

    }
}
