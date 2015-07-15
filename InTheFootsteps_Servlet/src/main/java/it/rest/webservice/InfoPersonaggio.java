package it.rest.webservice;

import it.rest.utility.GetValues;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

@Path("/InfoPersonaggio")
public class InfoPersonaggio {
    
            private static Logger logger = Logger.getLogger(InfoPersonaggio.class);
            private final String log4jProp="/var/lib/openshift/5528e950fcf93381de00012a/app-root/data/propLog.xml";
            // private final DOMConfigurator conf=new DOMConfigurator();
    
            GetValues get = new GetValues();
            @GET
            @Path("{uri : .*}")
            @Produces({MediaType.APPLICATION_JSON})
            public String getInfoJSON(@PathParam("uri") String uri){
                
                String URI = "http://www.trentinocultura.net/asp_cat/main.asp?"+uri;
                return get.getInfoPersonaggioJSON(URI);
            }
            
            @GET
            @Path("{uri : .*}")
            @Produces({MediaType.TEXT_HTML})
            public String getInfoHTML(@PathParam("uri") String uri){
                String URI = "http://www.trentinocultura.net/asp_cat/main.asp?"+uri;
                return get.getInfoPersonaggioHTML(URI);
                //return get.getInfoPersonaggioJSON(URI);
            }            
    
}
