import java.io.Serializable;
import java.util.Vector;

class BlockChain implements Serializable {

    private Vector < Block > chain = new Vector<Block>();

    BlockChain(){ }

    boolean addBlock(Block newBlock){
        if(this.hasBlock(newBlock.blockHash))
            return false;
        if(newBlock.blockHeight != chainLength())
            return false;
        if(newBlock.blockHeight == 0){
            chain.add(newBlock);
            return true;
        }
        if(!newBlock.isValidBlock())
            return false;
        if(!newBlock.previousBlockHash.equals(lastBlock().blockHash))
            return false;
        chain.add(newBlock);
        return true;
    }

    private boolean hasBlock(String blockName){
        for (Block block : chain)
            if (block.blockHash.equals(blockName))
                return true;
        return false;
    }

    boolean hasTransaction(Transaction tx){
        for(int i=0;i<chainLength();i++)
            if(chain.get(i).hasTransaction(tx))
                return true;
        return false;
    }

    Block lastBlock(){
        if(chain.size() == 0)
            return null;
        else
            return chain.lastElement();
    }

    Block getBlock(int blockHeight){
        if(blockHeight <= chain.size())
            return chain.get(blockHeight);
        else
            return null;
    }

    int chainLength(){
        return chain.size();
    }

    boolean isValidBlockChain(){
        for(int i=1;i<chainLength();i++){
            Block lastBlock = getBlock(i-1);
            Block currentBlock = getBlock(i);
            if(!currentBlock.previousBlockHash.equals(lastBlock.blockHash))
                return false;
            if(!currentBlock.blockHash.equals(currentBlock.hashBlock()))
                return false;
        }
        return true;
    }
}
