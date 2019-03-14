package com.example.joanbiel.quepassaeh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editText;
    Button btnEnviar;
    TextView vista;
    BufferedReader in;
    ListView list;
    Preferencies preferencies;
    DataSourceQuepassaeh missatge;
    DataSourceQuepassaeh usuari;
    ArrayList<Missatge> missatges = new ArrayList<>();
    Usuari u;
    String[] from = {"ID", "NOM", "EMAIL"};
    //int[] to = {R.id.textId,R.id.textName,R.id.textEmail};
    MissatgeAdapter ma;
    final int CODI_AFEGIR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editMissatge);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);
        list = findViewById(R.id.lista);
        preferencies = new Preferencies(this);
        ma = new MissatgeAdapter(this, R.layout.missatge, missatges);
        Intent i = new Intent(this,Login.class);
        startActivityForResult(i,CODI_AFEGIR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        new TascaDescarrega(this).execute("http://iesmantpc.000webhostapp.com/public/provamissatge/");
    }

    @Override
    public void onClick(View v){
        HashMap<String,String> dades = new HashMap<>();
        String text = editText.getText().toString();
        Log.d("RUN", "Missatge" + text);
        dades.put("msg", text);
        dades.put("codiusuari", String.valueOf(preferencies.getCodiusuari()));
        if (v == btnEnviar) {
            CridadaPost(dades);
            new TascaDescarrega(this).execute("http://iesmantpc.000webhostapp.com/public/provamissatge/");
        }
    }

    public static String CridadaPost(HashMap<String, String> parametres){
        String resultat="";
        String adrecaURL = "https://iesmantpc.000webhostapp.com/public/provamissatge/";
        try {
            URL url = new URL(adrecaURL);
            HttpsURLConnection httpConn = (HttpsURLConnection) url.openConnection();
            httpConn.setReadTimeout(15000);
            httpConn.setConnectTimeout(25000);
            httpConn.setRequestMethod("POST");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Log.i("ResConnectUtils",montaParametres(parametres));
            writer.write(montaParametres(parametres));
            writer.flush();
            writer.close();
            os.close();
            int resposta = httpConn.getResponseCode();
            if (resposta == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    resultat+=line;
                }
                Log.i("ResConnectUtils", resultat);
            }else {
                resultat="";
                Log.i("ResConnectUtils","Errors:"+resposta);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }return resultat;
    }

    private static String montaParametres(HashMap<String, String> params) throws UnsupportedEncodingException {
        // A partir d'un hashmap clau-valor cream
        //               clau1=valor1&clau2=valor2&...
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) { first = false;} else {result.append("&");}
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }return result.toString();
    }

    class TascaDescarrega extends AsyncTask<String, Integer, ArrayList<Missatge>> {
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
        protected ArrayList<Missatge> doInBackground(String... params) {
            StringBuilder text = new StringBuilder();
            URL url = null;
            String resultat = "";
            //ArrayList<HashMap<String, String>> llista = new ArrayList<HashMap<String, String>>();
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
                            JSONObject jObject = jArray.getJSONObject(i);
                            missatges.add(new Missatge(
                                    jObject.getLong("codi"),
                                    jObject.getString("msg"),
                                    jObject.getString("datahora"),
                                    jObject.getLong("codiusuari"),
                                    jObject.getString("nom")
                                    ));
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
            return missatges;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(ArrayList<Missatge> llista) {
            ma = new MissatgeAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,llista);
            list.setAdapter(ma);
            ma.notifyDataSetChanged();
        }
    }
}

class MissatgeAdapter extends ArrayAdapter<Missatge>{
    private Context context;
    private List<Missatge> missatges;

    public MissatgeAdapter(Context context, int resource, ArrayList<Missatge> missatges){
        super(context, resource, missatges);
        this.context = context;
        this.missatges = missatges;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Missatge missatge = missatges.get(position);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.missatge, null);

        TextView nom = view.findViewById(R.id.textNom);
        TextView msg = view.findViewById(R.id.textMissatge);
        TextView hora = view.findViewById(R.id.textData);

        nom.setText(missatge.getNom());
        msg.setText(missatge.getMsg());
        hora.setText(missatge.getDataHora());
        return view;
    }
}