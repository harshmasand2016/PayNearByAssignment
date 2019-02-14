package com.paynearby.paynearbyassignment.dao;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.paynearby.paynearbyassignment.model.PantsItem;

import java.sql.SQLException;
import java.util.List;

public class PantIemDao extends ModelDao{

    public PantIemDao(OrmLiteSqliteOpenHelper dbHelper) {
        super(dbHelper);
    }

    /**
     * this method is used to find the list of Categories by superCategoryID
     *
     * @return {@linkplain List} of {@linkplain PantsItem}
     * @throws SQLException if operation was not successful
     */
    public List<PantsItem> getPantsListInDescOrder() throws SQLException {
        Dao<PantsItem, Integer> dao = getDbHelper().getDao(PantsItem.class);
        QueryBuilder<PantsItem, Integer> queryBuilder = dao.queryBuilder();
        queryBuilder.orderBy(PantsItem.COLUMN_PANT_ID,false);
        return dao.query(queryBuilder.prepare());
    }
}
