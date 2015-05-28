/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.rest.utility;

import it.rest.webservice.InfoPersonaggio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author paolo
 */
public class GetValues {
   
   public String ServerTomcat = "http://server-footsteps.rhcloud.com";
   public String infoPersonaggio = "/ProvaTomcat-1.0-SNAPSHOT/rest/InfoPersonaggio/";
   public String endpoint = "http://sandbox.fusepool.info:8181/sparql/select";
   
   private static Logger logger = Logger.getLogger(GetValues.class);
   private final String log4jProp="/var/lib/openshift/5528e950fcf93381de00012a/app-root/data/propLog.xml";
   
   public GetValues(){
       DOMConfigurator.configure(log4jProp);
       logger.debug("Creazione oggeto GetValues");
   }
   
   
            String queryPersonaggiStorici = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX schema: <http://schema.org/>\n" +
                "PREFIX dbo: <http://www.dbpedia.org/ontology/>\n" +
                "PREFIX fam: <http://vocab.fusepool.info/fam#>\n" +
                "\n" +
                "SELECT DISTINCT ?person ?name ?job \n"+ 
                "FROM <http://sandbox.fusepool.info:8181/ldp/historical-characters/personaggi_storici_trentino-refine-csv-csv-transformed>" +
                "\n WHERE {\n" +
                "  ?person a schema:Person ;\n" +
                "             schema:name ?name ;\n" +
                "              schema:jobTitle ?job ; \n" +    
                "             fam:entity-reference ?ref . \n"+
                "} \n ORDER BY ?name";
            
    
   public String getPersonaggiStoriciJSON(){
       logger.info("ListaPersonaggi JSON");
       
       
       String res ="";

       Sparql sp = new Sparql(queryPersonaggiStorici, endpoint);
       JSONObject result = new JSONObject(sp.returnJSON());
       JSONArray jarray = result.getJSONObject("results").getJSONArray("bindings");
       JSONArray arrayRisultati = new JSONArray(); //riscrivamo i risultati in maniera piu bella
       for(int i=0; i < jarray.length(); i++)
       {
           JSONObject row = new JSONObject();
           String uri = jarray.getJSONObject(i).getJSONObject("person").getString("value");
           String name = jarray.getJSONObject(i).getJSONObject("name").getString("value");
           String job = "";
           if(jarray.getJSONObject(i).has("job"))
                   job = jarray.getJSONObject(i).getJSONObject("job").getString("value");
           row.accumulate("URI", uri);
           row.accumulate("name", name);
           row.accumulate("icona", Place.returnJobIcona(job));
           row.accumulate("detailEndpoint",ServerTomcat+infoPersonaggio+uri.replace("http://www.trentinocultura.net/asp_cat/main.asp?", ""));
           arrayRisultati.put(row);
           
       }
       
       logger.info("Restituiti "+arrayRisultati.length()+" personaggi storici");
       
//       return sp.returnJSON();
       return arrayRisultati.toString();
   }
   
