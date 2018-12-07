import java.math.BigInteger;
import java.security.*;
import java.util.Date;
import java.util.Random;

import static java.lang.System.exit;


class BlockChainUtil {

    static int TIME_TO_MINE_BLOCK = 10000;   // 10 seconds

    static String sha256(String stringToHash){
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(stringToHash.getBytes("UTF-8"));
        }catch(Exception e) {
            System.out.println("Error in the sha256 hashing algorithm");
            exit(1);
        }
        return byteToHex(hash);
    }

    private static String byteToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            // hex = first 4 bits converted to hexadecimal + last 4 bits converted to hexadecimal
            String hex = Integer.toHexString((0xf0 & b) / 16) + Integer.toHexString(0xf & b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    static String toBase58(String str){
        String code = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        BigInteger num = new BigInteger(str , 16);  // 16 because the number is in hexadecimal
        BigInteger zero = new BigInteger("0");
        BigInteger fiftyEight = new BigInteger("58");
        StringBuilder finalAddress = new StringBuilder();
        while(num.compareTo(zero) == 1){    // while(num > 0)
            int index = num.mod(fiftyEight).intValue();
            finalAddress.append(code.charAt(index));
            num = num.divide(fiftyEight);
        }
        return finalAddress.toString();
    }

    static byte[] sign(String message , PrivateKey privateKey){
        try {
            Signature signature = Signature.getInstance("SHA1withECDSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        }catch (Exception e){
            System.out.println("Error while signing the message..");
            exit(1);
        }
        return null;    // will never happen
    }

    static boolean verify(byte[] signedMessage , String message , PublicKey publicKey){
        try {
            Signature signature = Signature.getInstance("SHA1withECDSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());
            return signature.verify(signedMessage);
        }catch(Exception e){
            System.out.println("Error while verifying the signature..");
            exit(1);
        }
        return false;    // will never happen
    }

    static long createTimeStamp(){
        Date date = new Date();
        return date.getTime();
    }

    static int logBase2(long num){
        int log = 0;
        while(num > 0){
            num = num / 2;
            log = log + 1;
        }
        return log;
    }

    static int randomNumberBelow(int num){
        Random rand = new Random();
        return rand.nextInt(num);
    }

    private static String byteToBinary(byte[] hash){
        StringBuilder str = new StringBuilder();
        for (byte aHash : hash) {
            str.append(String.format("%8s", Integer.toBinaryString(aHash & 0xFF)).replace(' ', '0'));
        }
        return str.toString();
    }

    static int hashAndCountZeros(String str) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(str.getBytes("UTF-8"));
        }catch(Exception e) {
            System.out.println("Error in the hashing function");
            exit(1);
        }
        String finalStr = byteToBinary(hash);
        int numberOfZeros = 0;
        for(int i=0;i<finalStr.length();i++){
            if(finalStr.charAt(i) == '0')
                numberOfZeros = numberOfZeros + 1;
            else
                return numberOfZeros;
        }
        return numberOfZeros;
    }
}
