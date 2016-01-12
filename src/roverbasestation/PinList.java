/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Fireheart-Ultra
 */
public class PinList 
{
    private int pinID;
    private final SimpleStringProperty latPin;
    private final SimpleStringProperty longPin;
    private String descriptionPin;
    private ImageView pinImageView, pinSelectionImageView;
    
    public PinList(int id, String _latPin, String _longPin, String _descriptionPin)
    {
        this.pinID = id;
        this.latPin = new SimpleStringProperty(_latPin);
        this.longPin = new SimpleStringProperty(_longPin);
        this.descriptionPin = _descriptionPin;
    }
    
    public void setPinID(int id)
    {
        pinID = id;
    }
    public int getPinID()
    {
        return pinID;
    }
    
    public String getLatPin()
    {
        return latPin.getValue();
    }
    public StringProperty latPinProperty()
    {
        return latPin;
    }
    
    
    public String getLongPin()
    {
        return longPin.getValue();
    }
    public StringProperty longPinProperty()
    {
        return longPin;
    }
    
    
    public String getDescriptionPin()
    {
        return descriptionPin;
    }
    
    public void setPinImageView()
    {
        Image pinImage = new Image("file:src\\roverbasestation\\Pin.png");
        Image pinSelectionImage = new Image("file:src\\roverbasestation\\PinGlow.png");
            
        pinImageView = new ImageView();
        pinSelectionImageView = new ImageView();

        pinImageView.setFitWidth(22.0);
        pinImageView.setFitHeight(38.0);
        pinSelectionImageView.setFitWidth(23.0);
        pinSelectionImageView.setFitHeight(40.0);

        pinImageView.setImage(pinImage);
        pinImageView.setCache(true);
        pinImageView.setSmooth(true);
        
        pinSelectionImageView.setImage(pinSelectionImage);
        pinSelectionImageView.setCache(true);
        pinSelectionImageView.setVisible(false);
    }
    public ImageView getPinImageView()
    {
        return pinImageView;
    }
    public ImageView getPinSelectionImageView()
    {
        return pinSelectionImageView;
    }
}
