package com.example.clientlite;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class Controller implements Runnable {
    public TextField URL;
    public TextField inputToSend;
    public TextArea textRecieved;
    public TextField NAME;

    final int port = 8082;
    DataOutputStream out;
    BufferedReader in;
    String path;
    SecretKey key;
    byte[] iv;
    //IvParameterSpec ivParameterSpec2;
    String cipherText = "";
    String plainText = "";


    public void ConnectToServer(ActionEvent actionEvent) {
        try {
            path = URL.getText();
            if (path == null) path = "localhost";
            Socket sock = new Socket(path, port);
            out = new DataOutputStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (Exception e) {
            System.out.println("client lite input output connection error");
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public void SendText(ActionEvent actionEvent) {
        // combine the name and message as one string, so all communications have a name attached
        String plaintext = inputToSend.getText();
        String name = NAME.getText();
        String stringToEncrypt = name + " : " + plaintext;

        // retrieve keys from files to encrypt message
        try (ObjectInputStream keyos = new ObjectInputStream(new FileInputStream("src/main/java/com/example/clientlite/KeyFile"));
             ObjectInputStream ivos = new ObjectInputStream(new FileInputStream("src/main/java/com/example/clientlite/IvFile"))) {
            key = (SecretKey) keyos.readObject();
            iv = (byte[])(ivos.readObject());
            String algorithm = "AES/CBC/PKCS5Padding";
            cipherText = encrypt(algorithm, stringToEncrypt, key, new IvParameterSpec(iv));
        } catch (Exception e) {
            System.out.println("unable to retreive keys and encrypt message");
            e.printStackTrace();
            return;
        }
        try {
            // send out the encrypted message + attached username
            out.writeBytes(cipherText + "\n");
            out.flush();
            System.out.println(NAME.getText() + " sent " + plaintext);
        } catch (Exception e) {
            System.out.println("client send error");
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        try {
            // initiates whenever this client gets a message
            while ((message = in.readLine()) != null) {
                // sometimes, in.readLine() will contain the name of the client
                // so check to make sure it is a message, not just the name
                if (!message.equals(NAME.getText())) {
                    String finalMessage = "";
                    //retrieve keys to decrypt messages
                    try (ObjectInputStream keyos = new ObjectInputStream(new FileInputStream("src/main/java/com/example/clientlite/KeyFile"));
                         ObjectInputStream ivos = new ObjectInputStream(new FileInputStream("src/main/java/com/example/clientlite/IvFile"))) {
                        key = (SecretKey) keyos.readObject();
                        iv = (byte[]) (ivos.readObject());
                        String algorithm = "AES/CBC/PKCS5Padding";
                        finalMessage = decrypt(algorithm, message, key, new IvParameterSpec(iv));
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                        System.out.println("client decryption error");
                        e.printStackTrace();
                    }

                    String finalMessage1 = finalMessage;
                    Platform.runLater(
                            () -> textRecieved.setText(textRecieved.getText() + "\n" + finalMessage1)
                    );
                    System.out.println(NAME.getText() + " received: " + message);
                }
            }
            } catch(Exception e){
                System.out.println("client receiving error");
            }
        }



    public static String encrypt(String algorithm, String input, SecretKey key,
                                 IvParameterSpec iv) throws
            NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key,
                                 IvParameterSpec iv) throws
            NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
}
