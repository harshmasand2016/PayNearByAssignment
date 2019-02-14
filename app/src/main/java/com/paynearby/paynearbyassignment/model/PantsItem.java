package com.paynearby.paynearbyassignment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.paynearby.paynearbyassignment.db.Model;

import constants.PayNearbyDatabaseConstants;

@DatabaseTable(tableName = PayNearbyDatabaseConstants.TABLE_PANTS)
public class PantsItem extends Model implements Parcelable{

    public static final String COLUMN_PANT_ID = "pantID";
    public static final String COLUMN_PANT_IMAGE_URL = "pantImageURL";

    @DatabaseField(columnName = COLUMN_PANT_ID, generatedId = true,allowGeneratedIdInsert = true)
    private int pantID;

    @DatabaseField(columnName = COLUMN_PANT_IMAGE_URL)
    private String pantImageURL;

    public PantsItem(){
        //Empty Constructor
    }

    protected PantsItem(Parcel in) {
        pantID = in.readInt();
        pantImageURL = in.readString();
    }

    public static final Parcelable.Creator<PantsItem> CREATOR = new Parcelable.Creator<PantsItem>() {
        @Override
        public PantsItem createFromParcel(Parcel in) {
            return new PantsItem(in);
        }

        @Override
        public PantsItem[] newArray(int size) {
            return new PantsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(pantID);
        parcel.writeString(pantImageURL);
    }

    public int getPantID() {
        return pantID;
    }

    public void setPantID(int pantID) {
        this.pantID = pantID;
    }

    public String getPantImageURL() {
        return pantImageURL;
    }

    public void setPantImageURL(String pantImageURL) {
        this.pantImageURL = pantImageURL;
    }


}
