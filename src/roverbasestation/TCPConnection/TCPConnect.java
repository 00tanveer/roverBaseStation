/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation.TCPConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author tanmaster
 */
public class TCPConnect {
    Socket socketClient;
    String hostname;
    int port;
    
    public TCPConnect(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }
    
    public boolean connect() throws UnknownHostException, IOException
    {
        System.out.println("Attempting to connect to "+hostname+":"+port);
        socketClient = new Socket(hostname,port);
        
        if(socketClient.isConnected())
        {
            System.out.println("Connection Established");
        }
        
        return true;
    }
    
    public int getInputStreamRead() throws IOException
    {
        return socketClient.getInputStream().read();
    }
    
    public BufferedReader readFromPi() throws IOException
    {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        
        return reader;
    }
    
    public Socket client()
    {
        return socketClient;
    }
}
