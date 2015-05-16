package sisinflab.inthefootsteps;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PersonaggioActivity extends ActionBarActivity {

    String descrizione;
    String titolo;
    JSONArray placeArray;

    TextView descrizioneView;
    TextView titoloView;
    TextView nascitaMorteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaggio);

        Intent i = getIntent();
        String personaggio = i.getStringExtra("personaggio");
        String uriPersonaggio = i.getStringExtra("uriPersonaggio");
        setTitle(personaggio);

        new RecuperaDettagli().execute(uriPersonaggio);
        //Toast.makeText(getApplicationContext(),descrizione, Toast.LENGTH_LONG).show();
    }

    public void apriMappa(View v){
        Intent i = new Intent(getApplicationContext(), MapActivity.class);
        i.putExtra("placeArrayString", placeArray.toString());
        startActivity(i);
    }



    /**
     * Classe per recuperare la lista di personaggi
     */
    private class RecuperaDettagli extends AsyncTask<String, Void, JSONObject> {
        /**
         * Metodo che opera in background e recupera i dettagli del personaggio
         *
         * @param endpoint stringa contenente l'URL dell'endpoint
         * @return JSONArray restituito dalla query
         */
        protected JSONObject doInBackground(String... endpoint) {


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

                JSONObject jO = new JSONObject(sb.toString());
                //JSONArray jA = new JSONArray(sb.toString());
                return jO;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * Eseguito al termine del metodo doInBackground(String...).
         *
         * @param jsonObject ottenuto dalla query nel metodo doInBackground()
         */
        protected void onPostExecute(JSONObject jsonObject) {

            if (jsonObject==null){
                Toast erroreConnessione = Toast.makeText(getApplicationContext(), "Connessione al server non riuscita, controlla la connessione a internet e riprova più tardi.", Toast.LENGTH_LONG);
                erroreConnessione.setGravity(Gravity.NO_GRAVITY,0,0);
                erroreConnessione.show();
                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressPersonaggio);
                mProgress.setVisibility(View.GONE);
                return;
            }

            try {
                if (jsonObject.has("description")){
                    descrizione = jsonObject.getString("description");
                    //Toast.makeText(getApplicationContext(),descrizione, Toast.LENGTH_LONG).show();
                    descrizioneView = (TextView) findViewById(R.id.descrizioneText);
                    descrizioneView.setText(descrizione);
                    descrizioneView.setMovementMethod(new ScrollingMovementMethod());
                }
                if (jsonObject.has("title")){
                    titolo = jsonObject.getString("title");
                    titoloView = (TextView) findViewById(R.id.titoloText);
                    titoloView.setText(titolo);
                }

                String nascitaMorte="";

                if (jsonObject.has("birthPlace")){
                    String nascita = jsonObject.getString("birthPlace");
                    nascitaMorte += nascita;
                }

                if (jsonObject.has("deathPlace")){
                    String morte = jsonObject.getString("deathPlace");
                    nascitaMorte += "\n" + morte;
                }

                nascitaMorteView = (TextView) findViewById(R.id.nascitaMorteText);
                nascitaMorteView.setText(nascitaMorte);

                if (jsonObject.has("place")){

                    placeArray = jsonObject.getJSONArray("place");

                    Button mappaButton = (Button) findViewById(R.id.buttonMappa);
                    mappaButton.setVisibility(View.VISIBLE);

                }

                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressPersonaggio);
                mProgress.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
