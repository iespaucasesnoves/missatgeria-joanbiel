package com.example.joanbiel.quepassaeh;

import android.database.sqlite.SQLiteDatabase;

public class DataSourceQuepassaeh {
    private SQLiteDatabase database;
    private HelperQuepassaeh dbAjuda;

    private String[] allColumnsQuepassaeh = {
            HelperQuepassaeh.COLUMN_CODI, HelperQuepassaeh.COLUMN_CODIUSUARI,
            HelperQuepassaeh.COLUMN_DATAHORA, HelperQuepassaeh.COLUMN_EMAIL,
            HelperQuepassaeh.COLUMN_FKCODIUSUARI, HelperQuepassaeh.COLUMN_FOTO,
            HelperQuepassaeh.COLUMN_MSG
    };
}
