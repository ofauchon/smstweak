package com.oflabs.smstweak.smsdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.oflabs.smstweak.Sms;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: olivier
 * Date: 9/12/11
 * Time: 12:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class SmsDB {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "smstweak.db";

    private static final String TABLE_SMS = "table_sms";
    private static final String COL_ID = "_id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_DATETIME = "datetime";
    private static final int NUM_COL_TS = 1;
    private static final String COL_CALLERID = "callerid";
    private static final int NUM_COL_CALLERID = 2;
    private static final String COL_BODY = "body";
    private static final int NUM_COL_BODY = 3;

    private SQLiteDatabase bdd;

    private DatabaseHelper maBaseSQLite;

    public SmsDB(Context context) {
        //On créer la BDD et sa table
        maBaseSQLite = new DatabaseHelper(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open() {
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public long insertSms(Sms sms) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_CALLERID, sms.getCallerid());
        values.put(COL_BODY, sms.getBody());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(sms.getTimestamp());
        values.put(COL_DATETIME, dateFormat.format(d));
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_SMS, null, values);
    }

    public int updateSms(int id, Sms sms) {
        ContentValues values = new ContentValues();
        values.put(COL_CALLERID, sms.getCallerid());
        values.put(COL_BODY, sms.getBody());
        values.put(COL_DATETIME, sms.getTimestamp());
        return bdd.update(TABLE_SMS, values, COL_ID + " = " + id, null);
    }

    public int removeSmsWithId(int id) {
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_SMS, COL_ID + " = " + id, null);
    }

    public Sms getSmsWithCallerId(String callerid) {
        //Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(TABLE_SMS, new String[]{COL_ID, COL_CALLERID, COL_BODY}, COL_CALLERID + " LIKE \"" + callerid + "\"", null, null, null, null);
        return cursorToSMS(c);
    }

    public Cursor getLastSms(int count) {
        Cursor c = bdd.query(TABLE_SMS, new String[]{COL_ID, COL_CALLERID, COL_BODY, COL_DATETIME}, null, null, null, null, COL_DATETIME + " DESC", Integer.toString(count));
        return c;
    }

    public void deleteAllSms() {
        bdd.delete(TABLE_SMS, null, null);

    }


    //Cette méthode permet de convertir un cursor en un livre
    private Sms cursorToSMS(Cursor c) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        Sms sms = new Sms();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        sms.setId(c.getInt(NUM_COL_ID));
        sms.setCallerid(c.getString(NUM_COL_CALLERID));
        sms.setBody(c.getString(NUM_COL_BODY));
        //On ferme le cursor
        c.close();

        //On retourne le livre
        return sms;
    }
}
