package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;
    private Map<String, Socket> clientes;

    public Server(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        clientes = new HashMap<>();
    }

    public void iniciar() throws IOException {
        logger.info("Servidor iniciado en el puerto " + serverSocket.getLocalPort());
        while (true) {
            Socket cliente = serverSocket.accept();
            logger.info("Nuevo cliente conectado: " + cliente.getInetAddress().getHostAddress());
            clientes.put(cliente.getInetAddress().getHostAddress(), cliente);
            new Thread(new HiloGestorCliente(cliente)).start();
        }
    }

    private class HiloGestorCliente implements Runnable {

        private Socket cliente;
        private InputStream inputStream;
        private OutputStream outputStream;
        private volatile boolean conectado;

        public HiloGestorCliente(Socket cliente) throws IOException {
            this.cliente = cliente;
            this.inputStream = cliente.getInputStream();
            this.outputStream = cliente.getOutputStream();
            conectado = true;
        }

        @Override
        public void run() {
            try {
                while (conectado) {
                    byte[] mensaje = new byte[1024];
                    int bytesLeidos = inputStream.read(mensaje);

                    if (bytesLeidos == -1) {
                        // El cliente cerró la conexión
                        break;
                    }

                    String comandoTxt = new String(mensaje, 0, bytesLeidos);

                    if (comandoTxt.equals("NU")) {
                        int bytesLeidos2 = inputStream.read(mensaje);
                        String username = new String(mensaje, 0, bytesLeidos2);
                        broadcast(username + " se ha unido al chat");
                    } else if (comandoTxt.equals("G")) {
                        int bytesLeidos2 = inputStream.read(mensaje);
                        String mensajeGeneral = new String(mensaje, 0, bytesLeidos2);
                        broadcast(mensajeGeneral);
                    } else if (comandoTxt.equals("U")) {
                        getAllUser();
                    }
                }
            } catch (IOException e) {
                conectado = false;
                try {
                    desconectar();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public void desconectar() throws IOException {
            clientes.remove(cliente.getInetAddress().getHostAddress());
            broadcast(cliente.getInetAddress().getHostAddress() + " salió del chat");
            outputStream.close();
            inputStream.close();
            cliente.close();
            logger.info("Cliente desconectado: " + cliente.getInetAddress().getHostAddress());
        }

        public void broadcast(String mensaje) throws IOException {
            byte[] bytesMsg = mensaje.getBytes("UTF-8");
            for (String clave : clientes.keySet()) {
                Socket clienteActual = clientes.get(clave);
                outputStream = clienteActual.getOutputStream();
                outputStream.write(bytesMsg);
            }
        }

        public void getAllUser() throws IOException {
            String strClaves = "#";
            for (String clave : clientes.keySet()) {
                strClaves = strClaves + clave + "#";
            }
            byte[] bytesclave = strClaves.getBytes("UTF-8");
            outputStream.write(bytesclave);
        }
    }

    public static void main(String[] args) throws IOException {
        int puerto = 5000;
        Server servidor = new Server(puerto);
        servidor.iniciar();
    }
}
