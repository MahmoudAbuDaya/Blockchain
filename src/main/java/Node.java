import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Node extends Thread{

    class Outputs {
        Outputs(String txName_ , int amount_){
            txName = txName_;
            amount = amount_;
        }
        String txName;
        int amount;
    }

    private Keys keys;
    private String address;
    private Vector < Outputs > outputs = new Vector<Outputs>();
    private Vector < Transaction > transactions = new Vector<Transaction>();
    private BlockChain blockChain = new BlockChain();
    private int myPort;

    Node(int myPort_) {
        keys = new Keys();
        address = keys.address;
        myPort = myPort_;
        P2pUtil.registerInTheServer(address , myPort);
        GUI.logTo(2 , "User with address " + address + " and port " + myPort_ + " added to the server and received 20 coins as a gift\n\n");
    }

    public void run(){
        blockChain.addBlock(Block.createGenesisBlock());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(50);
        executor.scheduleAtFixedRate(unlockTransactions, 0, 5, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(sendMoneyToRandomPerson, 0, 2, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(mine, 1, 5, TimeUnit.SECONDS);
        PeerServer();
    }

    private void PeerServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(myPort);
            while(true){
                Socket socket = serverSocket.accept();
                DataInputStream reader = new DataInputStream(socket.getInputStream());
                ObjectInputStream objectReader = new ObjectInputStream(socket.getInputStream());
                int operation = reader.read();
                if(operation == 1){     // receive a transaction
                    Transaction newTx = (Transaction) objectReader.readObject();
                    transactions.add(newTx);
                }
                else if(operation == 2){        //receive a chain
                    BlockChain newChain = (BlockChain) objectReader.readObject();
                    if(newChain.isValidBlockChain() && newChain.chainLength() > blockChain.chainLength())
                        blockChain = newChain;
                }
            }
        }catch(Exception e){
            System.out.println("Error in the P2P server");
        }
    }

    private void sendMoneyTo(String addressOfRecipient , int amount){
        if(amount <= 0){
            System.out.println("Invalid amount to send");
            return;
        }
        if(calculateBalance() < amount){
            System.out.println("Don't have enough money to send " + amount + " to " + addressOfRecipient);
            return;
        }
        Vector < Outputs > selected = selectInputs(amount);
        Vector < TxInput > createdInputs = createInputs(selected , addressOfRecipient);
        Transaction newTx = new Transaction(createdInputs , addressOfRecipient , this.address , amount);
        transactions.add(newTx);
        P2pUtil.broadcastTransaction(newTx , myPort);
    }

    private Vector < TxInput > createInputs(Vector<Outputs> selected , String addressOfRecipient) {
        Vector < TxInput > createdInputs = new Vector<TxInput>();

        for(int i=0;i<selected.size();i++) {
            String stringToSign = selected.get(i).txName + addressOfRecipient;
            stringToSign = BlockChainUtil.sha256(stringToSign);
            byte[] signature = BlockChainUtil.sign(stringToSign , keys.getPrivateKey());
            createdInputs.add(new TxInput(keys.getPublicKey() , signature, selected.get(i).txName , selected.get(i).amount));
        }
        return createdInputs;
    }

    private int calculateBalance(){
        int balance = 0;
        for (Outputs output : outputs) balance = balance + output.amount;
        return balance;
    }

    private Vector < Outputs > selectInputs(int need){
        int have = 0;
        Vector<Outputs> selected = new Vector<Outputs>();
        while (have < need) {
            have = have + outputs.get(0).amount;
            selected.add(outputs.get(0));
            outputs.remove(0);
        }
        return selected;
    }

    private void takeMoneyIfMine(Block block){
        for(int i=0;i<block.transactions.size();i++){
            if(block.transactions.get(i).recipientAddress.equals(address)) {
                String txName = block.transactions.get(i).name;
                int amountToGet = block.transactions.get(i).amountToSend;
                outputs.add(new Outputs(txName , amountToGet));
            }
            if(block.transactions.get(i).senderAddress.equals(address)){
                String txName = block.transactions.get(i).name;
                int amountToGet = block.transactions.get(i).change;
                if(amountToGet > 0)
                    outputs.add(new Outputs(txName , amountToGet));
            }
        }
        GUI.logTo(2 , "Current balance of " + address + " is: " + calculateBalance() + "\n\n");
    }

    private String getRandomUser(){
        Vector < String > usersList = P2pUtil.getUsersFromServer();
        int userIndex = usersList.size();
        while(userIndex >= usersList.size() || usersList.get(userIndex).equals(address)){  // while generated random user == me
            Random rand = new Random();
            userIndex = rand.nextInt(usersList.size());
        }
        return usersList.get(userIndex);
    }

    private void cleanTransactionsList(){
        Vector < Transaction > cleanedTxList = new Vector<Transaction>();
        for (Transaction transaction : transactions) {
            if (!blockChain.hasTransaction(transaction))
                cleanedTxList.add(transaction);
        }
        transactions = cleanedTxList;
    }

    private Runnable unlockTransactions = new Runnable() {
        public void run() {
            if (blockChain.chainLength() > 0)
                for (int i = 0; i < blockChain.chainLength(); i++) {
                    if (!blockChain.getBlock(i).unlocked) {
                        blockChain.getBlock(i).unlocked = true;
                        takeMoneyIfMine(blockChain.getBlock(i));
                    }
                }
        }
    };

    private Runnable sendMoneyToRandomPerson = new Runnable() {
        public void run() {
            if(calculateBalance() == 0)
                return;
            if(BlockChainUtil.randomNumberBelow(2) == 0)    // 50% chance to send money each time
                return;
            String recipient = getRandomUser();
            int amountToSend = BlockChainUtil.randomNumberBelow(calculateBalance()) + 1;
            GUI.logTo(2 , address + " sent " + amountToSend + " to " + recipient + "\n\n");
            sendMoneyTo(recipient , amountToSend);
        }
    };

    private Runnable mine = new Runnable() {
        public void run() {
            cleanTransactionsList();
            if(blockChain.lastBlock() == null)
                return;
            Miner miner = new Miner(address , blockChain , transactions);
            Block minedBlock = miner.mine();
            if(minedBlock != null && blockChain.addBlock(minedBlock)){
                GUI.logTo(2, "User " + address + " found a block and received 100 as a reward!\n\n");
                GUI.logTo(1, "A new block has been found by address " + address + "\n" + minedBlock.toString() + "\n\n");
                P2pUtil.broadcastBlockChain(blockChain , myPort);
            }
        }
    };
}
