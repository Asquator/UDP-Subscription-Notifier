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

    public NotifierServer(){
        try {
            socket = new DatagramSocket(PORT);
            new Thread(new RequestReceiver()).start();

        }
        catch(SocketException ex){
            ex.printStackTrace();
        }
    }

    public void send(String text){
        DatagramPacket packet;
        buffer = text.getBytes();

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

    public enum SubscribeRequest{
        SUBSCRIBE, UNSUBSCRIBE;
    }

    private class RequestReceiver implements Runnable{

        private byte[] buf = new byte[100];
        private DatagramPacket packet = new DatagramPacket(buf, buf.length);

        @Override
        public void run() {
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

        private SubscribeRequest receiveRequest() throws IOException, ClassNotFoundException {
            socket.receive(packet);
            InetSocketAddress addr = new InetSocketAddress(packet.getAddress(), packet.getPort());
            byte[] data = packet.getData();

            try(ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data))){
                return (SubscribeRequest) is.readObject();
            }
        }

        private void processRequest(SubscribeRequest req){
            InetSocketAddress addr = new InetSocketAddress(packet.getAddress(), packet.getPort());

            switch (req){
                case SUBSCRIBE:
                    synchronized (clients){
                        clients.add(addr);
                    }
                    break;

                case UNSUBSCRIBE:
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
