package sisinflab.inthefootsteps;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Objects;


public class MainActivity extends ActionBarActivity {

    //static final String ENDPOINT_ROOT = "http://server-footsteps.rhcloud.com/ProvaTomcat-1.0-SNAPSHOT/rest/";
    
    static final String ENDPOINT_ROOT = "http://inthefootsteps.solr.netseven.it/rest/";
    static final String ENDPOINT_PERSONAGGI = ENDPOINT_ROOT + "listapersonaggi";
    static final String ENDPOINT_LUOGHI_INTERESSE = ENDPOINT_ROOT + "RicercaLuoghiInteresse";
    static final String ENDPOINT_RISTORANTI = ENDPOINT_ROOT + "RicercaRistoranti";
    static final String ENDPOINT_HOTEL = ENDPOINT_ROOT + "RicercaHotel";

    String[] personaggi;
    String[] mestiere;
    JSONArray jsonArrayPersonaggi;

    EditText inputSearch;

    //ArrayAdapter<String> adapter;
    IconicAdapter iconicAdapter;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast toast = Toast.makeText(getApplicationContext(),"Attendi il caricamento!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.NO_GRAVITY,0,0);
        toast.show();

        //chiamata asincrona per eseguire la query per ottenere la lista dei nomi
        new RecuperaPersonaggi().execute(ENDPOINT_PERSONAGGI);

        listView = (ListView) findViewById(R.id.listview);

        //Listener che chiama l'intent nel caso di click su un elemento della lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), personaggi[position], Toast.LENGTH_LONG).show();      //no buono
                //Toast.makeText(getApplicationContext(), adapter.getItem(position), Toast.LENGTH_LONG).show(); //buono

                try {
                    //String uriPersonaggio = jsonArrayPersonaggi.getJSONObject(position).getString("detailEndpoint");
                    //Toast.makeText(getApplicationContext(), "i: " + getItemPositionByName(adapter.getItem(position)), Toast.LENGTH_LONG).show();
                    String uriPersonaggio = jsonArrayPersonaggi.getJSONObject(getItemPositionByName(iconicAdapter.getItem(position))).getString("detailEndpoint");

                    Intent i = new Intent(getApplicationContext(), PersonaggioActivity.class);
                    i.putExtra("personaggio", iconicAdapter.getItem(position));
                    i.putExtra("uriPersonaggio", uriPersonaggio);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Recupera la posizione del personaggio nell'array personaggi verificando l'uguaglianza del nome passato per parametro.
     * Utile per recuperare la posizione nel caso si usi il tab di ricerca che sballa gli indici.
     * @param nomePersonaggio stringa da comparare
     * @return int posizione del personaggio nell'array personaggi
     */
    private int getItemPositionByName (final String nomePersonaggio)
    {
        for (int i = 0; i < personaggi.length; i++)
        {
            if (personaggi[i].equals(nomePersonaggio))
                return i;
        }
        return -1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_credits) {
            Intent i = new Intent(getApplicationContext(), CreditsActivity.class);
            startActivity(i);
        }

        if (id==R.id.action_help){
            Intent i =new Intent(getApplicationContext(), Help.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Classe per recuperare la lista di personaggi
     */
    private class RecuperaPersonaggi extends AsyncTask<String, Void, JSONArray> {
        /**
         * Metodo che opera in background e recupera la lista dei personaggi
         * @param endpoint stringa contenente l'URL dell'endpoint
         * @return JSONArray restituito dalla query
         */
        protected JSONArray doInBackground(String... endpoint){

            //String endpoint = "http://193.204.59.21:8080/ProvaTomcat-1.0-SNAPSHOT/rest/listapersonaggi";

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
         * Aggiorna la listView con i nomi contenuti nel jsonArray e attiva la ricerca sulla lista.
         * Salva il jsonArray in jsonArrayPersonaggi.
         * @param jsonArray ottenuto dalla query nel metodo doInBackground()
         */
        protected void onPostExecute (JSONArray jsonArray){
            //personaggi=new String[]{"Torquato Tasso","Alonso","Paolo Fox","Martufello","Magalli", "Pippi Calze Lunghe", "Paolo Albino", "Ciccio Benzina", "Berlusconi", "Ruby", "Dan Bilzerian", "Castellano", "Famiglia di Lecce",
              //      "Tizio", "Caio", "Sempronio", "Maione"};

            if (jsonArray==null){
                Toast erroreConnessione = Toast.makeText(getApplicationContext(), "Connessione al server non riuscita, controlla la connessione a internet e riprova più tardi.", Toast.LENGTH_LONG);
                erroreConnessione.setGravity(Gravity.NO_GRAVITY,0,0);
                erroreConnessione.show();
                ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMain);
                mProgress.setVisibility(View.GONE);
                return;
            }

            ArrayList<String> stringArrayList = new ArrayList<String>();
            ArrayList<String> stringMestiereArrayList = new ArrayList<String>();

            for (int i=0; i<jsonArray.length();i++){
                try {
                    String nome = jsonArray.getJSONObject(i).getString("name");
                    String mestiere = jsonArray.getJSONObject(i).getString("icona");
                    stringArrayList.add(nome);
                    stringMestiereArrayList.add(mestiere);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            personaggi = stringArrayList.toArray(new String[stringArrayList.size()]);
            mestiere = stringMestiereArrayList.toArray(new String[stringMestiereArrayList.size()]);

            //copio il jsonArray per renderlo disponibile nel metodo principale onCreate()
            try {
                jsonArrayPersonaggi=new JSONArray(jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            inputSearch = (EditText) findViewById(R.id.inputSearch);

            // adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.row, R.id.textView, personaggi);
            listView = (ListView) findViewById(R.id.listview);
            //listView.setAdapter(adapter);

            iconicAdapter = new IconicAdapter();
            listView.setAdapter(iconicAdapter);

            //Log.d("RIGA", "qualcosa: " + listView.getAdapter().getCount());
            //Log.d("RIGA", "qualcosa che sta dentro" + listView.getAdapter().getItem(1).toString());

            for (int i=0; i<personaggi.length; i++){

                View riga = iconicAdapter.getView(i, null, listView);

                //View riga = listView.getAdapter().getView(i, null, listView);
                //View riga = getViewByPosition(i,listView);
                //if (riga==null){
                  //  Log.d("RIGA", "riga null" + i);
                //}

                //assert riga != null;
                //ImageView icona = (ImageView) riga.findViewById(R.id.listIcon);

                //Log.d("ICONA", "asd:" + icona.getId());

                //icona.setImageResource(R.drawable.chiesa);

            }
            //enabling search filter
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    //MainActivity.this.adapter.getFilter().filter(cs);
                    MainActivity.this.iconicAdapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });

            ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressMain);
            mProgress.setVisibility(View.GONE);
        }

        public View getViewByPosition(int pos, ListView listView) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }
    }

    class IconicAdapter extends ArrayAdapter<String> {
        IconicAdapter() {
            super(MainActivity.this, R.layout.row, R.id.textView, personaggi);
        }
        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View row=super.getView(position, convertView, parent);
            ImageView icon=(ImageView)row.findViewById(R.id.listIcon);
            switch (mestiere[position]){
                case "none": icon.setImageResource(R.drawable.none);
                    break;
                case "chiesa": icon.setImageResource(R.drawable.chiesa);
                    break;
                case "penna": icon.setImageResource(R.drawable.penna);
                    break;
                case "arte": icon.setImageResource(R.drawable.arte);
                    break;
                case "nobile": icon.setImageResource(R.drawable.nobile);
                    break;
                case "parlamento": icon.setImageResource(R.drawable.parlamento);
                    break;
            }
            //TextView size=(TextView)row.findViewById(R.id.size);
            //size.setText(String.format(getString(R.string.size_template), items[position].length()));
            return(row);
        }
    }
}
