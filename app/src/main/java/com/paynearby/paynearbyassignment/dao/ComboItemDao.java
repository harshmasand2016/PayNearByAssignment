package com.paynearby.paynearbyassignment.dao;

import android.database.Cursor;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.paynearby.paynearbyassignment.app.PayNearbyApplication;
import com.paynearby.paynearbyassignment.model.ComboItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;

import java.sql.SQLException;
import constants.PayNearbyDatabaseConstants;

public class ComboItemDao extends ModelDao {
    public ComboItemDao(OrmLiteSqliteOpenHelper dbHelper) {
        super(dbHelper);
    }

    /**
     * this method is used to find the list of Categories by superCategoryID
     *
     * @return return comboID
     * @throws SQLException if operation was not successful
     */
    public int retrieveComboID(int shirtID, int pantID) throws SQLException
    {
        int comboID = 0;
        String selectQuery = "SELECT " +ComboItem.COLUMN_COMBO_ID+" FROM " + PayNearbyDatabaseConstants.TABLE_COMBO + " WHERE " + ComboItem.COLUMN_SHIRT_ID + "=" + shirtID + " AND " + ComboItem.COLUMN_PANT_ID + "=" + pantID;
        Cursor cursor = PayNearbyApplication.getDatabase().getWritableDatabase().rawQuery(selectQuery, null);
        if(cursor != null && cursor.getCount() == 1){
            cursor.moveToFirst();
            comboID = cursor.getInt(cursor.getColumnIndex(ComboItem.COLUMN_COMBO_ID));
            cursor.close();
        }
        return comboID;
    }
}