   public String getPersonaggiStoriciHTML(){
       logger.info("ListaPersonaggi HTML");
       
       String res ="";

       Sparql sp = new Sparql(queryPersonaggiStorici, endpoint);
       return sp.returnHTML();
   }
   public String getInfoPersonaggioHTML(String URI){
       
       logger.info("InfoPersonaggioHTML" +"richiesto:"+URI);
       
              String queryPersonaggio = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "PREFIX schema: <http://schema.org/>\n" +
        "PREFIX dbo: <http://www.dbpedia.org/ontology/>\n" +
        "PREFIX fam: <http://vocab.fusepool.info/fam#>\n" +
        "\n" +
        "SELECT ?name ?title ?birthPlace ?deathPlace ?sameAs ?description ?reference \n" +
        "FROM <http://sandbox.fusepool.info:8181/ldp/historical-characters/personaggi_storici_trentino-refine-csv-csv-transformed>\n" +
        "WHERE {\n" +
        "<"+URI+"> schema:name ?name."+
        "OPTIONAL { <"+URI+"> schema:jobTitle ?title }"+
        "OPTIONAL { <"+URI+"> schema:birthPlace ?birthPlace }"+
        "OPTIONAL { <"+URI+"> schema:deathPlace ?deathPlace }"+               
        "OPTIONAL { <"+URI+"> schema:sameAs ?sameAs }"+  
        "OPTIONAL { <"+URI+"> schema:description ?description }"+   
        "OPTIONAL { <"+URI+"> fam:entity-reference ?reference }"+  
                "}";
              Sparql sp = new Sparql(queryPersonaggio,endpoint);
              
              logger.info("Restituito InfoPersonaggio HTML");
              return sp.returnHTML();
        
   }
   
   
   public String getInfoPersonaggioJSON(String URI){
        logger.info("InfoPersonaggioJSON " +"richiesto:"+URI);
       
              String queryPersonaggio = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "PREFIX schema: <http://schema.org/>\n" +
        "PREFIX dbo: <http://www.dbpedia.org/ontology/>\n" +
        "PREFIX fam: <http://vocab.fusepool.info/fam#>\n" +
        "\n" +
        "SELECT ?name ?title ?birthPlace ?deathPlace ?sameAs ?description \n" +
        "FROM <http://sandbox.fusepool.info:8181/ldp/historical-characters/personaggi_storici_trentino-refine-csv-csv-transformed>\n" +
        "WHERE {\n" +
        "<"+URI+"> schema:name ?name."+
        "OPTIONAL { <"+URI+"> schema:jobTitle ?title }"+
        "OPTIONAL { <"+URI+"> schema:birthPlace ?birthPlace }"+
        "OPTIONAL { <"+URI+"> schema:deathPlace ?deathPlace }"+               
        "OPTIONAL { <"+URI+"> schema:sameAs ?sameAs }"+  
        "OPTIONAL { <"+URI+"> schema:description ?description }"+    
                "}";
       
       Sparql sp = new Sparql(queryPersonaggio,endpoint);
       JSONObject result = new JSONObject(sp.returnJSON());
       JSONArray jarray = result.getJSONObject("results").getJSONArray("bindings");       

       //qui per non creare casini assurdi i reference li prendiamo a parte con un'altra query
       
       String queryReference ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX schema: <http://schema.org/>\n" +
            "PREFIX dbo: <http://www.dbpedia.org/ontology/>\n" +
            "PREFIX fam: <http://vocab.fusepool.info/fam#>\n" +
            "\n" +
            "SELECT ?ref\n" +
            "FROM <http://sandbox.fusepool.info:8181/ldp/historical-characters/personaggi_storici_trentino-refine-csv-csv-transformed>\n" +
            "WHERE {\n" +
            "  <"+URI+"> a schema:Person ;\n" +
            "            fam:entity-reference ?ref .\n" +
            "}";
       
       sp = new Sparql(queryReference,endpoint);
       JSONObject resultReference = new JSONObject(sp.returnJSON());
       JSONArray jarrayReference = resultReference.getJSONObject("results").getJSONArray("bindings");
       JSONArray reference =new JSONArray(); //il jsonArray dei Reference
         //analizziamo se ci sono reference
       Set<String> listaReference =new HashSet<String>();
       if(jarrayReference.length() > 0){
       int contaRef = 0;
           for(int i = 0; i < jarrayReference.length(); i++)
           {
               String ref = Place.converterWikiToDBpedia(jarrayReference.getJSONObject(i).getJSONObject("ref").getString("value"));
               listaReference.add(ref);
               reference.put(contaRef++, ref);
               
           }
       }
              

       JSONObject json = jarray.getJSONObject(0);
       JSONObject res = new JSONObject();
       
       String[] head = {"title","name","birthPlace","deathPlace","sameAs","description"};
       for(int i=0; i< head.length; i++)
       {

           
           if(i == 2){
               if(json.has("birthPlace")){
                  listaReference.add((json.getJSONObject("birthPlace").getString("value")));
                  res.put("birthPlace", Place.removeDBpedia(json.getJSONObject("birthPlace").getString("value"))); 
                  reference.put(reference.length(),json.getJSONObject("birthPlace").getString("value"));
               }
           }
           else if(i == 3){
               if(json.has("deathPlace")){
                   listaReference.add((json.getJSONObject(head[i]).getString("value")));     
                   res.accumulate("deathPlace", Place.removeDBpedia(json.getJSONObject("deathPlace").getString("value"))); 
                   reference.put(reference.length(),json.getJSONObject("deathPlace").getString("value"));
               }
           }
           else{
            if(json.has(head[i]))
               res.accumulate(head[i], json.getJSONObject(head[i]).getString("value"));  
           }
           
       }
       
       if(jarrayReference.length() > 0)
         res.put("reference", reference);
       
       //res.put("controllo",listaReference.toString());
       
       JSONArray placeArray = new JSONArray(); //qui andranno i reference solo in trentino
       //recuperiamo i posti del trentino
       List<String> placeTrentino = (new Place()).getTrentinoPlace();
          //controlliamo se i reference del cristiano sono in trentino
       int contaRef = 0;
       if(listaReference.size() > 0){
       Place pl = new Place();

       for(String s : listaReference)
         {
             if(placeTrentino.contains(s))
                   placeArray.put(contaRef++, pl.getGeo(s));
         }
       }
       
      if(placeArray.length() > 0)
          // res.accumulate("place", placeArray);
           res.put("place", placeArray);
      
     
      logger.info("Restituito InfoPersonaggio JSON "+json.has("name"));
       

        return res.toString();
       
   }//fine getInfoPersonaggioJSON()
   
