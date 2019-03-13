package com.example.joanbiel.quepassaeh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataSourceQuepassaeh {
    private SQLiteDatabase database;
    private HelperQuepassaeh dbAjuda;

    private String[] allColumnsQuepassaeh = {
            HelperQuepassaeh.COLUMN_CODI, HelperQuepassaeh.COLUMN_CODIUSUARI,
            HelperQuepassaeh.COLUMN_DATAHORA, HelperQuepassaeh.COLUMN_EMAIL,
            HelperQuepassaeh.COLUMN_FKCODIUSUARI, HelperQuepassaeh.COLUMN_FOTO,
            HelperQuepassaeh.COLUMN_MSG, HelperQuepassaeh.COLUMN_NOM,
            HelperQuepassaeh.COLUMN_PENDENT
    };

    public DataSourceQuepassaeh(Context context){dbAjuda = new HelperQuepassaeh(context);}

    public void open() throws SQLException {
        database = dbAjuda.getWritableDatabase();
    }

    public void close() {
        dbAjuda.close();
    }

    public Missatge createMissatge(Missatge missatge){
        ContentValues values = new ContentValues();
        values.put(HelperQuepassaeh.COLUMN_CODI,missatge.getCodi());
        values.put(HelperQuepassaeh.COLUMN_MSG,missatge.getMsg());
        values.put(HelperQuepassaeh.COLUMN_DATAHORA,missatge.getDataHora());
        values.put(HelperQuepassaeh.COLUMN_FKCODIUSUARI,String.valueOf(missatge.getCodiUsuari()));
        //values.put(HelperQuepassaeh.COLUMN_PENDENT,);
        long insertID = database.insert(HelperQuepassaeh.TABLE_MISSATGE,null,values);
        missatge.setCodi(insertID);
        return missatge;
    }

    public boolean updateMissatge(Missatge missatge){
        ContentValues values = new ContentValues();
        long codi = missatge.getCodi();
        values.put(HelperQuepassaeh.COLUMN_CODI,missatge.getCodi());
        values.put(HelperQuepassaeh.COLUMN_MSG,missatge.getMsg());
        values.put(HelperQuepassaeh.COLUMN_DATAHORA,missatge.getDataHora());
        values.put(HelperQuepassaeh.COLUMN_FKCODIUSUARI,String.valueOf(missatge.getCodiUsuari()));
        return database.update(HelperQuepassaeh.TABLE_MISSATGE, values,
                HelperQuepassaeh.COLUMN_CODI + "=" + codi, null) > 0;
    }

    public void deleteMissatge(Missatge missatge){
        long codi = missatge.getCodi();
        database.delete(HelperQuepassaeh.TABLE_MISSATGE,
                HelperQuepassaeh.COLUMN_CODI + "=" + codi, null);
    }

    public Missatge getMissatge(long codi){
        Missatge missatge;
        Cursor cursor = database.query(HelperQuepassaeh.TABLE_MISSATGE,
                allColumnsQuepassaeh, HelperQuepassaeh.COLUMN_CODI + "=" + codi,null,
                null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            missatge = cursorToMissatge(cursor);
        } else {
            missatge = new Missatge();
        }
        cursor.close();
        return missatge;
    }

    public List<Missatge> getAllMissatge(){
        List<Missatge> missatges =  new ArrayList<Missatge>();
        Cursor cursor = database.query(
                HelperQuepassaeh.TABLE_MISSATGE, allColumnsQuepassaeh, null,
                null,null, null,
                HelperQuepassaeh.COLUMN_DATAHORA + " DESC"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Missatge missatge = cursorToMissatge(cursor);
            missatges.add(missatge);
            cursor.moveToNext();
        }
        cursor.close();
        return missatges;
    }

    private Missatge cursorToMissatge(Cursor cursor){
        Missatge m = new Missatge();
        m.setCodi(cursor.getLong(0));
        m.setMsg(cursor.getString(1));
        m.setDataHora(cursor.getString(2));
        m.setCodiUsuari(cursor.getLong(3));
        return m;
    }

    public Usuari createUsuari(Usuari usuari){
        ContentValues values = new ContentValues();
        values.put(HelperQuepassaeh.COLUMN_CODIUSUARI, usuari.getCodiUsuari());
        values.put(HelperQuepassaeh.COLUMN_NOM, usuari.getNom());
        values.put(HelperQuepassaeh.COLUMN_EMAIL, usuari.getEmail());
        values.put(HelperQuepassaeh.COLUMN_FOTO, usuari.getFoto());
        long insertCodi = database.insert(HelperQuepassaeh.TABLE_USUARI, null, values);
        usuari.setCodiUsuari(insertCodi);
        return usuari;
    }

    public boolean updateUsuari(Usuari usuari){
        ContentValues values = new ContentValues();
        long codi = usuari.getCodiUsuari();
        values.put(HelperQuepassaeh.COLUMN_CODIUSUARI, usuari.getCodiUsuari());
        values.put(HelperQuepassaeh.COLUMN_NOM, usuari.getNom());
        values.put(HelperQuepassaeh.COLUMN_EMAIL, usuari.getEmail());
        values.put(HelperQuepassaeh.COLUMN_FOTO, usuari.getFoto());
        return database.update(HelperQuepassaeh.TABLE_USUARI, values,
                HelperQuepassaeh.COLUMN_CODIUSUARI + "=" + codi, null) > 0;
    }

    public void deleteUsuari(Usuari usuari){
        long codi = usuari.getCodiUsuari();
        database.delete(HelperQuepassaeh.TABLE_USUARI,
                HelperQuepassaeh.COLUMN_CODIUSUARI + "=" + codi, null);
    }

    public Usuari getUsuari(long codi){
        Usuari usuari;
        Cursor cursor = database.query(HelperQuepassaeh.TABLE_USUARI,
                allColumnsQuepassaeh, HelperQuepassaeh.COLUMN_CODIUSUARI + "="+ codi,
                null, null,null,null);
        if (cursor.getCount()> 0){
            cursor.moveToFirst();
            usuari = cursorToUsuari(cursor);
        } else {
            usuari = new Usuari();
        }
        cursor.close();
        return usuari;
    }

    public List<Usuari>getAllUsuari(){
        List<Usuari> usuaris = new ArrayList<Usuari>();
        Cursor cursor = database.query(
                HelperQuepassaeh.TABLE_USUARI, allColumnsQuepassaeh, null,
                null, null,null,
                HelperQuepassaeh.COLUMN_DATAHORA + " DESC"
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Usuari usuari = cursorToUsuari(cursor);
            usuaris.add(usuari);
            cursor.moveToNext();
        }
        cursor.close();
        return usuaris;
    }

    private Usuari cursorToUsuari(Cursor cursor){
        Usuari u = new Usuari();
        u.setCodiUsuari(cursor.getLong(0));
        u.setNom(cursor.getString(1));
        u.setEmail(cursor.getString(2));
        return u;
    }
}
