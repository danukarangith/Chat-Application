package lk.ijse.server;

import lk.ijse.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    private static Server server;

    private List<Client> clients = new ArrayList<>();

    private Server() throws IOException {
        serverSocket = new ServerSocket(3030);
    }

    public static Server getInstance() throws IOException {
        return server != null? server:(server=new Server());
    }

    public void makeSocket(){
        while (!serverSocket.isClosed()){
            try {
                socket = serverSocket.accept();
                Client client = new Client(socket,clients);
                clients.add(client);
                System.out.println("client socket accepted" + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
