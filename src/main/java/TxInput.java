import java.io.Serializable;
import java.security.PublicKey;

class TxInput implements Serializable {

    PublicKey senderPublicKey;
    byte[] signature;
    String previousTx;
    int amount;

    TxInput(PublicKey senderPublicKey_ , byte[] signature_ , String previousTx_ , int amount_){
        senderPublicKey = senderPublicKey_;
        signature  = signature_;
        previousTx = previousTx_;
        amount = amount_;
    }
}
