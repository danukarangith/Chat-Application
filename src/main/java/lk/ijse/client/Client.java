package lk.ijse.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    private Socket socket;
    private List<Client> clients;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String message = "";

    public Client(Socket socket, List<Client> clients) {
        try {
            this.socket = socket;
            this.clients = clients;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        while (socket.isConnected()){
                            message = dataInputStream.readUTF();
                           for (Client client : clients) {
                              if (client.socket.getPort() != socket.getPort()) {
                                client.dataOutputStream.writeUTF(message);
                                client.dataOutputStream.flush();
                              }
                           }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }).start();
    }
}
