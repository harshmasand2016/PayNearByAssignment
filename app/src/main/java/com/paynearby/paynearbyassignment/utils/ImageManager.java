package com.paynearby.paynearbyassignment.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.paynearby.paynearbyassignment.BuildConfig;
import com.paynearby.paynearbyassignment.app.PayNearbyApplication;
import com.paynearby.paynearbyassignment.exception.InvalidPathException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;

public class ImageManager {

    public static final int REQUEST_IMAGE_CAPTURE = 11;
    public static final int SELECT_PICTURE = 12;
    private Uri mCurrentImageFileUri = null;
    private Uri mTempImageFileUri = null;
    private Activity activity;
    private Fragment fragment;
    private int samplingFactor = 4;
    private int compressionQualityFactor = 90;
    private static final String FILE_NOT_FOUND_MESSAGE = "Could not find the file mentioned in the URI";

    public ImageManager() {
        //Empty constructor
    }

    public ImageManager(Activity activity) {
        this.activity = activity;
    }

    public ImageManager(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }


    public void captureImage(String type) throws InvalidPathException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = createTempImageFile();
        if (imageFile == null) {
            throw new InvalidPathException("External Files Directory Not Found.");
        } else {
            {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempImageFileUri());
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                if (fragment == null) {
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, getTempImageFileUri(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    List<ResolveInfo> resInfoList = fragment.getContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        fragment.getContext().grantUriPermission(packageName, getTempImageFileUri(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    /**
     * This method allows the user to select an image from the gallery.
     */

    public void selectImageFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (fragment == null) {
            activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        } else {
            fragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

    }

    /**
     * This function has to be compulsorily called from the overridden function onActivityResult in the
     * calling activity.
     *
     * @param requestCode Same as the requestCode parameter in onActivityResult method in calling activity
     * @param resultCode  Same as the resultCode parameter in onActivityResult method in calling activity
     * @param data        Same as the data parameter in onActivityResult method in calling activity
     * @return Uri Returns URI of the newly created image if resultCode is RESULT_OK else returns null
     * @throws Exception Thrown if there is some problem in FileOutputStream or if it cannot find the file or wrong request/result code
     */
    public Uri onActivityResult(int requestCode, int resultCode, Intent data, String userID) throws Exception {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap scaledBitmap = createScaledBitmap(getTempImageFileUri(), getSamplingFactor());

            storeCompressedImageFile(scaledBitmap, getCompressionQualityFactor(), userID);

            deleteTempImageFile(getTempImageFileUri());

            return getCurrentImageFileUri();
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            storeCompressedImageFile(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData()), getCompressionQualityFactor(), getSamplingFactor(), userID);
            return getCurrentImageFileUri();
        } else {
            throw new Exception("Could not upload image\nPlease try again!");
        }

    }

    /**
     * This function returns the URI of the newly created compressed image.
     *
     * @return URI of newly created compressed image if onActivityResult function is called or else it returns null.
     */

    public Uri getCurrentImageFileUri() {
        return mCurrentImageFileUri;
    }

    /**
     * This method deletes the image with the given input Uri.
     *
     * @param imageUri Input image Uri.
     */
    public void deleteImage(Uri imageUri) {
        new File(imageUri.getPath()).delete();
    }

    /**
     * This method sets the sampling factor for sub-sampling the image
     *
     * @param samplingFactor Sampling factor in powers of 2. eg. 2, 4 , 8 ,...
     */
    public void setSamplingFactor(int samplingFactor) {
        this.samplingFactor = samplingFactor;
    }

    public int getSamplingFactor() {
        return samplingFactor;
    }

    /**
     * This method sets the compression quality factor for JPEG compression
     *
     * @param compressionQualityFactor Compression quality factor between 0 - 100. Higher the factor more the quality is preserved.
     */
    public void setCompressionQualityFactor(int compressionQualityFactor) {
        this.compressionQualityFactor = compressionQualityFactor;
    }

    public int getCompressionQualityFactor() {
        return compressionQualityFactor;
    }


    //This method creates a new File which later holds an image.
    private File createImageFile() {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir;
        if (PayNearbyApplication.getExternalImageFilesDir() != null) {
            storageDir = PayNearbyApplication.getExternalImageFilesDir();
        } else {
            storageDir = PayNearbyApplication.getInternalShirtsImagesDir();
        }
        return new File(storageDir, imageFileName + ".jpg");
    }

    //This method creates a new File which later holds an image.
    private File createImageFile(String type) {
        String uniqueString = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir;
        if (PayNearbyApplication.getExternalImageFilesDir() != null) {
            storageDir = PayNearbyApplication.getExternalImageFilesDir();
        } else {
            if(type.equalsIgnoreCase("shirts")) {
                storageDir = PayNearbyApplication.getInternalShirtsImagesDir();
            }else{
                storageDir = PayNearbyApplication.getInternalPANTSImagesDir();
            }
        }
        return new File(storageDir, type + "_" + uniqueString + ".jpg");
    }

    private void setCurrentImageFileUri(File imageFile) {
        mCurrentImageFileUri = Uri.fromFile(imageFile);
    }

    /**
     * This method compresses and stores the input bitmap in the form of a PNG image.
     *
     * @param sourceBitmap   Input bitmap
     * @param percentQuality Compression Factor(0-100).Higher the factor more the quality is preserved.
     * @throws IOException           Thrown if there is some issue in FileOutputStream
     * @throws FileNotFoundException Thrown if there is some issue in creation of image file.
     */
    private void storeCompressedImageFile(Bitmap sourceBitmap, int percentQuality, int sampleSize, String userID) throws IOException {

        File imageFile = createImageFile(userID);
        FileOutputStream fos = new FileOutputStream(imageFile);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.JPEG, percentQuality, stream);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = sampleSize;
        bmOptions.inPurgeable = true;
        InputStream inputStream = new ByteArrayInputStream(stream.toByteArray());

        Bitmap compressedBitmap = BitmapFactory.decodeStream(inputStream, null, bmOptions);

        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        inputStream.close();

        stream.flush();
        stream.close();

        fos.flush();
        fos.close();

        setCurrentImageFileUri(imageFile);
    }

    /**
     * This method compresses and stores the input bitmap in the form of a PNG image.
     *
     * @param scaledBitmap   Input bitmap
     * @param percentQuality Compression Factor(0-100).Higher the factor more the quality is preserved.
     * @throws IOException           Thrown if there is some issue in FileOutputStream
     * @throws FileNotFoundException Thrown if there is some issue in creation of image file.
     */
    public void storeCompressedImageFile(Bitmap scaledBitmap, int percentQuality, String userID) throws IOException {
        File imageFile = createImageFile(userID);

        FileOutputStream fos = new FileOutputStream(imageFile);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, percentQuality, fos);
        fos.flush();
        fos.close();

        setCurrentImageFileUri(imageFile);

    }

    //This method subsamples the image with the provided sampling factor.
    private Bitmap createScaledBitmap(Uri imageFileUri, int sampleSize) throws FileNotFoundException {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = sampleSize;
        bmOptions.inPurgeable = true;

        Bitmap bitmap;

        if (fragment != null) {
            bitmap = BitmapFactory.decodeStream(fragment.getContext().getContentResolver().openInputStream(imageFileUri), null, bmOptions);
        } else {
            bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageFileUri), null, bmOptions);
        }

        return bitmap;
    }

    //The following 4 functions deal with handling temporary original image created by the Camera
    private File createTempImageFile() {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir;
        if (PayNearbyApplication.getExternalImageFilesDir() != null) {
            storageDir = PayNearbyApplication.getExternalImageFilesDir();
        } else {
            storageDir = PayNearbyApplication.getInternalShirtsImagesDir();
        }
        File imageFile = new File(storageDir, imageFileName + ".jpg");
        setTempImageFileUri(imageFile);
        return imageFile;
    }

    private Uri getTempImageFileUri() {
        return mTempImageFileUri;
    }

    private void setTempImageFileUri(File imageFile) {
        if (imageFile != null) {
            if (fragment != null) {
                mTempImageFileUri = getUriForFile(fragment.getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
            } else {
                mTempImageFileUri = getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
            }
        } else {
            mTempImageFileUri = null;
        }
    }

    private void deleteTempImageFile(Uri tempImageFileUri) {
        new File(tempImageFileUri.getPath()).delete();
        setTempImageFileUri(null);
    }
}

