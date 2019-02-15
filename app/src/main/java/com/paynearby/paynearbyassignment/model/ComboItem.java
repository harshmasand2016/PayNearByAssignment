package com.paynearby.paynearbyassignment.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.paynearby.paynearbyassignment.constants.PayNearbyDatabaseConstants;
import com.paynearby.paynearbyassignment.db.Model;

@DatabaseTable(tableName = PayNearbyDatabaseConstants.TABLE_COMBO)
public class ComboItem extends Model implements Parcelable{
    public static final String COLUMN_PANT_ID = "pantID";
    public static final String COLUMN_SHIRT_ID = "shirtID";
    public static final String COLUMN_COMBO_ID = "comboID";

    @DatabaseField(columnName = COLUMN_COMBO_ID,generatedId = true, allowGeneratedIdInsert = true)
    private
    int comboID;

    @DatabaseField(columnName = COLUMN_PANT_ID)
    private
    int pantID;

    @DatabaseField(columnName = COLUMN_SHIRT_ID)
    private
    int shirtID;

    public ComboItem(){
        //Empty Constructor
    }

    protected ComboItem(Parcel in) {
        comboID = in.readInt();
        pantID = in.readInt();
        shirtID = in.readInt();
    }

    public static final Creator<ComboItem> CREATOR = new Creator<ComboItem>() {
        @Override
        public ComboItem createFromParcel(Parcel in) {
            return new ComboItem(in);
        }

        @Override
        public ComboItem[] newArray(int size) {
            return new ComboItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(comboID);
        parcel.writeInt(pantID);
        parcel.writeInt(shirtID);
    }

    public int getComboID() {
        return comboID;
    }

    public void setComboID(int comboID) {
        this.comboID = comboID;
    }

    public int getPantID() {
        return pantID;
    }

    public void setPantID(int pantID) {
        this.pantID = pantID;
    }

    public int getShirtID() {
        return shirtID;
    }

    public void setShirtID(int shirtID) {
        this.shirtID = shirtID;
    }

}
