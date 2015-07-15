package it.rest.webservice;

import it.rest.utility.GetValues;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/RicercaHotel")  //anche qui come in ricerca luoghi usiamo i query params
public class RicercaHotel {
         GetValues get = new GetValues();
         @GET
         @Produces({MediaType.APPLICATION_JSON})
         public String getRistorantiJSON( 
                 @QueryParam("lat") double latitudine,
                 @QueryParam("long") double longitudine,
                 @DefaultValue("0.04") @QueryParam("prec") double prec)
         {
             return get.getHotelJSON(latitudine, longitudine, prec).toString();
         }
         
         @GET
         @Produces({MediaType.TEXT_HTML})
         public String getRistorantiHTML( 
                 @QueryParam("lat") double latitudine,
                 @QueryParam("long") double longitudine,
                 @DefaultValue("0.04") @QueryParam("prec") double prec)
         {
             return get.getHotelHTML(latitudine, longitudine, prec);
             //return get.getHotelJSON(latitudine, longitudine, prec).toString();
         }
    
}
