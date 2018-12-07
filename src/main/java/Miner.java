import java.util.Random;
import java.util.Vector;

class Miner extends Thread{

    private String addressOfMiner;
    private Block lastBlock;
    private BlockChain blockChain;
    private Vector < Transaction > transactions;
    private int difficulty;
    Miner(String addressOfMiner_ , BlockChain blockChain_ , Vector< Transaction > transactions_){
        addressOfMiner = addressOfMiner_;
        blockChain = blockChain_;
        lastBlock = blockChain.lastBlock();
        transactions = transactions_;
        difficulty = calculateBlockDifficulty();
    }

    private String merkleTree(int start, int end) {
        if(start == end)
            return BlockChainUtil.sha256(transactions.get(start).name);

        int mid = (start + end) / 2;
        String leftHash = merkleTree(start , mid);
        String rightHash = merkleTree(mid + 1 , end);
        return BlockChainUtil.sha256(leftHash + rightHash);
    }

    private long timeToMineLastBlock(){
        if(lastBlock.blockHeight == 0)
            return 0;
        else
            return lastBlock.timeStamp - blockChain.getBlock(blockChain.chainLength() - 2).timeStamp;
    }

    private int calculateBlockDifficulty() {
        if(lastBlock.blockHeight == 0){
            int numberOfUsers = P2pUtil.getUsersFromServer().size();
            int computingPowerPerSecond = numberOfUsers * 10000;
            return BlockChainUtil.logBase2(computingPowerPerSecond);
        }
        long time = timeToMineLastBlock();
        if(time > BlockChainUtil.TIME_TO_MINE_BLOCK)
            return lastBlock.difficulty - 1;
        else if(time < BlockChainUtil.TIME_TO_MINE_BLOCK)
            return lastBlock.difficulty + 1;
        else
            return lastBlock.difficulty;
    }

    public void run(){
        mine();
    }

    Block mine(){
        if(transactions.size() == 0)
            return null;
        String str = lastBlock.blockHash + merkleTree(0 , transactions.size() - 1);
        int mx = 0;
        while(true){
            Random rand = new Random();
            long nonce = rand.nextLong();
            if(BlockChainUtil.hashAndCountZeros(str + nonce) > mx){
                mx = BlockChainUtil.hashAndCountZeros(str + nonce);
            }
            if(BlockChainUtil.hashAndCountZeros(str + nonce) >= difficulty){
                String newBlockHash = BlockChainUtil.sha256(str + nonce);
                Block minedBlock = new Block(lastBlock.blockHeight + 1, nonce, lastBlock.blockHash, newBlockHash, difficulty, transactions, merkleTree(0 , transactions.size() - 1));
                minedBlock.transactions.add(Transaction.coinBaseTransaction(addressOfMiner));
                return minedBlock;
            }
        }
    }
}
