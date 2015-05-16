/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.rest.webservice;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author paolo
 */
@Path("/ShowLog")
public class ShowLog {
    
    	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getLog() {
            
            String ret ="<html><body>";
	            FileInputStream fis;
                try {
                    fis = new FileInputStream("/var/lib/openshift/5528e950fcf93381de00012a/app-root/data/ProvaTomcat.log");
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                    String line = null;
                    while ((line = br.readLine()) != null) {
                            ret += line+"</br>";
                    }

                    br.close();
                    
                } catch (Exception ex) {
                    Logger.getLogger(ShowLog.class.getName()).log(Level.SEVERE, null, ex);
                }
            ret += "</body></html>";
            return ret;
     	}
    
}
