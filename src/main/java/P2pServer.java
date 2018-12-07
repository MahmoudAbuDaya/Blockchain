import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import static java.lang.System.exit;


public class P2pServer extends Thread{

    private static Vector < String > connectedUsers = new Vector<String>();
    private static Vector < Integer > ports = new Vector<Integer>();
    private ServerSocket serverSocket;
    private DataOutputStream writer;
    private DataInputStream reader;
    static P2pServer s = new P2pServer();

    public static P2pServer getInstance(){
        return s;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(1234);
            System.out.println("Server started running..");
            while (true) {
                acceptConnection();
                int operation = reader.read();
                if (operation == 1)          // new user
                    addNewUser();
                else if(operation == 2)     // user wants to receive the list of users
                    giveUsersList();
                else if(operation == 3)
                    givePortsList();
            }
        }
        catch (Exception e){
            System.out.println("Error in the p2p server..");
            exit(1);
        }
    }

    private void acceptConnection() {
        try {
            Socket socket = serverSocket.accept();
            writer = new DataOutputStream(socket.getOutputStream());
            reader = new DataInputStream(socket.getInputStream());
        }catch(Exception e){
            System.out.println("Error while accepting the connection..");
            exit(1);
        }
    }

    private void addNewUser() {
        try {
            String address = reader.readUTF();
            int port = reader.readInt();
            connectedUsers.add(address);
            ports.add(port);
        } catch (Exception e) {
            System.out.println("Unable to add new user to the server..");
            exit(1);
        }
    }

    private void giveUsersList() {
        try {
            writer.write(connectedUsers.size());
            for (String user : connectedUsers) writer.writeUTF(user);
        }catch(Exception e){
            System.out.println("Unable to send the list of users");
            exit(1);
        }
    }

    private void givePortsList() {
        try{
            writer.write(ports.size());
            for (Integer port : ports) writer.writeInt(port);
        }catch(Exception e){
            System.out.println("Unable to send the list of ports");
            exit(1);
        }
    }
}
