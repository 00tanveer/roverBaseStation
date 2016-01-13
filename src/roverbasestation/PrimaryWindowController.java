/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    
    //map FXML ids
    @FXML
    public AnchorPane map;
    @FXML
    public ImageView mapImageView;
    @FXML
    public Label longScale1, longScale2, longScale3, longScale4, longScale5, longScale6, latScale1, latScale2, latScale3, latScale4, latScale5, latScale6;
    
    //pin console FXML ids
    @FXML
    public Button setButton, reset1Button, addButton, reset2Button, deleteButton;
    @FXML
    public TextField lowerLatText, lowerLongText, upperLatText, upperLongText, latText, longText, descriptionText;
    @FXML
    public TableView pinTable;
    @FXML
    public TableColumn latColumn, longColumn;
    
    //motion panel FXML ids
    @FXML
    public ImageView pinOfCompass;
    @FXML
    public Label angleTextLabel;
    @FXML
    public Circle forwardArrow, backwardArrow, leftArrow, rightArrow;
    
    //lower panel FXML ids
    @FXML
    public Circle statusCircle;
    @FXML
    public Label humidity, pressure, lux, longitude, latitude, temperature, battery, messageTextLabel;
    @FXML
    public TextField mapLocationText;
    @FXML
    public Button browseMapButton, loadMapButton;
  
    //non-FXML variables - Connection
    public static TCPConnect connObject;
    private Thread run;
    private boolean connect;
    
    //non-FXML variables - Navigation
    public double lowerLat, lowerLong, upperLat, upperLong, mapWidth, mapHeight, mapOffsetX, mapOffsetY, widthDifference, heightDifference, latVal, longVal;
    public final ObservableList<PinList> dataSource = FXCollections.observableArrayList();
    public int pinCounterOffset = 2, pinCounter;
    public boolean calibrationDone;
    
    private File mapImage = null;
    
    private static Stage controllerStage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        connect = true;
        
        lowerLat = 0;
        lowerLong = 0;
        upperLat = 0;
        upperLong = 0;
        
        mapWidth = mapImageView.getFitWidth();
        mapHeight = mapImageView.getFitHeight();
        mapOffsetX = 0;
        mapOffsetY = 0;
        
        widthDifference = 0;
        heightDifference = 0;
        
        pinCounter = pinCounterOffset;
        
        calibrationDone = false;
        
        initTable();
        addPinTableSelectionListener();
        
        messageTextLabel.setTextFill(Color.RED);
        messageTextLabel.setText("Please Calibrate the coordinates");
    } 
    
    /*public PrimaryWindowController()
    {
        
    }*/
    
    public static void init(Stage stage)
    {
        controllerStage = stage;
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
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Lower Lat Empty");
        }
        else if(lowerLongText.getText().equals(""))
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Lower Long Empty");
        }
        else if(upperLatText.getText().equals(""))
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Upper Lat Empty");
        }
        else if(upperLongText.getText().equals(""))
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Upper Long Empty");
        }
        else
        {
            setButton.setDisable(true);
            reset1Button.setDisable(false);
            
            addButton.setDisable(false);
            latText.setDisable(false);
            longText.setDisable(false);
            descriptionText.setDisable(false);
            
            lowerLatText.setDisable(true);
            lowerLongText.setDisable(true);
            upperLatText.setDisable(true);
            upperLongText.setDisable(true);
            
            lowerLat = Double.parseDouble(lowerLatText.getText());
            lowerLong = Double.parseDouble(lowerLongText.getText());
            upperLat = Double.parseDouble(upperLatText.getText());
            upperLong = Double.parseDouble(upperLongText.getText());
            
            widthDifference = Math.abs((upperLong - lowerLong));
            heightDifference = Math.abs((upperLat - lowerLat));
            
            setScaleLabels(true);
                        
            calibrationStatusCircle.setFill(Color.LIME);
            calibrationDone = true;
            
            messageTextLabel.setTextFill(Color.GREEN);
            messageTextLabel.setText("Calibration Done");
        }
    }
    
    private void setScaleLabels(boolean active)
    {
        if(active == true)
        {
            Double latValForLabel = lowerLat, longValForLabel = lowerLong;
            
            if(lowerLat < upperLat)
            {
                latScale1.setText(String.format("%.3f", latValForLabel));
                latValForLabel += heightDifference/5;
                latScale2.setText(String.format("%.3f", latValForLabel));
                latValForLabel += heightDifference/5;
                latScale3.setText(String.format("%.3f", latValForLabel));
                latValForLabel += heightDifference/5;
                latScale4.setText(String.format("%.3f", latValForLabel));
                latValForLabel += heightDifference/5;
                latScale5.setText(String.format("%.3f", latValForLabel));
                latValForLabel += heightDifference/5;
                latScale6.setText(String.format("%.3f", latValForLabel));
            }
            else
            {
                latScale1.setText(String.format("%.3f", latValForLabel));
                latValForLabel -= heightDifference/5;
                latScale2.setText(String.format("%.3f", latValForLabel));
                latValForLabel -= heightDifference/5;
                latScale3.setText(String.format("%.3f", latValForLabel));
                latValForLabel -= heightDifference/5;
                latScale4.setText(String.format("%.3f", latValForLabel));
                latValForLabel -= heightDifference/5;
                latScale5.setText(String.format("%.3f", latValForLabel));
                latValForLabel -= heightDifference/5;
                latScale6.setText(String.format("%.3f", latValForLabel));
            }
            
            if(lowerLong < upperLong)
            {
                longScale1.setText(String.format("%.3f", longValForLabel));
                longValForLabel += widthDifference/5;
                longScale2.setText(String.format("%.3f", longValForLabel));
                longValForLabel += widthDifference/5;
                longScale3.setText(String.format("%.3f", longValForLabel));
                longValForLabel += widthDifference/5;
                longScale4.setText(String.format("%.3f", longValForLabel));
                longValForLabel += widthDifference/5;
                longScale5.setText(String.format("%.3f", longValForLabel));
                longValForLabel += widthDifference/5;
                longScale6.setText(String.format("%.3f", longValForLabel));
            }
            else
            {
                longScale1.setText(String.format("%.3f", longValForLabel));
                longValForLabel -= widthDifference/5;
                longScale2.setText(String.format("%.3f", longValForLabel));
                longValForLabel -= widthDifference/5;
                longScale3.setText(String.format("%.3f", longValForLabel));
                longValForLabel -= widthDifference/5;
                longScale4.setText(String.format("%.3f", longValForLabel));
                longValForLabel -= widthDifference/5;
                longScale5.setText(String.format("%.3f", longValForLabel));
                longValForLabel -= widthDifference/5;
                longScale6.setText(String.format("%.3f", longValForLabel));
            }
        }
        else
        {
            latScale1.setText("");
            latScale2.setText("");
            latScale3.setText("");
            latScale4.setText("");
            latScale5.setText("");
            latScale6.setText("");
            
            longScale1.setText("");
            longScale2.setText("");
            longScale3.setText("");
            longScale4.setText("");
            longScale5.setText("");
            longScale6.setText("");
        }
    }
    
    @FXML
    private void reset1ButtonClicked(MouseEvent event)
    {
        setButton.setDisable(false);
        reset1Button.setDisable(true);
        
        latText.setText("");
        longText.setText("");
        descriptionText.setText("");
        
        addButton.setDisable(true);
        latText.setDisable(true);
        longText.setDisable(true);
        descriptionText.setDisable(true);
        
        lowerLatText.setDisable(false);
        lowerLongText.setDisable(false);
        upperLatText.setDisable(false);
        upperLongText.setDisable(false);
        
        lowerLatText.setText("");
        lowerLongText.setText("");
        upperLatText.setText("");
        upperLongText.setText("");
        
        setScaleLabels(false);
        
        calibrationStatusCircle.setFill(Color.RED);
        calibrationDone = false;
        
        messageTextLabel.setTextFill(Color.RED);
        messageTextLabel.setText("Please Calibrate the coordinates");
    }
    
    @FXML
    private void addButtonClicked(MouseEvent event)
    {
        if(latText.getText().equals(""))
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Lat Empty");
        }
        else if(longText.getText().equals(""))
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Long Empty");
        }
        else
        {
            latVal = Double.parseDouble(latText.getText());
            longVal = Double.parseDouble(longText.getText());
            
            PinList tempAdd = new PinList((pinCounter-pinCounterOffset)/2, latText.getText(),longText.getText(),descriptionText.getText());
            
            dataSource.add(tempAdd);
            
            tempAdd.setPinImageView();
            
            double tempLayoutX = getPlanarCoordinates("Longitude",longVal) - (tempAdd.getPinImageView().getFitWidth()/2);
            tempAdd.getPinImageView().setLayoutX((double)tempLayoutX);
            tempAdd.getPinSelectionImageView().setLayoutX((tempAdd.getPinImageView().getLayoutX()-1));
            double tempLayoutY = (mapHeight - getPlanarCoordinates("Latitude",latVal)) - (tempAdd.getPinImageView().getFitHeight());
            tempAdd.getPinImageView().setLayoutY((double)tempLayoutY);
            tempAdd.getPinSelectionImageView().setLayoutY((tempAdd.getPinImageView().getLayoutY()-2));
            
            System.out.println(tempAdd.getPinImageView().getLayoutX() + " " + tempAdd.getPinImageView().getLayoutY());
            
            map.getChildren().add(pinCounter, tempAdd.getPinSelectionImageView());
            map.getChildren().add(pinCounter+1, tempAdd.getPinImageView());
            pinCounter+=2;
            
            latText.setText("");
            longText.setText("");
            descriptionText.setText("");
        }
    }
    
    private double getPlanarCoordinates(String latOrLongString, double latOrLongVal)
    {
        if(latOrLongString.equals("Latitude"))
        {
            double temp;
            
            temp = ((lowerLat - latOrLongVal)*mapHeight/heightDifference);
            
            return Math.abs(temp);
        }
        else if(latOrLongString.equals("Longitude"))
        {
            double temp;
            
            temp = ((lowerLong - latOrLongVal)*mapWidth)/widthDifference;
            
            return Math.abs(temp);
        }
        
        return 0;
    }
        
    @FXML
    private void reset2ButtonClicked(MouseEvent event)
    {
        map.getChildren().remove(pinCounterOffset, pinCounter);
        
        pinCounter = pinCounterOffset;
        
        dataSource.clear();
        
        messageTextLabel.setTextFill(Color.GREEN);
        messageTextLabel.setText("Pin Table reset");
    }
    
    @FXML
    private void deleteButtonClicked(MouseEvent event)
    {
        int i, temp;
        
        PinList selectedPin = (PinList)pinTable.getSelectionModel().getSelectedItem();
        
        map.getChildren().remove(selectedPin.getPinImageView());
        map.getChildren().remove(selectedPin.getPinSelectionImageView());
        
        temp = selectedPin.getPinID();
        dataSource.remove(selectedPin.getPinID());
        
        for(i=temp;i<((pinCounter-pinCounterOffset)/2)-1;i++)
        {
            System.out.println(i);
            dataSource.get(i).setPinID(dataSource.get(i).getPinID()-1);
        }
        
        pinCounter-=2;
    }
    
    private void addPinTableSelectionListener()
    {
        pinTable.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<PinList>()
            {
                @Override
                public void changed(ObservableValue<? extends PinList> ov, PinList newSelection, PinList oldSelection)
                {
                    if(oldSelection != null)
                    {
                        messageTextLabel.setTextFill(Color.BLACK);
                                        
                        if(pinTable.getSelectionModel().getSelectedItem() != null)
                        {
                            if(oldSelection.getDescriptionPin().length() > 0)
                            {
                                messageTextLabel.setText(oldSelection.getDescriptionPin() + oldSelection.getPinID());
                            }
                            else
                            {
                                messageTextLabel.setText("No description is available");
                            }   
                        }
                        else
                        {
                            messageTextLabel.setTextFill(Color.BLACK);
                            messageTextLabel.setText("No description is available");
                        }

                        for(PinList p:dataSource)
                        {
                            p.getPinSelectionImageView().setVisible(false);

                        }

                        oldSelection.getPinSelectionImageView().setVisible(true);
                    }
                }
            }
        );
    }
    
    private void initTable()
    {
        latColumn.setCellValueFactory(new PropertyValueFactory<PinList,String>("latPin"));
        longColumn.setCellValueFactory(new PropertyValueFactory<PinList,String>("longPin"));
        
        pinTable.setItems(dataSource);
    }
    
    @FXML
    private void browseClicked(MouseEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
        mapImage = fileChooser.showOpenDialog(controllerStage);
        
        if(mapImage != null)
        {
            mapLocationText.setText(mapImage.toString());
        }
    }
    
    @FXML
    private void loadMapClicked(MouseEvent event) throws MalformedURLException
    {
        if(mapImage != null)
        {
            try
            {
                String imagePath = mapImage.toURI().toURL().toString();
                Image newMap = new Image(imagePath);
                
                mapImageView.setImage(newMap);
                
                mapLocationText.setText("");
                
                messageTextLabel.setTextFill(Color.GREEN);
                messageTextLabel.setText("Map loaded successfully - Please check calibration");
            }
            catch(Exception e)
            {
                messageTextLabel.setTextFill(Color.RED);
                messageTextLabel.setText("Error opening file");
                
                System.out.println(e.getMessage() + mapImage.getPath());
            }
        }
        else
        {
            messageTextLabel.setTextFill(Color.RED);
            messageTextLabel.setText("Please select a file");
        }
    }
}
