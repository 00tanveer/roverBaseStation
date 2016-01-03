/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roverbasestation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author tanmaster
 */
public class test {
    public static void main(String[] args)
    {
        try
        {
            System.out.println(new File(".").getAbsolutePath());
            
            InputStream is = new FileInputStream("./src/roverbasestation/testfile.txt");
            
            InputStreamReader reader = new InputStreamReader(is);
            
            BufferedReader br = new BufferedReader(reader);
            
            String line;
            
            while((line = br.readLine()) != null)
            {
                System.out.println(line);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