   public JSONArray getLuoghiInteresse(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaLuoghi JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);
        String query = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                        "\n" +
                        "SELECT DISTINCT ?label ?lat ?long ?description\n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-architectural-cultural-heritage-enriched-ttl>\n" +
                        "WHERE {\n" +
                        "  ?building a schema:TouristAttraction ;\n" +
                        "              rdfs:label ?label;\n" +
                        "              dct:description ?description; \n"+
                        "              geo:lat ?lat ;\n" +
                        "              geo:long ?long .\n" +
                        "              \n" +
                        "  FILTER (?lat >= \""+(latitudine-prec)+"\"^^xsd:double && ?lat <= \""+(latitudine+prec)+"\"^^xsd:double && ?long >= \""+(longitudine-prec)+"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +
                        "  FILTER (!sameTerm(\"CASA\", ?description))"+
                        "}";
        Sparql sp = new Sparql(query, endpoint);
        JSONArray res = new JSONArray();
        JSONArray jarrayRes = (new JSONObject(sp.returnJSON())).getJSONObject("results").getJSONArray("bindings");
        
        for(int i=0; i < jarrayRes.length(); i++)
        {
            JSONObject json = new JSONObject();
            json.accumulate("description", jarrayRes.getJSONObject(i).getJSONObject("description").getString("value"));
            json.accumulate("label",jarrayRes.getJSONObject(i).getJSONObject("label").getString("value"));
            json.accumulate("long",jarrayRes.getJSONObject(i).getJSONObject("long").getDouble("value"));
            json.accumulate("lat",jarrayRes.getJSONObject(i).getJSONObject("lat").getDouble("value"));
            res.put(json);            
        }
        
        logger.info("RicercaLuoghi JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec + " restituiti: "+jarrayRes.length()+" luoghi");

        
        return res;
    }
   
    public String getLuoghiInteresseHTML(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaLuoghi HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);
        String query = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                        "\n" +
                        "SELECT DISTINCT ?label ?lat ?long ?description\n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-architectural-cultural-heritage-enriched-ttl>\n" +
                        "WHERE {\n" +
                        "  ?building a schema:TouristAttraction ;\n" +
                        "              rdfs:label ?label;\n" +
                        "              dct:description ?description; \n"+
                        "              geo:lat ?lat ;\n" +
                        "              geo:long ?long .\n" +
                        "              \n" +
                        "  FILTER (?lat >= \""+(latitudine-prec)+"\"^^xsd:double && ?lat <= \""+(latitudine+prec)+"\"^^xsd:double && ?long >= \""+(longitudine-prec)+"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +
                        "  FILTER (!sameTerm(\"CASA\", ?description))"+
                        "}";
        Sparql sp = new Sparql(query, endpoint);
       
        logger.info("RicercaLuoghi HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec + "RISPOSTO");

        
        return sp.returnHTML();
    }
    
    
    public JSONArray getRistorantiJSON(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaRistoranti JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);

        
        //credenziali YELP
            String CONSUMER_KEY = "Q87CAjXrI_s3k1LbbIEe9Q";
            String CONSUMER_SECRET = "9TflxKNwqGLNZ9IW_qJbd3SPc_4";
            String TOKEN = "nHwsQ0V0eok71taAjSZ_f4sE7DPe28Te";
            String TOKEN_SECRET = "8HwHjAYPqdGN-6dPW8OsPx7D3g0";
        //oggetto yelpApi
        YelpAPI yelpApi = new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
            
        
        
