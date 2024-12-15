import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES_Enryption {
    private int key_size = 128;
    private SecretKey key;
    private int T_LEN = 128;
    private byte[] IV;

    private void initFromStrings(String secretKey) {
        key = new SecretKeySpec(decode(secretKey), "AES");
        IV = decode("WD9c+anls3zxlgoy");
    }

  
    public String encrpytMsg(String message, String secretKey) throws Exception {
        if(message.isBlank())
            return "";
        initFromStrings(secretKey);
        byte[] byteMsg = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encrpytedBytes = encryptionCipher.doFinal(byteMsg);
        // System.out.println("Message is Sucessfully encrypted!...");
        String encodeEncryptedBytes = encode(encrpytedBytes);
        // System.out.println("Encrypted Message: " + encodeEncryptedBytes);
        return encodeEncryptedBytes;
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public String decryptMsg(String encryptedMsg, String secretKey) throws Exception {
        if(encryptedMsg.isBlank())
            return "";
        initFromStrings(secretKey);
        byte[] byteMsg = decode(encryptedMsg);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(byteMsg);
        String decryptedMessage = new String(decryptedBytes);
        // System.out.println("The Message Decrypted Sucessfully!....");
        // System.out.println("Decrypted Message: " + decryptedMessage);
        return decryptedMessage;

    }

    public static SecretKey GenerateSecretKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("AES/GCM/NoPadding");
        generator.init(keySize);
        SecretKey key = generator.generateKey();
//        System.out.println("Secrect key" + key.getEncoded());
        return key;
    }

    public void getSecretKey() {
        System.err.println("SecretKey: " + encode(key.getEncoded()));
        System.err.println("IV: " + encode(IV));
    }

   
}