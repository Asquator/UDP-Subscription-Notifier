package com.example.udp_subscriptor;

import java.io.IOException;
import java.net.*;

public class NotifierClient extends Thread{

    private DatagramSocket socket;
    private String host;

    private byte[] msgbuffer = new byte[10000];

    private NotifierController cont;

    public NotifierClient(NotifierController cont, String host) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(host);
        socket = new DatagramSocket(0);

        this.cont = cont;
        this.host = host;
    }

    @Override
    public void run() {
        super.run();

        DatagramPacket packet = new DatagramPacket(msgbuffer, msgbuffer.length);

        while (true)
            try {
                socket.receive(packet);
                cont.appendText(new String(msgbuffer).substring(packet.getLength()));

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void subscribe(boolean val){
        byte[] buf = new byte[100];
        NotifierServer.SubscribeRequest req = val ? NotifierServer.SubscribeRequest.SUBSCRIBE :
                NotifierServer.SubscribeRequest.UNSUBSCRIBE;

        DatagramPacket packet;

        while(true){
            try{
                packet = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName(host), NotifierServer.PORT);

                socket.send(packet);
                break;
            }
            catch(IOException ex){
                System.err.println("Couldn't send, trying again");
            }
        }
    }
}
