import java.io.Serializable;
import java.util.Vector;

public class Block implements Serializable {
    int blockHeight;
    long timeStamp;
    private long nonce;
    String previousBlockHash;
    String blockHash;
    Vector < Transaction > transactions;
    private String merkleRoot;
    int difficulty;
    boolean unlocked;

    Block(int blockHeight_ , long nonce_ , String previousBlockHash_ , String currentBlockHash_ , int difficulty_ , Vector < Transaction > transactions_ , String merkleRoot_){
        blockHeight = blockHeight_;
        timeStamp = BlockChainUtil.createTimeStamp();
        nonce = nonce_;
        previousBlockHash = previousBlockHash_;
        blockHash = currentBlockHash_;
        difficulty = difficulty_;
        transactions = transactions_;
        unlocked = false;
        merkleRoot = merkleRoot_;
    }

    static Block createGenesisBlock(){
        long nonce = 0;
        String previousBlockHash = BlockChainUtil.sha256("Mahmoud");
        String currentBlockHash = BlockChainUtil.sha256("Abu Daya");
        int difficulty = 0;
        Vector < Transaction > txs = Transaction.createGiftTransactions();
        return new Block(0 , nonce , previousBlockHash , currentBlockHash , difficulty , txs , BlockChainUtil.sha256("0"));
    }

    boolean isValidBlock(){
        if(blockHeight == 0)
            return true;
        if(transactions.size() == 0)
            return false;
        if(!blockHash.equals(hashBlock()))
            return false;
        for (int i=0;i<transactions.size()-1;i++)
            if (!transactions.get(i).isValidTx())
                return false;
        return true;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("Block Height:  ").append(blockHeight).append("\n");
        str.append("Block hash:  ").append(blockHash).append("\n");
        str.append("Previous bloch hash:  ").append(previousBlockHash).append("\n");
        str.append("Merkle root:  ").append(merkleRoot).append("\n");
        str.append("Time Stamp:  ").append(timeStamp).append("\n");
        str.append("Number of transactions:  ").append(transactions.size()).append("\n");
        return str.toString();
    }

    String hashBlock(){
        if(blockHeight == 0)
            return BlockChainUtil.sha256("Abu Daya");
        else
            return BlockChainUtil.sha256(previousBlockHash + merkleRoot + nonce);
    }

    boolean hasTransaction(Transaction tx){
        for (Transaction transaction : transactions)
            if (transaction.name.equals(tx.name))
                return true;
        return false;
    }
}
