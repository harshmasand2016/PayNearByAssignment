package com.paynearby.paynearbyassignment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.j256.ormlite.dao.Dao;
import com.paynearby.paynearbyassignment.R;
import com.paynearby.paynearbyassignment.app.PayNearbyApplication;
import com.paynearby.paynearbyassignment.dao.ModelDao;
import com.paynearby.paynearbyassignment.model.ComboItem;
import com.paynearby.paynearbyassignment.model.PantsItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import constants.PayNearbyIntentConstants;

public class DataLoadingActivity extends AppCompatActivity {

    private static final String TAG = DataLoadingActivity.class.getSimpleName();
    List<ShirtsItem> shirtsItemList;
    List<PantsItem> pantsItemList;
    List<ComboItem> comboItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataloading);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ModelDao modelDao = new ModelDao(PayNearbyApplication.getDatabase());
                getDataFromDatabase(modelDao);
            }
        },5000);


    }


    /** This method is called on start of the application to check if the user has
     * previous stored favorite combos, shirt items and pant items available.
     * If the respective values are available then it will be taken from the Database
     * else user has to add it manually
     * @param modelDao modelDao for getting the respective daos and checking the data in the tables
     * */
    @SuppressWarnings("unchecked")
    private void getDataFromDatabase(ModelDao modelDao) {
        try {
            Dao shirtsDao = modelDao.getModelDao(ShirtsItem.class);
            Dao pantsDao = modelDao.getModelDao(PantsItem.class);
            Dao comboDao = modelDao.getModelDao(ComboItem.class);

            if((shirtsDao.isTableExists() && shirtsDao.countOf() > 0) && (pantsDao.isTableExists() && pantsDao.countOf() > 0) && (comboDao.isTableExists() && comboDao.countOf() > 0)){
                //Update the List and send the same list
                shirtsItemList = modelDao.getAllModelList(ShirtsItem.class);
                pantsItemList  = modelDao.getAllModelList(PantsItem.class);
                comboItemList  = modelDao.getAllModelList(ComboItem.class);
            }else if(shirtsDao.countOf() == 0 && pantsDao.countOf() == 0 && comboDao.countOf() == 0){
                shirtsItemList = new ArrayList<>();
                pantsItemList = new ArrayList<>();
                comboItemList = new ArrayList<>();

            }else{
                if(shirtsDao.isTableExists() && shirtsDao.countOf() > 0) {
                    //Update only Shirts List
                    shirtsItemList = modelDao.getAllModelList(ShirtsItem.class);
                }
                if(pantsDao.isTableExists() && pantsDao.countOf() > 0){
                    //Update only Pant List
                    pantsItemList = modelDao.getAllModelList(PantsItem.class);
                }
                comboItemList = new ArrayList<>();
            }

        }catch (Exception e){
            Log.e(TAG,e.getMessage() + "");
        }finally {
            sendIntent();
        }
    }

    /**Navigate
     * */
    private void sendIntent(){
        Intent navigateToMainViewIntent = new Intent(this,WardrobeActivity.class);
        navigateToMainViewIntent.putParcelableArrayListExtra(PayNearbyIntentConstants.PANTS_LIST, (ArrayList<? extends Parcelable>) pantsItemList);
        navigateToMainViewIntent.putParcelableArrayListExtra(PayNearbyIntentConstants.SHIRTS_LIST, (ArrayList<? extends Parcelable>) shirtsItemList);
        navigateToMainViewIntent.putParcelableArrayListExtra(PayNearbyIntentConstants.COMBO_LIST, (ArrayList<? extends Parcelable>) comboItemList);
        startActivity(navigateToMainViewIntent);
    }


}
