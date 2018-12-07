import java.util.Vector;

public class Main {
    public static void main(String[] args){
        startServer();
        runGUI();
        int numberOfUsers = 5;
        createUsers(numberOfUsers);
    }

    private static void startServer() {
        P2pServer server = P2pServer.getInstance();
        server.start();
    }

    private static void runGUI(){
        GUI gui = new GUI();
        gui.setSize(1000 , 700);
        gui.setVisible(true);
    }

    private static void createUsers(int numberOfUsers){
        int portNumber = 3000;
        Vector < Node > blockchainUsers = new Vector<Node>();
        for(int i=0;i<numberOfUsers;i++){
            blockchainUsers.add(new Node(portNumber));
            portNumber = portNumber + 1;
        }
        GUI.logTo(1 , "Genesis block has been created! \n" + Block.createGenesisBlock().toString() + "\n\n");
        startUsers(blockchainUsers);
    }

    private static void startUsers(Vector < Node > users){
        for (Node user : users) {
            user.start();
        }
    }
}
