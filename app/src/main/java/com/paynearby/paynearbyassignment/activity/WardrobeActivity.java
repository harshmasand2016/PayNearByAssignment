package com.paynearby.paynearbyassignment.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.j256.ormlite.dao.Dao;
import com.paynearby.paynearbyassignment.R;
import com.paynearby.paynearbyassignment.adapter.PantsImageLoadingAdapter;
import com.paynearby.paynearbyassignment.adapter.ShirtsLoadingAdapter;
import com.paynearby.paynearbyassignment.app.PayNearbyApplication;
import com.paynearby.paynearbyassignment.dao.ComboItemDao;
import com.paynearby.paynearbyassignment.dao.ModelDao;
import com.paynearby.paynearbyassignment.dao.PantIemDao;
import com.paynearby.paynearbyassignment.dao.ShirtItemDao;
import com.paynearby.paynearbyassignment.model.ComboItem;
import com.paynearby.paynearbyassignment.model.PantsItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;
import com.paynearby.paynearbyassignment.model.ShuffleObject;
import com.paynearby.paynearbyassignment.utils.ImageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import constants.PayNearbyIntentConstants;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class WardrobeActivity extends AppCompatActivity {

    private static final String TAG = WardrobeActivity.class.getSimpleName();
    ViewPager shirtsPager,pantsPager;
    String type;
    boolean cameraClicked, galleryClicked;
    FloatingActionButton shirtsFab,pantsFab;
    AppCompatImageView favoriteImageIcon, shuffleImageIcon;
    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CAMERA = 2;
    ImageManager imageManager;
    List<ShirtsItem> shirtsItemList;
    List<PantsItem> pantsItemList;
    List<ComboItem> comboItemList;
    PantsImageLoadingAdapter pantsImageLoadingAdapter;
    ShirtsLoadingAdapter shirtsLoadingAdapter;
    List<ShuffleObject> newShuffledList = new ArrayList<>();
    ProgressDialog progressDialog ;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getData();
        init();
        listeners();
    }

    private void getData() {
        shirtsItemList = getIntent().getParcelableArrayListExtra(PayNearbyIntentConstants.SHIRTS_LIST);
        pantsItemList = getIntent().getParcelableArrayListExtra(PayNearbyIntentConstants.PANTS_LIST);
        comboItemList = getIntent().getParcelableArrayListExtra(PayNearbyIntentConstants.COMBO_LIST);
    }

    private void listeners() {
        shirtsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type  = getString(R.string.shirts_text);
                showActionDialog();
            }
        });

        pantsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type  = getString(R.string.pants_text);
                showActionDialog();
            }
        });


        shuffleImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleList();
            }
        });

        favoriteImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavIconAndInsertIntoDB();
            }
        });

        shirtsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                applyChangesToFavButton(isShirtCombo(position), isPantCombo(pantsPager.getCurrentItem()));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        pantsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int position) {
                applyChangesToFavButton(isShirtCombo(shirtsPager.getCurrentItem()), isPantCombo(position));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        showOrHideShuffleAndFavoriteButtons();
        applyChangesToFavButton(isShirtCombo(shirtsPager.getCurrentItem()), isPantCombo(pantsPager.getCurrentItem()));
        showMaterialCaseView();
    }

    private void showMaterialCaseView() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, PayNearbyApplication.SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(shirtsFab,
                getString(R.string.show_case_text_shirts), getString(R.string.ok));

        sequence.addSequenceItem(pantsFab,
                getString(R.string.show_case_text_pants), getString(R.string.ok));
        sequence.start();
    }

    /**
     * Show or Hide Visibility of Shuffle and Favorite Icons if shirt and pant item is 0
     * */
    private void showOrHideShuffleAndFavoriteButtons() {
        if(pantsItemList.size() > 0 && shirtsItemList.size() > 0){
            favoriteImageIcon.setVisibility(View.VISIBLE);
            shuffleImageIcon.setVisibility(View.VISIBLE);
        }else{
            favoriteImageIcon.setVisibility(View.GONE);
            shuffleImageIcon.setVisibility(View.GONE);
        }
    }

    /**Show the favorite icon to marked or unmarked
     * @param shirtIsCombo true or false
     * @param pantIsCombo true or false
     * */
    private void applyChangesToFavButton(boolean shirtIsCombo, boolean pantIsCombo) {
        if(shirtIsCombo && pantIsCombo){
            setFavSelectedIcon();
        }else{
            setFavUnselectedIcon();
        }
    }

    private void changeFavIconAndInsertIntoDB() {
        changeFavIconAndDBOperations();
    }

    private void init() {
        shirtsPager = findViewById(R.id.shirt_view_pager);
        pantsPager = findViewById(R.id.pants_view_pager);
        shirtsFab = findViewById(R.id.add_shirts_fab);
        pantsFab = findViewById(R.id.add_pants_fab);
        favoriteImageIcon = findViewById(R.id.fav_icon);
        shuffleImageIcon = findViewById(R.id.shuffle_icon);
        imageManager = new ImageManager(this);


        if(shirtsItemList == null){
            shirtsItemList = new ArrayList<>();
        }
        if(pantsItemList == null){
            pantsItemList = new ArrayList<>();
        }
        if(comboItemList == null){
            comboItemList = new ArrayList<>();
        }
        pantsImageLoadingAdapter = new PantsImageLoadingAdapter(getApplicationContext(),pantsItemList);
        shirtsLoadingAdapter = new ShirtsLoadingAdapter(getApplicationContext(),shirtsItemList);
        shirtsPager.setOffscreenPageLimit(shirtsItemList.size());
        pantsPager.setOffscreenPageLimit(shirtsItemList.size());
        shirtsPager.setAdapter(shirtsLoadingAdapter);
        pantsPager.setAdapter(pantsImageLoadingAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
    }

    /**Show the action dialog for Select from gallery
     * and use camera for uploading the image.
     * It also contains required permission check
     * */
    public void showActionDialog(){
        final Dialog actionsDialog = new Dialog(WardrobeActivity.this);
        actionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionsDialog.setContentView(R.layout.dialog_upload_image);
        Button uploadFromGallery = actionsDialog.findViewById(R.id.btn_edit);
        Button takePicture = actionsDialog.findViewById(R.id.btn_delete);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraClicked = true;
                galleryClicked = false;
                if (checkPermission()) {
                    captureImage();
                }
                actionsDialog.dismiss();
            }
        });

        uploadFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraClicked = false;
                galleryClicked = true;
                if (checkPermission()) {
                    imageManager.selectImageFromGallery();
                }
                actionsDialog.dismiss();
            }
        });
        actionsDialog.setCancelable(true);
        actionsDialog.show();
    }

    private void captureImage() {
        try {
            imageManager.captureImage(type);
        } catch (Exception e) {
            showToast(getString(R.string.error_loading_the_image));
            Log.e("exception", e.getMessage()+"");
        }
    }

    /**Checks the Read/Write External Storage and for Accessing System's Camera
     * and request for permissions
     * */
    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))  {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(getString(R.string.alert));
                    alertBuilder.setMessage(getString(R.string.external_storage_camera_permission));
                    alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(WardrobeActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CAMERA);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CAMERA);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ImageManager.REQUEST_IMAGE_CAPTURE || requestCode == ImageManager.SELECT_PICTURE) && resultCode == RESULT_OK) {
            try {
                //We got the Image here do further processing
                Uri imageUri = imageManager.onActivityResult(requestCode, resultCode, data, type);
                if(type.equalsIgnoreCase(getString(R.string.shirts_text))){
                    ShirtsItem shirtsItem = new ShirtsItem();
                    shirtsItem.setShirtImageURL(String.valueOf(imageUri));
                    shirtsItemList.add(shirtsItem);
                }else{
                    PantsItem  pantsItem = new PantsItem();
                    pantsItem.setPantImageURL(String.valueOf(imageUri));
                    pantsItemList.add(pantsItem);
                }

                //Insert
                insertImagesIntoRespectiveTablesAndGetDataAsWell(type);
            } catch (Exception e) {
                showToast(getString(R.string.sorry_could_not_perform_operations));
                Log.e("exception", e.getMessage()+"");
            }
        }
    }


    /**Inserts the image coming into the respective tables based on @param type
     * @param type It contains type of item like shirts or pants
     * It contains method called getDataFromRespectiveTablesAndNotifyTheAdapters which takes
     * @param  type for fetching the latest data from db and updating the adapters respectively
     * */
    private void insertImagesIntoRespectiveTablesAndGetDataAsWell(String type) {
        try {
            ModelDao modelDao = new ModelDao(PayNearbyApplication.getDatabase());
            if (type.equalsIgnoreCase(getString(R.string.shirts_text))) {
                modelDao.clear(ShirtsItem.class);
                modelDao.bulkPersist(shirtsItemList);
            } else {
                modelDao.clear(PantsItem.class);
                modelDao.bulkPersist(pantsItemList);
            }
            getDataFromRespectiveTablesAndNotifyTheAdapters(type);
        }catch (Exception e){
            showToast(getString(R.string.sorry_could_not_perform_operations));
            Log.e(TAG,e.getMessage() + "");
        }
    }

    /** It contains method called getDataFromRespectiveTablesAndNotifyTheAdapters which takes
     * @param  type for fetching the latest data from db in descending order to show the latest image
     * and updating the adapters respectively
     **/
    private void getDataFromRespectiveTablesAndNotifyTheAdapters(String type) {
        try {
            if(type.equalsIgnoreCase(getString(R.string.shirts_text))) {
                shirtsItemList.clear();
                ShirtItemDao shirtItemDao = new ShirtItemDao(PayNearbyApplication.getDatabase());
                shirtsItemList = shirtItemDao.getShirtsListInDescOrder();
                //Notify the changes into the adapter
                shirtsLoadingAdapter.updateShirtsList(shirtsItemList);
            }else{
                pantsItemList.clear();
                PantIemDao pantIemDao = new PantIemDao(PayNearbyApplication.getDatabase());
                pantsItemList = pantIemDao.getPantsListInDescOrder();
                //Notify the changes into the lists
                pantsImageLoadingAdapter.updatePantsList(pantsItemList);
            }
        }catch (Exception e){
            showToast(getString(R.string.sorry_could_not_perform_operations));
            Log.e(TAG,e.getMessage() + "-");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CAMERA:
                if (cameraClicked) {
                    captureImage();
                } else if (galleryClicked) {
                    imageManager.selectImageFromGallery();
                }
                break;
        }
    }
    /** Get the current shirts and pant object
     *  Check if the combo/favorite table has any rows or not
     *  And perform delete or insert operations based on the taping the Favorite Button
     * */
    @SuppressWarnings("unchecked")
    private void changeFavIconAndDBOperations() {
        createProgressDialog();
        int shirtPosition = shirtsPager.getCurrentItem();
        int pantPosition = pantsPager.getCurrentItem();
        try {
            if (shirtsItemList.size() >=1 && pantsItemList.size() >= 1) {
                ComboItemDao comboDao = new ComboItemDao(PayNearbyApplication.getDatabase());
                ModelDao modelDao = new ModelDao(PayNearbyApplication.getDatabase());
                if(modelDao.getModelDao(ComboItem.class).countOf() > 0){
                    //Check if pantID ,shirtID exists in DB or not ?

                    int comboID = comboDao.retrieveComboID(shirtsItemList.get(shirtPosition).getShirtID(),pantsItemList.get(pantPosition).getPantID());
                    if(comboID != 0){
                        //Delete the comboID
                        deleteCombo(comboID);
                        comboItemList = modelDao.getAllModelList(ComboItem.class);
                    }else{
                        insertIntoComboTable(modelDao,shirtPosition,pantPosition);
                    }
                }else{
                 insertIntoComboTable(modelDao,shirtPosition,pantPosition);
                }

            } else {
                showToast(getString(R.string.select_both_shirt_and_pant_item_text));
            }
            dismissProgressDialog();
        }catch (Exception e){
            showToast(getString(R.string.sorry_could_not_perform_operations));
            Log.e(TAG, e.getMessage() + "");
        }
    }

    /**This method is used to delete a favorite combo if user taps the already favorite button
     * @param comboID is need for delete operation
     * */
    private void deleteCombo(int comboID) {
        try {
            Dao<ComboItem, Integer> dao = PayNearbyApplication.getDatabase().getDao(ComboItem.class);
            dao.deleteById(comboID);
            setFavUnselectedIcon();
        }catch (Exception e){
            showToast(getString(R.string.sorry_could_not_perform_operations));
            Log.e(TAG,e.getMessage() + "");
        }
    }

    /**This method is used to delete a favorite combo if user taps the already favorite button
     * @param modelDao
     * @param shirtPosition shirt item's viewpager's current item position
     * @param pantPosition  pant item's viewpager's current item position
     * */
    private void insertIntoComboTable(ModelDao modelDao, int shirtPosition, int pantPosition) {
        try{
            if(shirtsPager != null && pantsPager != null) {
                ShirtsItem newShirtItem = shirtsItemList.get(shirtPosition);
                PantsItem pantsItem = pantsItemList.get(pantPosition);
                ComboItem comboItem = new ComboItem();
                comboItem.setShirtID(newShirtItem.getShirtID());
                comboItem.setPantID(pantsItem.getPantID());
                comboItemList.add(comboItem);
                modelDao.clear(ComboItem.class);
                modelDao.bulkPersist(comboItemList);
                setFavSelectedIcon();
            }
        }catch (Exception e){
            showToast(getString(R.string.sorry_combo_not_saved));
            Log.e(TAG,e.getMessage() + "");
        }
    }

    private void setFavSelectedIcon(){
        favoriteImageIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
    }

    private void setFavUnselectedIcon(){
        favoriteImageIcon.setImageResource(R.drawable.ic_favorite_border);

    }

    /**For showing the toast
     * @param textYouWantToShow the
     * */
    private void showToast(String textYouWantToShow){
        Toast.makeText(this,textYouWantToShow,Toast.LENGTH_SHORT).show();

    }

    /**Checks if the current shirt viewpager has favorite or not
     * @param position viewpager get current position
     * and return the
     * */
    public boolean isShirtCombo(int position){
        boolean isShirtCombo = false;
        for(int i = 0 ; i < comboItemList.size() ; i ++){
            if(comboItemList.get(i).getShirtID() == shirtsItemList.get(position).getShirtID()){
                isShirtCombo = true;
                break;
            }
        }
        return isShirtCombo;
    }

    /**Checks if the current pant viewpager has favorite or not
     * @param position viewpager get current position
     * */
    public boolean isPantCombo(int position){
        boolean isPantCombo = false;
        for(int i = 0 ; i < comboItemList.size() ; i ++){
            if(comboItemList.get(i).getPantID() == pantsItemList.get(position).getPantID()){
                isPantCombo = true;
                break;
            }
        }
        return isPantCombo;
    }

    /**Algo for shuffling
     * This method is used to get the new shuffling list and showing the image with favorite collection in random order
     * After creating a new list with unique combos of shirts and pants, it is shuffled via Collections.shuffle.
     * If the counter matches with the shuffledlist.size() , reset the counter value to 0.
     * If user taps on the shuffle Button then a shuffle list will be created with unique combos of pant and shirt item
     * in a random manner and shuffled list will maintain its order till the app is not killed
     * */
    public void shuffleList() {
        if (shirtsItemList.size() > 1 && pantsItemList.size() > 1) {
            if (newShuffledList.size() == 0) {
                LinkedHashSet<ShuffleObject> shuffleObjectLinkedHashSet = new LinkedHashSet<>();
                for (int i = 0; i < shirtsItemList.size(); i++) {
                    for (int j = 0; j < pantsItemList.size(); j++) {
                        ShuffleObject so = new ShuffleObject();
                        so.setShirtID(shirtsItemList.get(i).getShirtID());
                        so.setPantID(pantsItemList.get(j).getPantID());
                        shuffleObjectLinkedHashSet.add(so);
                    }
                }
                newShuffledList.addAll(shuffleObjectLinkedHashSet);
                Collections.shuffle(newShuffledList);
            }
            if (counter == newShuffledList.size()) {
                counter = 0;
            } else {
                int shirtID = newShuffledList.get(counter).getShirtID();
                int pantID = newShuffledList.get(counter).getPantID();
                for (int i = 0; i < shirtsItemList.size(); i++) {
                    if (shirtID == shirtsItemList.get(i).getShirtID()) {
                        Collections.swap(shirtsItemList, 0, i);
                        break;
                    }
                }
                for (int i = 0; i < pantsItemList.size(); i++) {
                    if (pantID == pantsItemList.get(i).getPantID()) {
                        Collections.swap(pantsItemList, 0, i);
                        break;
                    }
                }
                counter++;
                shirtsLoadingAdapter.notifyDataSetChanged();
                pantsImageLoadingAdapter.notifyDataSetChanged();
                applyChangesToFavButton(isShirtCombo(shirtsPager.getCurrentItem()), isPantCombo(pantsPager.getCurrentItem()));
            }
        } else {
            showToast(getString(R.string.select_both_shirt_and_pant_item_text));
        }
    }

    public void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }


}
