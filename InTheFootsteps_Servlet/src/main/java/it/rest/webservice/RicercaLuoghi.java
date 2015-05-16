/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.rest.webservice;

import it.rest.utility.GetValues;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author paolo
 */
@Path("/RicercaLuoghiInteresse")  //questa volta usiamo i query params
public class RicercaLuoghi {
          GetValues get = new GetValues();
                
                
         @GET
         @Produces({MediaType.APPLICATION_JSON})
         public String getLuoghiInteresseJSON( 
                 @QueryParam("lat") double latitudine,
                 @QueryParam("long") double longitudine,
                 @DefaultValue("0.01") @QueryParam("prec") double prec)
         {
             return get.getLuoghiInteresse(latitudine, longitudine, prec).toString();
         }
         
         @GET
         @Produces({MediaType.TEXT_HTML})
         public String getLuoghiInteresseHTML( 
                 @QueryParam("lat") double latitudine,
                 @QueryParam("long") double longitudine,
                 @DefaultValue("0.01") @QueryParam("prec") double prec)
         {
             return get.getLuoghiInteresseHTML(latitudine, longitudine, prec);
         }

}
