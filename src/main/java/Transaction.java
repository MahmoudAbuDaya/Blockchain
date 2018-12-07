import java.io.*;
import java.security.PublicKey;
import java.util.Vector;

class Transaction implements Serializable {

    String name;
    private Vector < TxInput > inputs;
    String recipientAddress;
    String senderAddress;
    int amountToSend;
    int change;

    Transaction(Vector < TxInput > inputs_ , String recipientAddress_ , String senderAddress_ , int amount_){
        long timestamp = BlockChainUtil.createTimeStamp();
        inputs = inputs_;
        recipientAddress = recipientAddress_;
        senderAddress = senderAddress_;
        amountToSend = amount_;
        int inputTotal = 0;
        for (TxInput input : inputs) inputTotal = inputTotal + input.amount;
        change = inputTotal - amountToSend;
        name = BlockChainUtil.sha256(timestamp + toString());    // included the timestamp to prevent double spending
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Inputs:\n");
        for(int i=0;i<inputs.size();i++){
            str.append(i).append("\n");
            str.append(inputs.get(i).toString()).append("\n");
        }

        str.append("Outputs:\n");
        str.append("send ").append(amountToSend).append(" to ").append(recipientAddress).append("\n");
        str.append("send ").append(change).append(" to ").append(senderAddress).append("\n");

        return str.toString();
    }

    boolean isValidTx(){
        return (validOutputs() && validInputs());
    }

    private boolean validInputs(){
        for (TxInput input : inputs) {
            String message = BlockChainUtil.sha256(input.previousTx + recipientAddress);
            byte[] signature = input.signature;
            PublicKey pk = input.senderPublicKey;
            if (!BlockChainUtil.verify(signature, message, pk))
                return false;
        }
        return true;
    }

    private boolean validOutputs(){
        Vector < String > users = P2pUtil.getUsersFromServer();
        if(senderAddress.equals("mahmoud"))     // means this is a coinbase transaction
            return true;
        return (users.contains(recipientAddress) && users.contains(senderAddress) && amountToSend > 0);
    }

    static Vector < Transaction > createGiftTransactions(){
        Vector < String > users = P2pUtil.getUsersFromServer();
        Vector < Transaction > gifts = new Vector<Transaction>();
        Vector < TxInput > ins = new Vector<TxInput>();
        for (String user : users) gifts.add(new Transaction(ins, user, "mahmoud", 20));
        return gifts;
    }

    static Transaction coinBaseTransaction(String address){
        Vector < TxInput > ins = new Vector<TxInput>();
        return new Transaction(ins , address , "mahmoud" , 100);
    }
}
