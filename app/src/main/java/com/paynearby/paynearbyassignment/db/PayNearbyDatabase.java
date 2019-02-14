package com.paynearby.paynearbyassignment.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.paynearby.paynearbyassignment.model.ComboItem;
import com.paynearby.paynearbyassignment.model.PantsItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import constants.PayNearbyDatabaseConstants;

public class PayNearbyDatabase extends OrmLiteSqliteOpenHelper{

    private static final String TAG = PayNearbyDatabase.class.getSimpleName();
    public static final String DATABASE_NAME = "PayNearbyDatabase.sqlite";
    private ConnectionSource connection;
    private static List<Class> tableList;


    public PayNearbyDatabase(Context context) {
        super(context, DATABASE_NAME, null, PayNearbyDatabaseConstants.DB_VERSION);
        connection = getConnectionSource();
        tableList = new ArrayList<>();
        tableList.add(ShirtsItem.class);
        tableList.add(PantsItem.class);
        tableList.add(ComboItem.class);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            for (Class classToCreate : tableList) {
                TableUtils.createTable(connection, classToCreate);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    Log.e(TAG,"onUpgrade");

    }

    public ConnectionSource getConnection() {
        return connection;
    }

    public void setConnection(ConnectionSource connection) {
        this.connection = connection;
    }

}
