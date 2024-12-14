import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;


public class CommunicationHandler implements Runnable {

    public String name = null;
    public BufferedReader clientInputStream;
    public PrintWriter clientOutputStream;
    private Socket clientSocket;
    public  static List<CommunicationHandler> clients=new ArrayList<>();
    private CommunicationHandler client2=null;
    private String key = "rnLgcmZmVZDsTreCCiiryA==";
    private String IV = "jQFIcwdbvVMRjjxk";
    AES_Enryption aes = new AES_Enryption();


    public CommunicationHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.clientInputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.clientOutputStream = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.name=clientInputStream.readLine();
            clients.add(this);
            System.out.println(clients);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public void run() {

        String inputMessageFromClient = "", recipientName = "", messageToReceiver = "";
        while (true) {
            try {
                if (!clientInputStream.ready())
                    continue;
                inputMessageFromClient = clientInputStream.readLine();
                if(!inputMessageFromClient.isBlank())
                    inputMessageFromClient= aes.decryptMsg(inputMessageFromClient, key,IV);
                else
                    continue;
                System.out.println(inputMessageFromClient);
                if (client2 == null) {
                    try {
                        findClient(inputMessageFromClient);
                        clientOutputStream.println(aes.encrpytMsg("Connected Successfully With " + client2.name,key,IV));
                        clientOutputStream.flush();
                    } catch (Exception ex) {

                    }
                } else if (inputMessageFromClient.equals("<Exit>")){
                    clientOutputStream.println("you are Exited Successfully");
                    clientOutputStream.flush();
                    client2=null;
                }
                else{
                    sendMessageToClient(inputMessageFromClient);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception ignored) {

            }
        }


    }

    private void findClient(String inputMessageFromClient) {
        boolean flag = false;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).name.equals(inputMessageFromClient)) {
                client2 = clients.get(i);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new RuntimeException("Client With username " + inputMessageFromClient + " Not Found");
        }
    }

    private void sendMessageToClient(String messageToReceiver) {
        if (messageToReceiver.isBlank())
            return;

        try {
            messageToReceiver = Client.aes.encrpytMsg(name + ": " + messageToReceiver, key,IV);
        } catch (Exception e) {
            e.printStackTrace();
        }
        client2.clientOutputStream.println(messageToReceiver);
        client2.clientOutputStream.flush();
    }
    @Override
    public String toString() {
        return name;
    }
}