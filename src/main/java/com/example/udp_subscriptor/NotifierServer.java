package com.example.udp_subscriptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class NotifierServer extends Thread{

    public static final int PORT = 6666;

    private byte[] buffer;

    private final Set<InetSocketAddress> clients = new HashSet<>();

    private DatagramSocket socket;

    /**
     * Create a server instance and start listener
     */
    public NotifierServer(){
        try {
            socket = new DatagramSocket(PORT);
        }
        catch(SocketException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Broadcast the given message
     * @param text message
     */
    public void send(String text){
        DatagramPacket packet;
        buffer = text.getBytes();

        //lock on the set of clients and send the message to everybody
        synchronized (clients){
            for(InetSocketAddress addr : clients){
                packet = new DatagramPacket(buffer, buffer.length, addr);
                try {
                    socket.send(packet);
                }

                catch (IOException ex){
                    System.err.println("Couldn't send package to " + addr);
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Run the server
     */
    @Override
    public void run() {
        super.run();

        Thread listener = new Thread(new RequestListener());
        listener.setDaemon(true);
        listener.start();
    }

    /**
     * Subscription option
     */
    public enum SubscribeRequest{
        SUBSCRIBE, UNSUBSCRIBE;
    }


    //listens to user subscription requests
    private class RequestListener implements Runnable{

        private byte[] buf = new byte[200];
        private DatagramPacket packet = new DatagramPacket(buf, buf.length);

        @Override
        public void run() {

            //continuously listen to request and process them
            while(true){
                try {
                    SubscribeRequest req = receiveRequest();
                    processRequest(req);
                }
                catch (Exception ex){
                    System.err.println("Error in receiving request");
                    ex.printStackTrace();
                }
            }
        }

        //receive a UDP subscription request
        private SubscribeRequest receiveRequest() throws IOException, ClassNotFoundException {
            socket.receive(packet);

            //extract data
            byte[] data = packet.getData();

            //return ready request
            try(ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data))){
                return (SubscribeRequest) is.readObject();
            }
        }

        //process the given request
        private void processRequest(SubscribeRequest req){
            //exctract IP address
            InetSocketAddress addr = new InetSocketAddress(packet.getAddress(), packet.getPort());

            switch (req){
                case SUBSCRIBE: //subscribe request
                    synchronized (clients){
                        clients.add(addr);
                    }
                    break;

                case UNSUBSCRIBE: //remove subscription request
                    synchronized (clients){
                        clients.remove(addr);
                    }

                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + req);
            }
        }
    }
}
