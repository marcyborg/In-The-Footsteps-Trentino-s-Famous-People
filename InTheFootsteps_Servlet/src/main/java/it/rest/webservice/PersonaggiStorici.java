package it.rest.webservice;

import it.rest.utility.GetValues;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/listapersonaggi")
public class PersonaggiStorici {
        GetValues get = new GetValues();
       	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPeopleBrowser() {
		return get.getPersonaggiStoriciHTML();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public String getPeople() {
		return get.getPersonaggiStoriciJSON();
	}
}
