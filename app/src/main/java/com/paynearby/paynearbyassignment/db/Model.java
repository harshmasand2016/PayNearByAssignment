package com.paynearby.paynearbyassignment.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.TableUtils;
import com.paynearby.paynearbyassignment.app.PayNearbyApplication;

import java.sql.SQLException;

public class Model {

    public static final String COLUMN_VERSION = "version";
    public static final String COLUMN_ACTIVE = "active";

    /**
     * the version of the Model instance available. Useful for client - server applications
     * where the client needs to maintain the version of the data that it gets from the server end.
     */
    @DatabaseField(columnName = COLUMN_VERSION, canBeNull = true)
    private int version;

    /**
     * indicates whether this Model instance is active or no. Encourages soft delete among Model instances
     */
    @DatabaseField(columnName = COLUMN_ACTIVE, canBeNull = true, dataType = DataType.BOOLEAN, defaultValue = "1")
    private boolean active = true;

    public Model() {
        super();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Drop the table with given class
     *
     * @param classType the class to drop the table of
     * @throws SQLException
     */
    public static void drop(Class classType) throws SQLException {
        TableUtils.dropTable(PayNearbyApplication.getDatabase().getConnection(), classType, false);
    }

    /**
     * Create the table with given class
     *
     * @param classType the class to create the table of
     * @throws SQLException
     */
    public static void create(Class classType) throws SQLException {
        TableUtils.createTable(PayNearbyApplication.getDatabase().getConnection(), classType);
    }
}
