/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Fireheart-Ultra
 */
public class PinList 
{
    private final SimpleStringProperty latPin;
    private final SimpleStringProperty longPin;
    private String descriptionPin;
    
    public PinList(String _latPin, String _longPin, String _descriptionPin)
    {
        this.latPin = new SimpleStringProperty(_latPin);
        this.longPin = new SimpleStringProperty(_longPin);
        this.descriptionPin = _descriptionPin;
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
}
