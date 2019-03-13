package com.example.joanbiel.quepassaeh;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editText;
    Button btn_descarrega;
    TextView vista;
    BufferedReader in;
    ListView list;
    DataSourceQuepassaeh missatge;
    DataSourceQuepassaeh usuari;
    Usuari u;
    String[] from = {"ID", "NOM", "EMAIL"};
    int[] to = {R.id.textId,R.id.textName,R.id.textEmail};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        btn_descarrega = findViewById(R.id.btn_descarrega);
        btn_descarrega.setOnClickListener(this);
        list = findViewById(R.id.lista);
        Intent i = new Intent(this,Login.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v){
        String text = editText.getText().toString();
        Log.d("RUN", "Descarrega" + text);

        if (v == btn_descarrega) {
            new TascaDescarrega(this).execute(text);
        }
    }


    class TascaDescarrega extends AsyncTask<String, Integer, ArrayList<HashMap<String,String>>> {
        int responseCode = -1;
        Context context;

        public TascaDescarrega(Context c){
            this.context = c;
        }

        protected void onPreExecute(String texte) {
            super.onPreExecute();
            vista.setText(texte);
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... params) {
            StringBuilder text = new StringBuilder();
            URL url = null;
            String resultat = "";
            ArrayList<HashMap<String, String>> llista = new ArrayList<HashMap<String, String>>();
            try {
                // Agafam la URL que s'ha passat com argument
                url = new URL(params[0]);
                // Feim la connexió a la URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                // Codi de la resposta
                responseCode = httpURLConnection.getResponseCode();
                Log.d("RUN", "Descarrega " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Recollim texte
                    in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String liniatxt = "";
                    while ((liniatxt = in.readLine()) != null) {
                        resultat += liniatxt;
                    }
                    try {
                        JSONObject json = new JSONObject(resultat);
                        JSONArray jArray = json.getJSONArray("dades");
                        // Llista de descarregues

                        // Guarda a la llista
                        for (int i = 0; i < jArray.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject jObject = jArray.getJSONObject(i);
                            map.put("ID",jObject.getString("id"));
                            map.put("NOM",jObject.getString("nom"));
                            map.put("EMAIL",jObject.getString("email"));
                            // map.put("ROLE",jObject.getString("fk_role"));
                            llista.add(map);
                             u = new Usuari();
                             u.setCodiUsuari(jObject.getLong("id"));
                             u.setNom((jObject.getString("nom")));
                             u.setEmail(jObject.getString("email"));
                             usuari.createUsuari(u);
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return llista;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(ArrayList<HashMap<String, String>> llista) {
            SimpleAdapter simpleAdapter = new SimpleAdapter(context, llista, R.layout.item, from, to);
            list.setAdapter(simpleAdapter);
            list.setTextFilterEnabled(true);
        }
    }
}