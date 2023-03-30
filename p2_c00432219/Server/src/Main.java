
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class Main {

        public static void main(String[] args) throws Exception {
            final int port = 8082;
            ServerSocket serverSock = new ServerSocket(port);
            System.out.println("server listening on local port 8082");

            int clientcount = 0;


            while (true) {
                Socket socka = serverSock.accept();
                clientcount++;
                System.out.println("Client" + clientcount + " has connected to the socket");
                Socket sockb = serverSock.accept();
                clientcount++;
                System.out.println("Client" + clientcount + " has connected to the socket");

                Clienta clienta = new Clienta(socka, clientcount-1);
                Clientb clientb = new Clientb(sockb, clientcount);
                clientb.passClientaSocket(socka);
                clienta.passClientbSocket(sockb);

                new Thread(clienta).start();
                new Thread(clientb).start();
                System.out.println("Path up between clients " + (clientcount-1) + " & " + clientcount);
            }

        }
}




