package com.paynearby.paynearbyassignment.dao;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.paynearby.paynearbyassignment.model.PantsItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;

import java.sql.SQLException;
import java.util.List;

public class ShirtItemDao extends ModelDao {

    public ShirtItemDao(OrmLiteSqliteOpenHelper dbHelper) {
        super(dbHelper);
    }

    /**
     * this method is used to find the list of Categories by superCategoryID
     *
     * @return {@linkplain List} of {@linkplain ShirtsItem}
     * @throws SQLException if operation was not successful
     */
    public List<ShirtsItem> getShirtsListInDescOrder() throws SQLException {
        Dao<ShirtsItem, Integer> dao = getDbHelper().getDao(ShirtsItem.class);
        QueryBuilder<ShirtsItem, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(ShirtsItem.COLUMN_SHIRT_ID,false);
        return dao.query(queryBuilder.prepare());
    }

}
