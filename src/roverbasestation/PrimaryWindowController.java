/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import roverbasestation.TCPConnection.TCPConnect;

/**
 *
 * @author tanmaster
 */
public class PrimaryWindowController implements Initializable, Runnable {
    
    //connect tab FXML ids
    @FXML
    private Label alertLabel;
    @FXML
    private ToggleButton TCPbutton, UDPbutton;
    @FXML
    private Button connectButton;
    @FXML
    private Circle circle;
    @FXML 
    private TextField targetIP, port;
    
    @FXML
    public AnchorPane map;
    
    //pin console FXML ids
    @FXML
    public Button setButton, reset1Button, addButton, reset2Button, deleteButton;
    
    //lower panel FXML ids
    @FXML
    public Circle statusCircle;
    public Label humidity, pressure, lux, longitude, latitude, temperature, battery;
  
    //non-FXML variables
    public static TCPConnect connObject;
    private Thread run;
    private boolean connect;
    
    public PrimaryWindowController()
    {
        connect = true;
    }
    
    @Override
    public void run()
    {
        
        while(true)
        {
//            if(connect == false)
//            {
//                System.out.println("Connect: " + connect);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(PrimaryWindowController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            
            if(connect)
            {
                System.out.println("run is running");
              
                try {
                        BufferedReader reader;
                        String line, lat, hum;
                        reader = new BufferedReader(new InputStreamReader(connObject.client().getInputStream()));
                        
                        line = reader.readLine();
                        
                        System.out.println(line.split(",")[0]);
                        hum = line.split(",")[1];
                        System.out.println(hum);
                        lat = line.split(",")[2];
                        System.out.println(lat);
                        
                        Platform.runLater(new Runnable() {
                        @Override
                        public void run() 
                        {
                            //if you change the UI, do it here !
                            humidity.setText("Humidity: " + hum.substring(10) + "%");
                            latitude.setText("Latitude: " + lat.substring(10));
                        }
                        });
                        
                        
                    
                        if(connObject.getInputStreamRead() == -1)
                        {
                            connectButton.setDisable(false);
                            targetIP.setDisable(false);
                            circle.setFill(Color.RED);
                            statusCircle.setFill(Color.RED);
                            connect = false;

                            alertLabel.setVisible(true);
                        }
                } catch (IOException ex) {
                    Logger.getLogger(PrimaryWindowController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void show(String line)
    {
        if(line.startsWith("(humidity)"))
        {
            humidity.setText("Humidity: " + line.substring(10) + "%");
        }
        else if(line.startsWith("(latitude)"))
        {
            latitude.setText("Latitude: " + line.substring(10));
        }
    }
    
    @FXML
    private void TCPButtonAction(ActionEvent event)
    {
        UDPbutton.setDisable(true);
    }
    
    @FXML
    private void UDPButtonAction(ActionEvent event)
    {
        TCPbutton.setDisable(true);
    }
    
    
    @FXML
    private void ConnectButtonAction(ActionEvent event) throws IOException
    {
        connObject = new TCPConnect(targetIP.getText(), Integer.parseInt(port.getText()));
        connect = connObject.connect();
        System.out.println(connect);
        run = new Thread(this);
        run.start();
        
        if(connect)
        {
            circle.setFill(Color.GREEN);
            statusCircle.setFill(Color.GREEN);
            connectButton.setDisable(true);
            targetIP.setDisable(true);
            port.setDisable(true);

            alertLabel.setVisible(false);
        }
    }
    
    //the button actions for pin console tab
    @FXML
    private void setButtonAction(ActionEvent event)
    {
        
    }
    
    @FXML
    private void reset1ButtonAction(ActionEvent event)
    {
        
    }
    
    @FXML
    private void addButtonAction(ActionEvent event)
    {
        
    }
    
    @FXML
    private void reset2ButtonAction(ActionEvent event)
    {
        
    }
    
    @FXML
    private void deleteButtonAction(ActionEvent event)
    {
        
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
