/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.rest.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;

/**
 *
 * @author paolo
 */
public class Sparql {
    private String query ;
    private String endpoint;
    
    public  Sparql(String query, String endpoint)
    {
        this.query = query;
        this.endpoint = endpoint;
    }
    
    public String returnJSON(){
        String res = "";
        try{
               URL url = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 
		// optional default is GET
               con.setDoOutput(true);
               con.setRequestMethod("GET");
               con.addRequestProperty("Accept", "application/json");
            String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8");               
               
                OutputStreamWriter osw = new OutputStreamWriter(
                con.getOutputStream());
                osw.write(data);
                osw.flush();



                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String in = "";
                StringBuffer sb = null;
                sb = new StringBuffer();

                while ((in = br.readLine()) != null) {
                    sb.append(in + "\n");
                }

                osw.close();
                br.close();   
           res = sb.toString();
         return res;
        }
        catch(Exception e){e.printStackTrace();}
        
       return res;
        
    }
    
     public String returnHTML(){
        String res = "";
        try{
               URL url = new URL(endpoint);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 
		// optional default is GET
               con.setDoOutput(true);
               con.setRequestMethod("GET");
            String data = URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8");               
               
                OutputStreamWriter osw = new OutputStreamWriter(
                con.getOutputStream());
                osw.write(data);
                osw.flush();



                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String in = "";
                StringBuffer sb = null;
                sb = new StringBuffer();

                while ((in = br.readLine()) != null) {
                    sb.append(in + "\n");
                }

                osw.close();
                br.close();   
           res = sb.toString();
         return res;
        }
        catch(Exception e){e.printStackTrace();}
        
       return res;
        
    }
    
}
