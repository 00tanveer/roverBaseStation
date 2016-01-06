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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import roverbasestation.TCPConnection.TCPConnect;
import roverbasestation.PinList;

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
    private Circle calibrationStatusCircle, connectionStatusCircle;
    @FXML 
    private TextField targetIP, port;
    
    @FXML
    public AnchorPane map;
    
    //pin console FXML ids
    @FXML
    public Button setButton, reset1Button, addButton, descriptionButton, reset2Button, deleteButton;
    public TextField lowerLatText, lowerLongText, upperLatText, upperLongText, latText, longText, descriptionText;
    public TableView pinTable;
    public TableColumn latColumn, longColumn;
    
    //lower panel FXML ids
    @FXML
    public Circle statusCircle;
    public Label humidity, pressure, lux, longitude, latitude, temperature, battery;
  
    //non-FXML variables - Connection
    public static TCPConnect connObject;
    private Thread run;
    private boolean connect;
    
    //non-FXML variables - Navigation
    public double lowerLat, lowerLong, upperLat, upperLong, mapLength, mapWidth, mapOffsetX, mapOffsetY, lengthDifference, widthDifference, latVal, longVal;
    public final ObservableList<PinList> dataSource = FXCollections.observableArrayList();
    public int pinCounter = 3;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        connect = true;
        
        lowerLat = 0;
        lowerLong = 0;
        upperLat = 0;
        upperLong = 0;
        
        mapLength = 710.0;
        mapWidth = 463.0;
        mapOffsetX = 0;
        mapOffsetY = 0;
        
        lengthDifference = 0;
        widthDifference = 0;
        
        initTable();
    } 
    
    /*public PrimaryWindowController()
    {
        
    }*/
    
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
                            connectionStatusCircle.setFill(Color.RED);
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
            connectionStatusCircle.setFill(Color.GREEN);
            statusCircle.setFill(Color.GREEN);
            connectButton.setDisable(true);
            targetIP.setDisable(true);
            port.setDisable(true);

            alertLabel.setVisible(false);
        }
    }
    
    //the button actions for pin console tab
    @FXML
    private void setButtonClicked(MouseEvent event)
    {        
        if(lowerLatText.getText().equals(""))
        {
            System.out.println("Lower Lat Empty");
        }
        else if(lowerLongText.getText().equals(""))
        {
            System.out.println("Lower Long Empty");
        }
        else if(upperLatText.getText().equals(""))
        {
            System.out.println("Upper Lat Empty");
        }
        else if(upperLongText.getText().equals(""))
        {
            System.out.println("Upper Long Empty");
        }
        else
        {
            setButton.setDisable(true);
            reset1Button.setDisable(false);
            
            lowerLatText.setDisable(true);
            lowerLongText.setDisable(true);
            upperLatText.setDisable(true);
            upperLongText.setDisable(true);
            
            lowerLat = Double.parseDouble(lowerLatText.getText());
            lowerLong = Double.parseDouble(lowerLongText.getText());
            upperLat = Double.parseDouble(upperLatText.getText());
            upperLong = Double.parseDouble(upperLongText.getText());
            
            lengthDifference = Math.abs((upperLong - lowerLong));
            widthDifference = Math.abs((upperLat - lowerLat));
            
            calibrationStatusCircle.setFill(Color.LIME);
        }
    }
    
    @FXML
    private void reset1ButtonClicked(MouseEvent event)
    {
        setButton.setDisable(false);
        reset1Button.setDisable(true);
        
        lowerLatText.setDisable(false);
        lowerLongText.setDisable(false);
        upperLatText.setDisable(false);
        upperLongText.setDisable(false);
        
        lowerLatText.setText("");
        lowerLongText.setText("");
        upperLatText.setText("");
        upperLongText.setText("");
        
        calibrationStatusCircle.setFill(Color.RED);
    }
    
    @FXML
    private void addButtonClicked(MouseEvent event)
    {
        if(latText.getText().equals(""))
        {
            System.out.println("Lat Empty");
        }
        else if(longText.getText().equals(""))
        {
            System.out.println("Long Empty");
        }
        else
        {
            latVal = Double.parseDouble(latText.getText());
            longVal = Double.parseDouble(longText.getText());
            
            dataSource.add(new PinList(latText.getText(),longText.getText(),descriptionText.getText()));
            
            Image pinImage = new Image("file:src\\roverbasestation\\Pin.png");
            
            ImageView pinImageView = new ImageView();
            
            pinImageView.setFitWidth(22.0);
            pinImageView.setFitHeight(38.0);
            
            double tempLayoutX = getPlanarCoordinates("Longitude",longVal) - (pinImageView.getFitWidth()/2);
            pinImageView.setLayoutX((double)tempLayoutX);
            double tempLayoutY = (mapWidth - getPlanarCoordinates("Latitude",latVal)) - (pinImageView.getFitHeight()/2);
            pinImageView.setLayoutY((double)tempLayoutY);
            
            pinImageView.setImage(pinImage);
            pinImageView.setCache(true);
            pinImageView.setSmooth(true);
            
            System.out.println(pinImageView.getLayoutX() + " " + pinImageView.getLayoutY());
            
            map.getChildren().add(pinCounter, pinImageView);
            pinCounter++;
        }
    }
    
    private double getPlanarCoordinates(String latOrLongString, double latOrLongVal)
    {
        if(latOrLongString.equals("Latitude"))
        {
            double temp;
            
            temp = ((lowerLat - latOrLongVal)*mapWidth/widthDifference);
            
            return Math.abs(temp);
        }
        else if(latOrLongString.equals("Longitude"))
        {
            double temp;
            
            temp = ((lowerLong - latOrLongVal)*mapLength)/lengthDifference;
            
            return Math.abs(temp);
        }
        
        return 0;
    }
    
    @FXML
    private void descriptionButtonClicked(MouseEvent event)
    {
        
    }
    
    @FXML
    private void reset2ButtonClicked(MouseEvent event)
    {
        
    }
    
    @FXML
    private void deleteButtonClicked(MouseEvent event)
    {
        
    }   
    
    private void initTable()
    {
        latColumn.setCellValueFactory(new PropertyValueFactory<PinList,String>("latPin"));
        longColumn.setCellValueFactory(new PropertyValueFactory<PinList,String>("longPin"));
        
        pinTable.setItems(dataSource);
    }
}
