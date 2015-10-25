package service;

import java.io.BufferedReader;
import java.io.PrintWriter;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by NB on 24.10.2015.
 */
public class ClientTCP {

    private String serverMessage;
    private Socket socket;

    public String SERVERIP; //your computer IP address
    public static final int SERVERPORT = 666;
    private OnMessageReceived mMessageListener = null;

    private static final String LOG_TAG = "ClientTCP";
    private boolean mRun = false;

    OutputStream out;
    BufferedReader in;

    public ClientTCP(String ip_address) {
        SERVERIP = ip_address;
    }

    public void sendMessage(byte[] message) {
        try {
            if(out != null) {
                out.write(message);
              //  out.flush();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Sending error!");
        }
    }

    public void stopClient() {
        mRun = false;
        try {
            if(socket != null)
                socket.close();
            else
                socket = null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Soccket cannot close!");
        }
    }

    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e(LOG_TAG, "C: Connecting...");
            socket = new Socket(serverAddr, SERVERPORT);
            try {
                out = socket.getOutputStream();
               // out.write(new byte[]{3,0x23});
                Log.e(LOG_TAG, "C: Sent.");

                Log.e(LOG_TAG, "C: Done.");
/*
                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null) {
                    }
                    serverMessage = null;

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");*/

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "C: Error", e);
        }
    }


    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
