/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.rest.webservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author paolo
 */
@Path("/path")
public class secondopath {
    	 
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPeopleBrowser() {
		return "<html><head></head><body>CHIUSO PURE QUA</body></html>";
	}
	// restituzione lista in formato XML/JSON per chiamate applicative. 
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public JSONObject getPeople() throws JSONException {
		JSONObject j = new JSONObject();
                j.append("path", "2");
                return j;
	}
}
