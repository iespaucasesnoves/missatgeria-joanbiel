package com.example.joanbiel.quepassaeh;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Preferencies preferencies;
    EditText usuari;
    EditText contrasenya;
    Button button;
    String url = "https://iesmantpc.000webhostapp.com/public/login/";
    HashMap<String,String> dades;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        preferencies = new Preferencies(this);
        dades = new HashMap<>();
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == button){
            usuari = findViewById(R.id.usuari);
            contrasenya = findViewById(R.id.contrasenya);
            dades.put("nom",this.usuari.getText().toString());
            dades.put("password",this.contrasenya.getText().toString());
            String info = CridadaPost(url,dades);
            try {
                JSONObject resp = new JSONObject(info);
                JSONObject dades  = resp.getJSONObject("dades");
                preferencies = new Preferencies(this);
                preferencies.setUser(usuari.getText().toString());
                preferencies.setPassword(contrasenya.getText().toString());
                preferencies.setCodiusuari(Integer.parseInt(dades.getString("codiusuari")));
                preferencies.setToken(dades.getString("token"));
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static String CridadaPost(String adrecaURL,HashMap<String, String> parametres) {
        String resultat="";
        try {
            URL url = new URL(adrecaURL);
            Log.i("ResConnectUtils", "Connectant"+adrecaURL);
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

}
