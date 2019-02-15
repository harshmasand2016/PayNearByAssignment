package com.paynearby.paynearbyassignment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.paynearby.paynearbyassignment.constants.PayNearbyDatabaseConstants;
import com.paynearby.paynearbyassignment.db.Model;

@DatabaseTable(tableName = PayNearbyDatabaseConstants.TABLE_SHIRTS)

public class ShirtsItem extends Model implements Parcelable{

    public static final String COLUMN_SHIRT_ID = "shirtID";
    public static final String COLUMN_SHIRT_IMAGE_URL = "shirtImageURL";

    @DatabaseField(columnName = COLUMN_SHIRT_ID, generatedId = true,allowGeneratedIdInsert = true)
    private int shirtID;

    @DatabaseField(columnName = COLUMN_SHIRT_IMAGE_URL)
    private String shirtImageURL;

    public ShirtsItem(){
        //Empty Constructor
    }

    protected ShirtsItem(Parcel in) {
        shirtID = in.readInt();
        shirtImageURL = in.readString();
    }

    public static final Creator<ShirtsItem> CREATOR = new Creator<ShirtsItem>() {
        @Override
        public ShirtsItem createFromParcel(Parcel in) {
            return new ShirtsItem(in);
        }

        @Override
        public ShirtsItem[] newArray(int size) {
            return new ShirtsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(shirtID);
        parcel.writeString(shirtImageURL);
    }

    public int getShirtID() {
        return shirtID;
    }

    public void setShirtID(int shirtID) {
        this.shirtID = shirtID;
    }

    public String getShirtImageURL() {
        return shirtImageURL;
    }

    public void setShirtImageURL(String shirtImageURL) {
        this.shirtImageURL = shirtImageURL;
    }
}