        String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "\n" +
                        "SELECT ?name ?locality ?street ?locality ?lat ?long \n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-restaurants-1/osterie-csv-csv-transformed-1>\n" +
                        "{\n" +
                        "  ?restaurant a schema:Restaurant ;  \n" +
                        "         schema:name ?name ;\n" +
                        "         schema:address ?address ;\n" +
                        "         schema:geo ?geo .\n" +
                        " \n" +
                        "  ?geo a schema:GeoCoordinates ;\n" +
                        "         schema:latitude ?lat ;\n" +
                        "         schema:longitude ?long .\n" +
                        " \n" +
                        "  ?address a schema:PostalAddress ;\n" +
                        "         schema:streetAddress ?street ;\n" +
                        "         schema:addressLocality ?locality .\n" +
                        " \n" +
                        "FILTER (?lat >= \""+(latitudine-prec)+"\"^^xsd:double && ?lat <= \""+(latitudine+prec)+"\"^^xsd:double && ?long >= \""+(longitudine-prec)+"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +
                        "} ORDER BY ?lat";
        
        Sparql sp = new Sparql(query, endpoint);
        
        JSONArray res = new JSONArray();
        JSONArray jarrayRes = (new JSONObject(sp.returnJSON())).getJSONObject("results").getJSONArray("bindings");
        
        //dati da recuperare da yelp
        String[] datiYelp = {"rating","snippet_text","image_url","phone"};
        
        for(int i=0; i < jarrayRes.length(); i++)
        {
            JSONObject json = new JSONObject();
            json.accumulate("name", jarrayRes.getJSONObject(i).getJSONObject("name").getString("value"));
            json.accumulate("street",jarrayRes.getJSONObject(i).getJSONObject("street").getString("value"));
            json.accumulate("citta",jarrayRes.getJSONObject(i).getJSONObject("locality").getString("value"));
            json.accumulate("lat",jarrayRes.getJSONObject(i).getJSONObject("lat").getDouble("value"));
            json.accumulate("long",jarrayRes.getJSONObject(i).getJSONObject("long").getDouble("value"));
            JSONObject ristor = yelpApi.searchRistorante(yelpApi, jarrayRes.getJSONObject(i).getJSONObject("name").getString("value"), 
                    jarrayRes.getJSONObject(i).getJSONObject("lat").getString("value")+","+
                    jarrayRes.getJSONObject(i).getJSONObject("long").getString("value"));
            for(String d : datiYelp)
            {
                if(ristor.has(d))
                    json.put(d, String.valueOf(ristor.get(d)));
                
            }
            res.put(json);            
        }
        
        logger.info("RicercaRistoranti JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec + " restituiti: "+jarrayRes.length()+ " ristoranti");
        
        return res;
    }  //fine getRistorantiJSON  
    
    public String getRistorantiHTML(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaRistoranti HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);

        String query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "\n" +
                        "SELECT ?name ?locality ?street ?locality ?lat ?long \n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-restaurants-1/osterie-csv-csv-transformed-1>\n" +
                        "{\n" +
                        "  ?restaurant a schema:Restaurant ;  \n" +
                        "         schema:name ?name ;\n" +
                        "         schema:address ?address ;\n" +
                        "         schema:geo ?geo .\n" +
                        " \n" +
                        "  ?geo a schema:GeoCoordinates ;\n" +
                        "         schema:latitude ?lat ;\n" +
                        "         schema:longitude ?long .\n" +
                        " \n" +
                        "  ?address a schema:PostalAddress ;\n" +
                        "         schema:streetAddress ?street ;\n" +
                        "         schema:addressLocality ?locality .\n" +
                        " \n" +
                        "FILTER (?lat >= \""+(latitudine-prec)+"\"^^xsd:double && ?lat <= \""+(latitudine+prec)+"\"^^xsd:double && ?long >= \""+(longitudine-prec)+"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +
                        "} ORDER BY ?lat";
        
        Sparql sp = new Sparql(query, endpoint);
        logger.info("RicercaRistoranti HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec+" FINITO");
        return sp.returnHTML();
    }  //fine getRistorantiHTML
    
    public String getHotelHTML(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaHotel HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);
        
        String query =  "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                        "\n" +
                        "SELECT DISTINCT ?h ?name ?category ?lat ?long ?comment ?phone ?loc ?via ?sito ?mail\n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-point-of-interest-ttl>\n" +
                        "WHERE {\n" +
                        "  VALUES ?category{\n" +
                        "    \"Bed & Breakfast\"\n" +
                        " 	\"Hotel\"\n" +
                        "        }\n" +
                        "  ?h schema:category ?category;\n" +
                        " 		rdfs:label  ?name;\n" +
                        " 		geo:lat ?lat;\n" +
                        " 		geo:long ?long.\n" +
                        "	OPTIONAL{ ?h rdfs:comment ?comment.\n" +
                        "               FILTER LANGMATCHES(LANG(?comment),  \"IT\")}\n" +
                        "  OPTIONAL { ?h  foaf:homepage ?sito. }\n" +
                        "  OPTIONAL { ?h  foaf:mbox ?mail. }\n" +
                        "  OPTIONAL { ?h foaf:phone ?phone. }\n" +
                        "  OPTIONAL { ?h  schema:streetAddress ?address .\n" +
                        "             ?address  schema:addressLocality ?loc;\n" +
                        "                       schema:streetAddress ?via.\n" +
                        "           }\n" +
                        "FILTER (?lat >= \""+(latitudine-prec)
                        +"\"^^xsd:double && ?lat <= \""+(latitudine+prec)
                        +"\"^^xsd:double && ?long >= \""+(longitudine-prec)
                        +"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +                
                        "  }";
        
