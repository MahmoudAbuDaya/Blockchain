import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import static java.lang.System.exit;

class P2pUtil {
    private DataOutputStream writer;
    private DataInputStream reader;

    private P2pUtil(){}

    private void connectToServer(){
        try {
            Socket socket = new Socket("localhost", 1234);
            writer = new DataOutputStream(socket.getOutputStream());
            reader = new DataInputStream(socket.getInputStream());
        }catch(Exception e){
            System.out.println("Error connecting to the server..");
            exit(1);
        }
    }

    static void registerInTheServer(String address , int port) {
        try{
            P2pUtil s = new P2pUtil();
            s.connectToServer();
            s.writer.write(1);
            s.writer.writeUTF(address);
            s.writer.writeInt(port);
        }catch(Exception e){
            System.out.println("Unable to connect to the server..");
            exit(1);
        }
    }

    static Vector< String > getUsersFromServer(){
        Vector < String > users = new Vector<String>();
        try {
            P2pUtil s = new P2pUtil();
            s.connectToServer();
            s.writer.write(2);
            int numberOfUsers = s.reader.read();
            for (int i = 0; i < numberOfUsers; i++)
                users.add(s.reader.readUTF());
        }catch(Exception e){
            System.out.println("Error getting the list of users..");
            exit(1);
        }
        return users;
    }

    private static Vector < Integer > getPortsFromServer(){
        Vector < Integer > ports = new Vector< Integer >();
        try{
            P2pUtil s = new P2pUtil();
            s.connectToServer();
            s.writer.write(3);
            int numberOfPorts = s.reader.read();
            for(int i=0;i<numberOfPorts;i++)
                ports.add(s.reader.readInt());
        }catch(Exception e){
            System.out.println("Error getting the list of ports..");
            exit(1);
        }
        return ports;
    }

    static void broadcastTransaction(Transaction newTx , int broadcasterPort){
        broadcastAnObject(1 , newTx , broadcasterPort);
    }

    static void broadcastBlockChain(BlockChain newChain , int broadcasterPort){
       broadcastAnObject(2 , newChain , broadcasterPort);
    }

    private static void broadcastAnObject(int objectKind , Object objectToBroadcast , int broadcasterPort){
        Vector < Integer > ports = getPortsFromServer();
        for (Integer port : ports) {
            if (port == broadcasterPort)
                continue;
            try {
                Socket socket = new Socket("localhost", port);
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream objectWriter = new ObjectOutputStream(socket.getOutputStream());
                writer.write(objectKind);       // objectKind = 1 for Transaction object, objectKind = 2 for BlockChain object
                objectWriter.writeObject(objectToBroadcast);
            } catch (Exception e) {
                System.out.println("Error broadcasting an object");
                exit(1);
            }
        }
    }
}
