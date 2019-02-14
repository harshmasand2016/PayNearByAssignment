package com.paynearby.paynearbyassignment.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import com.paynearby.paynearbyassignment.db.PayNearbyDatabase;
import java.io.File;

public class PayNearbyApplication extends Application {

    private static File EXTERNAL_IMAGE_FILES_DIR;
    private static File INTERNAL_IMAGES_SHIRTS_DIR,INTERNAL_IMAGES_PANTS_DIR;
    public static final String IMAGES_FOLDER = "Images";
    public static final String SHIRTS_IMAGES_FOLDER = "Shirts Images";
    public static final String PANTS_IMAGES_FOLDER = "Pants Images";
    static PayNearbyApplication application;
    static PayNearbyDatabase database;
    public static final String SHOWCASE_ID = "showcaseID";

    public static PayNearbyApplication getApplication(){
        return application;
    }

    public static PayNearbyDatabase getDatabase(){
        return database;
    }

    public static File getExternalImageFilesDir() {
        return EXTERNAL_IMAGE_FILES_DIR;
    }

    public static File getInternalShirtsImagesDir() {
        return INTERNAL_IMAGES_SHIRTS_DIR;
    }

    public static File getInternalPANTSImagesDir() {
        return INTERNAL_IMAGES_PANTS_DIR;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (database == null) {
            setDatabase(this);
        }

        EXTERNAL_IMAGE_FILES_DIR = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (EXTERNAL_IMAGE_FILES_DIR == null) {
            INTERNAL_IMAGES_SHIRTS_DIR = new File(getDir(IMAGES_FOLDER, MODE_PRIVATE).getPath() + File.separator + SHIRTS_IMAGES_FOLDER);
            if (!INTERNAL_IMAGES_SHIRTS_DIR.exists()) {
                INTERNAL_IMAGES_SHIRTS_DIR.mkdirs();
            }
            INTERNAL_IMAGES_PANTS_DIR = new File(getDir(IMAGES_FOLDER, MODE_PRIVATE).getPath() + File.separator + PANTS_IMAGES_FOLDER);
            if (!INTERNAL_IMAGES_PANTS_DIR.exists()) {
                INTERNAL_IMAGES_PANTS_DIR.mkdirs();
            }
        }
    }

    public static void setDatabase(Context context) {
        if (database == null) {
            database = new PayNearbyDatabase(context);
        }
    }
}

