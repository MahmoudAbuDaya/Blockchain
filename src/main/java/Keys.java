import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;

import static java.lang.System.exit;

public class Keys {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    String address;

    Keys(){
        KeyPair publicAndPrivateKeys = generateRandomKeys();
        privateKey = publicAndPrivateKeys.getPrivate();
        publicKey = publicAndPrivateKeys.getPublic();
        address = generateAddress();
    }

    PublicKey getPublicKey() {
        return publicKey;
    }

    PrivateKey getPrivateKey() {
        return privateKey;
    }

    private KeyPair generateRandomKeys() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC"); // elliptic curve digital signature
            SecureRandom random = new SecureRandom();
            generator.initialize(256, random);
            return generator.generateKeyPair();
        }catch(NoSuchAlgorithmException e){
            System.out.println("Error while generating the keypair for the user.");
            exit(1);
        }
        return null; // This will never happen
    }

    private String generateAddress(){
        String compressedPublicKey = compressKey(publicKey);
        String hashedPublicKey = BlockChainUtil.sha256(compressedPublicKey);
        return BlockChainUtil.toBase58(hashedPublicKey).substring(0,25);
    }

    private String compressKey(PublicKey pubKey) {
        BigInteger x = ((ECPublicKey)pubKey).getW().getAffineX();
        BigInteger y = ((ECPublicKey)pubKey).getW().getAffineY();
        String compressedKey;
        if(isEvenBigInteger(y))
            compressedKey = "02" + x.toString();
        else
            compressedKey = "03" + x.toString();
        return compressedKey;
    }

    private boolean isEvenBigInteger(BigInteger y) {
        BigInteger two = new BigInteger("2");
        BigInteger zero = new BigInteger("0");
        return (y.mod(two).equals(zero));
    }
}