        Sparql sp = new Sparql(query, endpoint);
        logger.info("RicercaHotel HTML "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec+ "FINITO");
        return sp.returnHTML();        
    }//fine getHotelHTML()
    
    public JSONArray getHotelJSON(double latitudine, double longitudine, double prec)
    {
        logger.info("RicercaHotel JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec);
        String query =  "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                        "PREFIX schema: <http://schema.org/>\n" +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                        "\n" +
                        "SELECT DISTINCT ?h ?name ?category ?lat ?long ?comment ?phone ?loc ?via ?sito ?mail\n" +
                        "FROM <http://sandbox.fusepool.info:8181/ldp/trentino-point-of-interest-ttl>\n" +
                        "WHERE {\n" +
                        "  VALUES ?category{\n" +
                        "    \"Bed & Breakfast\"\n" +
                        " 	\"Hotel\"\n" +
                        "        }\n" +
                        "  ?h schema:category ?category;\n" +
                        " 		rdfs:label  ?name;\n" +
                        " 		geo:lat ?lat;\n" +
                        " 		geo:long ?long.\n" +
                        "	OPTIONAL{ ?h rdfs:comment ?comment.\n" +
                        "               FILTER LANGMATCHES(LANG(?comment),  \"IT\")}\n" +
                        "  OPTIONAL { ?h  foaf:homepage ?sito. }\n" +
                        "  OPTIONAL { ?h  foaf:mbox ?mail. }\n" +
                        "  OPTIONAL { ?h foaf:phone ?phone. }\n" +
                        "  OPTIONAL { ?h  schema:streetAddress ?address .\n" +
                        "             ?address  schema:addressLocality ?loc;\n" +
                        "                       schema:streetAddress ?via.\n" +
                        "           }\n" +
                        "FILTER (?lat >= \""+(latitudine-prec)
                        +"\"^^xsd:double && ?lat <= \""+(latitudine+prec)
                        +"\"^^xsd:double && ?long >= \""+(longitudine-prec)
                        +"\"^^xsd:double && ?long <= \""+(longitudine+prec)+"\"^^xsd:double)\n" +                
                        "  }";
        
        Sparql sp = new Sparql(query, endpoint);
         //array dei risultati finali
        JSONArray res = new JSONArray();
        //mettiamo tutti i risultati in questo JSONArray
        JSONArray jarrayRes = (new JSONObject(sp.returnJSON())).getJSONObject("results").getJSONArray("bindings");
        String[] campiHotel = { "name", "category", "lat", "long", "comment", "phone", "loc","via", "sito",  "mail"};
        for(int i=0; i < jarrayRes.length(); i++)
        {
            JSONObject hotel =  jarrayRes.getJSONObject(i);
            JSONObject json = new JSONObject();
            for(String c : campiHotel)
            {
                if(hotel.has(c)){
                    if(c.equals("name")){
                        if(Place.extractStar(hotel.getJSONObject(c).getString("value")) > 0)  //se ha almeno 1 stella senno che cazz d hotel è aaaaaaa forse è un B&B
                            json.put("stelle", Place.extractStar(hotel.getJSONObject(c).getString("value")));
                        json.put(c,Place.removeStar(hotel.getJSONObject(c).getString("value")));
                    }
                    else{
                        if(c.equals("lat") || c.equals("long")){
                            json.put(c, (hotel.getJSONObject(c).getDouble("value"))); }                           
                     
                        else{     
                            json.put(c, String.valueOf(hotel.getJSONObject(c).get("value")));}
                    }
                }
                   
                
            }
            res.put(json);            
        }
        
        logger.info("RicercaHotel JSON "+"richiesta per lat:"+latitudine+" long:"+longitudine+ " precisione: "+prec+" restituiti: "+jarrayRes.length()+" hotel");
        return res;
    }  
    // fine getHotelJSON()   
    
    
    
}
