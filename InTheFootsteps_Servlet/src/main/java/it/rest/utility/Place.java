package it.rest.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Place {
    public String endpoint = "http://sandbox.fusepool.info:8181/sparql/select";
    public String endpointPoliba = "http://193.204.59.21:8890/sparql";
    public String endpointDBpedia = "http://it.dbpedia.org/sparql";
    
    private static final Logger logger = Logger.getLogger(Place.class); 
    
    public static String converterWikiToDBpedia(String link){
       return link.replaceAll("it.wikipedia.org/wiki","it.dbpedia.org/resource");
    }
    
    public static String removeDBpedia(String link){
        return link.replaceAll("http://it.dbpedia.org/resource/","");
    }            
    
    public static String removeWiki(String link){
       return link.replace("it.wikipedia.org/wiki","");
    }
    
    public static int extractStar(String nome){
        return StringUtils.countMatches(nome, "*");
    }
    
    public static String removeStar(String nome){
        return nome.replaceAll("\\*", "");
    }
    
    public static String returnJobIcona(String job)
    {
        job = job.toLowerCase();
        String icona = "none";
        if(job.contains("sacerdote") || job.contains("domenicano") || job.contains("vescovo") || job.contains("prete") || job.contains("gesuita") || job.contains("missionario") || job.contains("papa") || job.contains("martire") || job.contains("abate") )
            icona = "chiesa";
        if(job.contains("poeta") || job.contains("scrittore") || job.contains("giornalista") || job.contains("letterario") || job.contains("letterato") || job.contains("bibliotec") || job.contains("umanista") || job.contains("filosofo"))
            icona = "penna";
        if(job.contains("artista") || job.contains("pittore") || job.contains("scultore"))
            icona = "arte";
        if(job.contains("duca") || job.contains("imperat") || job.contains("conte") || job.contains("re") || job.contains("barone")    )
            icona = "nobile";
        if(job.contains("deputato") || job.contains("senat") || job.contains("prefetto") || job.contains("parlamentare"))
            icona = "parlamento";
        return icona;
    }
    
    public List<String> getTrentinoPlace(){
        List<String> Place = new ArrayList<String>();
        String[] luoghi = {"<http://it.dbpedia.org/resource/Trentino-Alto_Adige>","<http://it.dbpedia.org/resource/Trento>","<http://it.dbpedia.org/resource/Bolzano>"};
        String query = "SELECT DISTINCT ?place \n" +
          "WHERE{\n" ;            
        // altre citta  da qui  usiamo i fragments
        query += "{ ?place <http://dbpedia.org/ontology/wikiPageWikiLink> <http://it.dbpedia.org/resource/Provincia_di_Trento> ;"
                + "                <http://airpedia.org/typeWithConfidence#1> <http://dbpedia.org/ontology/Place> . "
                + "} \n" ;
        
        for(String l : luoghi)
        {
           query += "UNION { ?place <http://dbpedia.org/ontology/administrativeDistrict> "+l+" . } \n" ;
        }
        query += "FILTER (!CONTAINS('Provincia',?place))";
        query += "\n}";
        
        logger.info("Richiesti TrentinoPlace");

        Sparql sp = new Sparql(query, endpointDBpedia);
        JSONObject res = new JSONObject(sp.returnJSON());
        JSONArray jarray = res.getJSONObject("results").getJSONArray("bindings");
        for(int i=0; i < jarray.length(); i++)
        {
            Place.add(jarray.getJSONObject(i).getJSONObject("place").getString("value"));
        }   
        logger.info("Restituite "+jarray.length()+" località del Trentino");
        return Place;
    }
    
    public JSONObject getGeo(String uri){
        logger.info("Richieste coordinate per "+uri);
        
        JSONObject json = new JSONObject();
        String query;
        query = "PREFIX dbpprop-it:" + "<http://it.dbpedia.org/property/>" + " \n" +
                "PREFIX rdfs:" + "<http://www.w3.org/2000/01/rdf-schema#>" + " \n" +
                "SELECT ?latG ?latM ?latS ?longG ?longM ?longS ?label \n" +
                "WHERE\n" +
                "{\n <" + uri +"> dbpprop-it:latitudineGradi ?latG.\n" +
                "<"+ uri +"> dbpprop-it:latitudineMinuti ?latM.\n" +
                "<"+ uri +"> dbpprop-it:latitudineSecondi ?latS.\n" +
                "<"+ uri +"> dbpprop-it:longitudineGradi ?longG.\n" +
                "<"+ uri +"> dbpprop-it:longitudineMinuti ?longM.\n" +
                "<"+ uri +"> dbpprop-it:longitudineSecondi ?longS.\n" +
                "<"+ uri +"> rdfs:label ?label.\n" +
                "}";
        
        Sparql sp = new Sparql(query, endpointDBpedia);
        JSONObject res = new JSONObject(sp.returnJSON());
        
        JSONArray jarray = res.getJSONObject("results").getJSONArray("bindings");
        
        //String[] campi = {"latG","latM","latS","longG","longM","longS","label"};
        int i = 0;
            int latG = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("latG").getString("value"));
            int latM = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("latM").getString("value"));
            int latS = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("latS").getString("value"));
            int longG = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("longG").getString("value"));
            int longM = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("longM").getString("value"));
            int longS = Integer.parseInt(jarray.getJSONObject(i).getJSONObject("longS").getString("value"));
            String label = jarray.getJSONObject(i).getJSONObject("label").getString("value");
       json.accumulate("label", label);
       json.accumulate("uri", uri);
       json.accumulate("lat", getDecimalGeo(latG,latM,latS));
       json.accumulate("long", getDecimalGeo(longG,longM,longS));
        
       //return jarray.getJSONObject(0).toString();
       return json;
    }
    
    public static double getDecimalGeo(int degrees, int minutes, int seconds){
                return ((double)degrees)+(((double)(minutes))/60)+(((double)seconds)/3600);
    }
    
}
