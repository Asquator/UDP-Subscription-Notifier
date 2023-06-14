package com.example.udp_subscriptor;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Date;

import static javafx.application.Platform.runLater;

/**
 * Describes notifier client instance
 */
public class NotifierClient implements Runnable{

    private DatagramSocket socket;
    private String host;

    private byte[] msgbuffer = new byte[10000];

    private byte[] requestBuf = new byte[200];

    private NotifierController cont;

    /**
     * Constructs a notification receiver
     * @param cont GUI controller
     * @param host server host
     * @throws UnknownHostException on host resolve error
     * @throws SocketException on socket creation error
     */
    public NotifierClient(NotifierController cont, String host) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(host);
        socket = new DatagramSocket(0);

        this.cont = cont;
        this.host = host;
    }


    /**
     * Runs the client
     */
    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(msgbuffer, msgbuffer.length);

        //listen to messages and update the GUI
        while (true)
            try {
                socket.receive(packet);
                String msg = new StringBuilder().append(new Date()).append(
                        "   ").append(new String(msgbuffer).substring(0, packet.getLength())).append(
                        "\n").toString();

                runLater(() -> cont.appendText(msg));

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     * Manages subscription state
     * @param val true iff subscription wanted
     */
    public void subscribe(boolean val){

        //choose the needed option
        NotifierServer.SubscribeRequest req = val ? NotifierServer.SubscribeRequest.SUBSCRIBE :
                NotifierServer.SubscribeRequest.UNSUBSCRIBE;

        DatagramPacket packet;

        //serialize the option to requestBuf
        try(ByteArrayOutputStream bs = new ByteArrayOutputStream(requestBuf.length);
                ObjectOutputStream os = new ObjectOutputStream(bs))
        {
                os.writeObject(req);
                requestBuf = bs.toByteArray();
        }

        catch(IOException ex){
            return;
        }

        //send the needed option
        try{
            packet = new DatagramPacket(requestBuf, requestBuf.length,
                    InetAddress.getByName(host), NotifierServer.PORT);

            socket.send(packet);
        }
        catch(IOException ex){
            System.err.println("Couldn't send");
        }

    }
}
