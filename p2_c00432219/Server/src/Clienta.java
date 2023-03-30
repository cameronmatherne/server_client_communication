
import java.io.*;
import java.net.*;

public class Clienta implements Runnable {

    DataOutputStream out = null;
    BufferedReader in = null;
    int clientnumber = 0;

    public Clienta(Socket sock, int clientnumber) {
        try {
            this.clientnumber = clientnumber;
            out = new DataOutputStream(sock.getOutputStream());
        } catch (Exception e) {
            System.out.println("Server Client" + clientnumber + "output connection error");
        }
    }

    public void passClientbSocket(Socket sock) {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (Exception e) {
            System.out.println("Server Client" + clientnumber + "input connection error");
            e.printStackTrace();
        }
    }

    public void write(String message) throws Exception {
        try {
            out.writeBytes(message + "\n");
            out.flush();
            System.out.println("Server Client" + clientnumber + " sent: " + message);
        } catch (Exception e) {
            System.out.println("Server Client" + clientnumber + " write error");
        }
    }

    public void run() {
        String input;
        try {
            while ((input=in.readLine()) != null) {
                System.out.println("Server Client" + clientnumber + " received: " + input);
                write(input);
            }
        } catch (Exception e) {
            System.out.println("Server Client" + clientnumber + " read error");
            e.printStackTrace();
        }
        finally {
            try {
                in.close();

            } catch (Exception e) {
                System.out.println("Server Client" + clientnumber + " close error");
                e.printStackTrace();
            }
        }
    }
}
