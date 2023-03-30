import javax.crypto.*;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// Cameron Matherne
// C00432219
// CMPS 360
// Project #2
public class Main {
    public static void main(String[] args)
            throws NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException {

        // **********************************************************************************
        // ******************  Generate and Store Key / IV  *********************************
        // **********************************************************************************
        SecretKey key = generateKey(256); // n can be 128, 192, 256 bits
        //IvParameterSpec ivParameterSpec = generateIV();  // initialization vector
        byte[] iv = generateIV();

        try (ObjectOutputStream keyos = new ObjectOutputStream(new PrintStream("KeyFile"));
             ObjectOutputStream ivos = new ObjectOutputStream(new PrintStream("IvFile"))) {

            keyos.writeObject(key);
            ivos.writeObject(iv);

        } catch (Exception e) {
            System.out.println("one");
            e.printStackTrace();
            return;
        }


    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
