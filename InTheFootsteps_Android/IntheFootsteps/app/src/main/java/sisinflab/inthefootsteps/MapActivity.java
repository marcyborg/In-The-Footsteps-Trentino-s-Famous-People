package sisinflab.inthefootsteps;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback {

    JSONArray placeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent i = getIntent();
        try {
            placeArray = new JSONArray(i.getStringExtra("placeArrayString"));
        } catch (JSONException e) {
            placeArray = new JSONArray();
            e.printStackTrace();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
/*        LatLng sydney = new LatLng(-33.867, 151.206);
        LatLng trento = new LatLng(46.0804614,11.1203557);
        LatLng lucera = new LatLng(41.5052419,15.3474605);

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));

        map.addMarker(new MarkerOptions()
                .title("Trento")
                .snippet("Trentatre trentini entrarono a Trento, tutti e trentatre trotterellando.")
                .position(trento));

        map.addMarker(new MarkerOptions()
                .title("Lucera")
                .snippet("Lunonc'èpiù")
                .position(lucera));
*/
       mostraCittà(null);

        map.setMyLocationEnabled(true);
        try {
            LatLng place = new LatLng(placeArray.getJSONObject(0).getDouble("lat"),placeArray.getJSONObject(0).getDouble("long"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 10));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(lucera, 13));
    }

    public void mostraCittà(View v){
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
        map.clear();

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
        mProgress.setVisibility(View.VISIBLE);

        if (placeArray.length()>0){
            for (int i=0; i<placeArray.length(); i++){
                try {
                    LatLng place = new LatLng(placeArray.getJSONObject(i).getDouble("lat"),placeArray.getJSONObject(i).getDouble("long"));
                    map.addMarker(new MarkerOptions()
                            .title(placeArray.getJSONObject(i).getString("label"))
                            .position(place)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mProgress.setVisibility(View.GONE);
    }

    public void mostraPOI(View v){
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
        map.clear();
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
        mProgress.setVisibility(View.VISIBLE);
        LatLng centroMappa = map.getCameraPosition().target;
        //map.addMarker(new MarkerOptions().title("centro").position(centroMappa));

        LatLngBounds visibile = map.getProjection().getVisibleRegion().latLngBounds;
        double distanzaVisibile = Math.sqrt((visibile.northeast.latitude - visibile.southwest.latitude) * (visibile.northeast.latitude - visibile.southwest.latitude) +
                (visibile.northeast.longitude - visibile.southwest.longitude) * (visibile.northeast.longitude - visibile.southwest.longitude))/2;

        String endpointPOI = MainActivity.ENDPOINT_LUOGHI_INTERESSE + "/?lat=" + centroMappa.latitude + "&long=" + centroMappa.longitude + "&prec=" + distanzaVisibile;

        new RecuperaPOI().execute(endpointPOI);

    }

    public void mostraRistoranti(View v){
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
        map.clear();
        LatLng centroMappa = map.getCameraPosition().target;

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
        mProgress.setVisibility(View.VISIBLE);

        LatLngBounds visibile = map.getProjection().getVisibleRegion().latLngBounds;
        double distanzaVisibile = Math.sqrt((visibile.northeast.latitude - visibile.southwest.latitude) * (visibile.northeast.latitude - visibile.southwest.latitude) +
                (visibile.northeast.longitude - visibile.southwest.longitude) * (visibile.northeast.longitude - visibile.southwest.longitude))/2;

        String endpointPOI = MainActivity.ENDPOINT_RISTORANTI + "/?lat=" + centroMappa.latitude + "&long=" + centroMappa.longitude + "&prec=" + distanzaVisibile;

        new RecuperaRistoranti().execute(endpointPOI);
    }

    public void mostraAlberghi(View v){
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
        map.clear();
        LatLng centroMappa = map.getCameraPosition().target;

        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
        mProgress.setVisibility(View.VISIBLE);

        LatLngBounds visibile = map.getProjection().getVisibleRegion().latLngBounds;
        double distanzaVisibile = Math.sqrt((visibile.northeast.latitude - visibile.southwest.latitude) * (visibile.northeast.latitude - visibile.southwest.latitude) +
                (visibile.northeast.longitude - visibile.southwest.longitude) * (visibile.northeast.longitude - visibile.southwest.longitude))/2;

        String endpointPOI = MainActivity.ENDPOINT_HOTEL + "/?lat=" + centroMappa.latitude + "&long=" + centroMappa.longitude + "&prec=" + distanzaVisibile;

        new RecuperaHotel().execute(endpointPOI);
    }

    public void attivaPulsanti(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ImageButton citta = (ImageButton) findViewById(R.id.buttonCittà);
                citta.setEnabled(true);
                ImageButton poi = (ImageButton) findViewById(R.id.buttonPOI);
                poi.setEnabled(true);
                ImageButton cibo = (ImageButton) findViewById(R.id.buttonRistoranti);
                cibo.setEnabled(true);
                ImageButton hotel = (ImageButton) findViewById(R.id.buttonHotel);
                hotel.setEnabled(true);

            }
        });

    }

    public void disattivaPulsanti(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ImageButton citta = (ImageButton) findViewById(R.id.buttonCittà);
                citta.setEnabled(false);
                ImageButton poi = (ImageButton) findViewById(R.id.buttonPOI);
                poi.setEnabled(false);
                ImageButton cibo = (ImageButton) findViewById(R.id.buttonRistoranti);
                cibo.setEnabled(false);
                ImageButton hotel = (ImageButton) findViewById(R.id.buttonHotel);
                hotel.setEnabled(false);

            }
        });

    }



    /**
     * Classe per recuperare la lista di punti di interesse
     */
    private class RecuperaPOI extends AsyncTask<String, Void, JSONArray> {
        /**
         * Metodo che opera in background e recupera la lista dei punti di interesse
         * @param endpoint stringa contenente l'URL dell'endpoint
         * @return JSONArray restituito dalla query
         */
        protected JSONArray doInBackground(String... endpoint){

            disattivaPulsanti();

            try {
                java.net.URL url = new URL(endpoint[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.addRequestProperty("Accept", "application/json");

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String in = "";
                StringBuffer sb = null;
                sb = new StringBuffer();

                while ((in = br.readLine()) != null) {
                    sb.append(in + "\n");
                }

                //JSONObject jO = new JSONObject( sb.toString());
                JSONArray jA = new JSONArray( sb.toString());
                return  jA;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * Eseguito al termine del metodo doInBackground(String...).
         * Aggiunge i marker sulla mappa
         * @param jsonArray ottenuto dalla query nel metodo doInBackground()
         */
        protected void onPostExecute (JSONArray jsonArray){

            attivaPulsanti();

            if (jsonArray==null){
                Toast erroreConnessione = Toast.makeText(getApplicationContext(), "Connessione al server non riuscita, controlla la connessione a internet e riprova più tardi.", Toast.LENGTH_LONG);
                erroreConnessione.setGravity(Gravity.NO_GRAVITY,0,0);
                erroreConnessione.show();
                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
                mProgress.setVisibility(View.GONE);
                return;
            }

            GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
            map.clear();

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.infowindow_poi, null);

                    TextView info= (TextView) v.findViewById(R.id.descrizionePOI);
                    TextView titolo = (TextView) v.findViewById(R.id.titoloPOI);

                    titolo.setText(marker.getTitle());
                    info.setText(marker.getSnippet());

                    return v;
                }
            });

            if (jsonArray.length()>0){
                for (int i=0; i<jsonArray.length(); i++){
                    try {
                        LatLng place = new LatLng(jsonArray.getJSONObject(i).getDouble("lat"),jsonArray.getJSONObject(i).getDouble("long"));
                        map.addMarker(new MarkerOptions()
                                .title(jsonArray.getJSONObject(i).getString("description"))
                                .snippet(jsonArray.getJSONObject(i).getString("label"))
                                .position(place)
                                .icon(BitmapDescriptorFactory.defaultMarker(15)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
            mProgress.setVisibility(View.GONE);

        }

    }

    /**
     * Classe per recuperare la lista di ristoranti
     */
    private class RecuperaRistoranti extends AsyncTask<String, Void, JSONArray> {
        /**
         * Metodo che opera in background e recupera la lista dei ristoranti
         * @param endpoint stringa contenente l'URL dell'endpoint
         * @return JSONArray restituito dalla query
         */
        protected JSONArray doInBackground(String... endpoint){

            disattivaPulsanti();

            try {
                java.net.URL url = new URL(endpoint[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.addRequestProperty("Accept", "application/json");

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String in = "";
                StringBuffer sb = null;
                sb = new StringBuffer();

                while ((in = br.readLine()) != null) {
                    sb.append(in + "\n");
                }

                //JSONObject jO = new JSONObject( sb.toString());
                JSONArray jA = new JSONArray( sb.toString());
                return  jA;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * Eseguito al termine del metodo doInBackground(String...).
         * Aggiunge i marker sulla mappa
         * @param jsonArray ottenuto dalla query nel metodo doInBackground()
         */
        protected void onPostExecute (JSONArray jsonArray){

            attivaPulsanti();

            if (jsonArray==null){
                Toast erroreConnessione = Toast.makeText(getApplicationContext(), "Connessione al server non riuscita, controlla la connessione a internet e riprova più tardi.", Toast.LENGTH_LONG);
                erroreConnessione.setGravity(Gravity.NO_GRAVITY,0,0);
                erroreConnessione.show();
                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
                mProgress.setVisibility(View.GONE);
                return;
            }

            GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
            map.clear();

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.infowindow_poi, null);

                    TextView info= (TextView) v.findViewById(R.id.descrizionePOI);
                    TextView titolo = (TextView) v.findViewById(R.id.titoloPOI);

                    titolo.setText(marker.getTitle());
                    info.setText(marker.getSnippet());

                    return v;
                }
            });

            if (jsonArray.length()>0){
                for (int i=0; i<jsonArray.length(); i++){
                    try {
                        LatLng place = new LatLng(jsonArray.getJSONObject(i).getDouble("lat"),jsonArray.getJSONObject(i).getDouble("long"));
                        String descrizione="";
                        if (jsonArray.getJSONObject(i).has("rating") && !jsonArray.getJSONObject(i).getString("rating").equals("0.0")){
                            descrizione += "Valutazione: " + jsonArray.getJSONObject(i).getString("rating") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("street")){
                            descrizione += "Indirizzo: " + jsonArray.getJSONObject(i).getString("street") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("phone")){
                            descrizione += "Tel: " + jsonArray.getJSONObject(i).getString("phone") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("snippet_text")){
                            descrizione += jsonArray.getJSONObject(i).getString("snippet_text");
                        }
                        map.addMarker(new MarkerOptions()
                                .title(jsonArray.getJSONObject(i).getString("name"))
                                .snippet(descrizione)
                                .position(place)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
            mProgress.setVisibility(View.GONE);

        }

    }

    /**
     * Classe per recuperare la lista di alberghi
     */
    private class RecuperaHotel extends AsyncTask<String, Void, JSONArray> {
        /**
         * Metodo che opera in background e recupera la lista dei ristoranti
         * @param endpoint stringa contenente l'URL dell'endpoint
         * @return JSONArray restituito dalla query
         */
        protected JSONArray doInBackground(String... endpoint){

            disattivaPulsanti();

            try {
                java.net.URL url = new URL(endpoint[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.addRequestProperty("Accept", "application/json");

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String in = "";
                StringBuffer sb = null;
                sb = new StringBuffer();

                while ((in = br.readLine()) != null) {
                    sb.append(in + "\n");
                }

                //JSONObject jO = new JSONObject( sb.toString());
                JSONArray jA = new JSONArray( sb.toString());
                return  jA;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * Eseguito al termine del metodo doInBackground(String...).
         * Aggiunge i marker sulla mappa
         * @param jsonArray ottenuto dalla query nel metodo doInBackground()
         */
        protected void onPostExecute (JSONArray jsonArray){

            attivaPulsanti();

            if (jsonArray==null){
                Toast erroreConnessione = Toast.makeText(getApplicationContext(), "Connessione al server non riuscita, controlla la connessione a internet e riprova più tardi.", Toast.LENGTH_LONG);
                erroreConnessione.setGravity(Gravity.NO_GRAVITY,0,0);
                erroreConnessione.show();
                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
                mProgress.setVisibility(View.GONE);
                return;
            }

            GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mappaFragment)).getMap();
            map.clear();

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.infowindow_poi, null);

                    TextView info= (TextView) v.findViewById(R.id.descrizionePOI);
                    TextView titolo = (TextView) v.findViewById(R.id.titoloPOI);

                    titolo.setText(marker.getTitle());
                    info.setText(marker.getSnippet());

                    return v;
                }
            });

            if (jsonArray.length()>0){
                for (int i=0; i<jsonArray.length(); i++){
                    try {
                        LatLng place = new LatLng(jsonArray.getJSONObject(i).getDouble("lat"),jsonArray.getJSONObject(i).getDouble("long"));
                        String descrizione="";
                        if (jsonArray.getJSONObject(i).has("category")){
                            descrizione += jsonArray.getJSONObject(i).getString("category") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("stelle")){
                            descrizione += "Stelle: " + jsonArray.getJSONObject(i).getString("stelle") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("via")){
                            descrizione += "Indirizzo: " + jsonArray.getJSONObject(i).getString("via") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("loc")){
                            descrizione += jsonArray.getJSONObject(i).getString("loc") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("sito")){
                            descrizione += jsonArray.getJSONObject(i).getString("sito") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("mail")){
                            descrizione += "e-mail: " + jsonArray.getJSONObject(i).getString("mail") + "\n";
                        }
                        if (jsonArray.getJSONObject(i).has("phone")){
                            descrizione += "Tel: " + jsonArray.getJSONObject(i).getString("phone") + "\n";
                        }
                        //if (jsonArray.getJSONObject(i).has("comment")){
                        //    descrizione += jsonArray.getJSONObject(i).getString("comment");
                        //}
                        map.addMarker(new MarkerOptions()
                                .title(jsonArray.getJSONObject(i).getString("name"))
                                .snippet(descrizione)
                                .position(place)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMap);
            mProgress.setVisibility(View.GONE);

        }

    }

}