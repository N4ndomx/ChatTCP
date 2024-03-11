package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import client.observer.MensajeRecibido;

public class HiloChatTCP implements Runnable {

    private InputStream inputStream;
    private OutputStream outputStream;
    private MensajeRecibido mensajeRec;
    private Socket socket;

    public HiloChatTCP(int puerto, String host, MensajeRecibido mensajeRecibido) throws IOException {
        socket = new Socket(host, puerto);
        this.inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.mensajeRec = mensajeRecibido;
    }

    public void sendMessage(List<String> paqueteMsg) {

        for (String string : paqueteMsg) {
            byte[] mensajeBytes = string.getBytes();
            try {
                outputStream.write(mensajeBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void desconectar() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] mensaje = new byte[1024];
                int bytesLeidos = inputStream.read(mensaje);

                String mensajeTexto = new String(mensaje, 0, bytesLeidos);
                mensajeRec.recepcionMsg("G", mensajeTexto, socket.getInetAddress().getHostName());
                System.out.println(mensajeTexto);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}