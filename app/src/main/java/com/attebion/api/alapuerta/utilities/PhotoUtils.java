package com.attebion.api.alapuerta.utilities;

/**
 * Created by amar-pc on 5/28/2016.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/*
 * This is a generic class  to take picture from Camera and select picture from  gallery
 * Created By : Amar Jeet Singh
 * Date: 19-May-2016
 *
 * need to add below permissions in your Manifest.xml file
 * <!-- Accessing camera hardware -->
<uses-feature android:name="android.hardware.camera" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

Store the object of PhotoUtils in onSaveInstanceState method and restore it from bundle in oncreate

 */

public class PhotoUtils {





        private static final String TAG = PhotoUtils.class.getName();

        // Define the class Variables
        //private int picOption;
        private Context mContext;

        private String appImageDirectory;
        String cameraSupportError, galleryAccessError;
        public static int  CAMERA_REQUEST_CODE =113;
        public static int  GALLERY_REQUEST_CODE =115;
        private static PhotoUtils instance = null;

        // set a default height and weight in case view's height and weight are not passed
        int targetW ;
        int targetH;

	/*
	 * Constructor to create the class
	 * # pass the activity context from where the caller is comming
	 * #AppName is needed to create folder in device picture gallery and all the pictures related to this app
	 *  will be stored in that folder
	 *  #String error messages to display to user if there was any error. We need to pass this value from calling
	 *  class because this class does not know which language user is using and calling class has to
	 *  figure out that from string.xml file and pass to this class
	 *
	 */

        private PhotoUtils( Context contex, String appName, String cameraSupportError,String galleryAccessError){
            Log.d(TAG,"In main Constructor");
            //this.picOption = option;
            this.mContext = contex;
            this.appImageDirectory = appName;
            this.cameraSupportError = cameraSupportError;
            this.galleryAccessError = galleryAccessError;

        }

        /*
         * Singleton pattern  to create only one instance of this class
         */
        public static synchronized PhotoUtils getInstance(Context contex, String appName, String cameraSupportError,String galleryAccessError){

            if (instance == null) {
                instance = new PhotoUtils(contex,  appName,  cameraSupportError, galleryAccessError);
            }


            return instance;
        }

        /*
         * This method is called to get the picture from either gallery or camera based on user selection choice
         * #int picOption = the selection as 1 for camera and 2 for select picture from Gallery
         */
    /*
        public void getPicture (int picOption,Uri picUri){
            Log.d(TAG,"In getPicture");
            switch(picOption){

                case 1:
                    getPicturefromCamera(picUri);
                    break;
                case 2:
                    getPicturefromGallery();
                    break;
                default:
                    break;


            }

        }

        /*
         * This method is to process the result from onActivityResult method.
         * requestCode = tells whether request was for Camera or from Gallery
         * resultCode = tells whether the requested app completed successfully or not
         *  iv = is the instance of Image view where to set the image
         */
        public Uri processPicture(int requestCode, int resultCode, ImageView iv, Intent rIntent, Uri picUri) throws IOException{
            Log.d(TAG,"In processPicture");

            if (resultCode != Activity.RESULT_CANCELED){

                if (requestCode == CAMERA_REQUEST_CODE  && resultCode == Activity.RESULT_OK ) {
                    Log.d(TAG,"In processPicture in camera part");
                    decodeAndSetView(iv,picUri,100);
                    Log.d(TAG,"Saving pic at "+picUri.getPath());
                    galleryAddPic(picUri);
                    return picUri;
                }

                if (requestCode == GALLERY_REQUEST_CODE  && resultCode == Activity.RESULT_OK && rIntent !=null && rIntent.getData() != null) {
                    Uri selectedImage = rIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = mContext.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.d(TAG,"pic string path  "+  picturePath);
                    //decodeAndSetView(iv,rIntent.getData());
                    decodeAndSetView(iv,Uri.fromFile(new File(picturePath)),100);

                    //Log.d(TAG,"got  pic at "+ rIntent.getData().getPath());
                    return Uri.fromFile(new File(picturePath));
                }

            }
            return picUri;

        }





	/*
	 * This method is to decode the Image and set it on Image view passed in the call. It will call bitmap
	 * Factory and resize the image based on size of ImageView
	 */

        public  void decodeAndSetView(ImageView iv, Uri picUri,int dim) throws IOException{
            Log.d(TAG,"In decodeAndSetView");

            if(iv.getWidth() != 0 ){
                targetW = iv.getWidth();
            }else{
                if(dim ==0){
                    targetW = 250;
                }else{
                    targetW = dim;
                }

            }

            if(  iv.getHeight() != 0){
                targetH = iv.getHeight();
            }else{
                if(dim ==0){
                    targetW = 250;
                }else {
                    targetH = dim;
                }

            }

            // get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picUri.getPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // determine how much to scale down the image
            int scalefactor = Math.min(photoW/targetW, photoH/targetH);

            // decode the image into bitmap size to fill the view
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scalefactor;

            Bitmap mBitmap = BitmapFactory.decodeFile(picUri.getPath(), bmOptions);
            mBitmap = rotateImageIfRequired(mBitmap,picUri);
            iv.setImageBitmap(mBitmap);
        }

/*
	 * This method is to decode the Image and set it on Image view passed in the call. It will call bitmap
	 * Factory and resize the image based on size of ImageView
	 */

    public  void decodeAndSetView(ImageView iv,Resources res, int resId,int dim) throws IOException{
        Log.d(TAG,"In decodeAndSetView with resource");

        if(iv.getWidth() != 0 ){
            targetW = iv.getWidth();
        }else{
            if(dim ==0){
                targetW = 250;
            }else{
                targetW = dim;
            }

        }

        if(  iv.getHeight() != 0){
            targetH = iv.getHeight();
        }else{
            if(dim ==0){
                targetW = 250;
            }else {
                targetH = dim;
            }

        }

        // get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // determine how much to scale down the image
        int scalefactor = Math.min(photoW/targetW, photoH/targetH);

        // decode the image into bitmap size to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scalefactor;

        Bitmap mBitmap = BitmapFactory.decodeResource(res,resId, bmOptions);

        iv.setImageBitmap(mBitmap);
    }


        /*
         * This method will create Camera intent and open camera all to take picture
         */
       public void getPicturefromCamera(Uri picUri){
            Log.d(TAG,"In getPicturefromCamera");
            if(!isDeviceSupportCamera()){
                Toast.makeText(mContext.getApplicationContext(), cameraSupportError, Toast.LENGTH_LONG).show();
            }else{
                //picUri = getOutputPicUri();
                if(picUri != null){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,picUri);

                    ((Activity)mContext).startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }



            }

        }

	/*
	 * This method is to get the picture from Image Gallery . It will create an intent and open image Gallery to chose
	 * the picture from
	 */

        public void getPicturefromGallery(String title){
            Log.d(TAG,"In getPicturefromGallery");
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Show only images, no videos or anything else
            //intent.setType("image/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            //((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
            ((Activity)mContext).startActivityForResult(Intent.createChooser(intent, title), GALLERY_REQUEST_CODE);

        }


        /*
         * This internal method will check if the  device has camera hardware or not
         */
        public boolean isDeviceSupportCamera() {

            if (mContext.getApplicationContext().getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA)) {
                // this device has a camera
                return true;
            } else {
                // no camera on this device
                return false;
            }
        }

        /*
         * This method will create the Uri for image that is going to be taken by Camera.
         * It will create the folder for app if the folder already does not exists.
         * it will then build the image file name from date and time.
         * then get the full path of created image name and change to uri and send to calling code
         */
        @SuppressLint("SimpleDateFormat")
        public Uri getOutputPicUri(){
            Log.d(TAG,"In getOutputPicUri");
            // get the picture gallery of this application
            File mediaStorageDir = new File(Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appImageDirectory);

            Log.d(TAG,mediaStorageDir.getAbsolutePath());
            //If application picture gallery folder does not exist then create one
            if(!mediaStorageDir.exists()){
                if(!mediaStorageDir.mkdir()){
                    Toast.makeText(mContext.getApplicationContext(),galleryAccessError , Toast.LENGTH_LONG).show();
                    return null;
                }
            }

            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";

            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);
            Log.d(TAG,mediaFile.getAbsolutePath());
            return Uri.fromFile(mediaFile);

        }

        /*
         *  This method will add the image to Image Gallery after Camera finish creating the image
         */
        public void galleryAddPic(Uri picUri) {
            Log.d(TAG,"In galleryAddPic");
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            mediaScanIntent.setData(picUri);
            mContext.sendBroadcast(mediaScanIntent);
        }

	/*
	 * This method gives answer if the image needs to be rotated and if yes then rotate the image and return
	 * the bitmap
	 */

        private  Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

            Log.d(TAG,"In rotateImageIfRequired");
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Log.d(TAG,"In rotateImageIfRequired" + orientation);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }

	/*
	 * This  method will rotate the image by given degree
	 *
	 */

        public Bitmap rotateImage(Bitmap img, int degree) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }

        /*
         * This method is to save image in your app private location. Used for profile image and all
         */
        @SuppressWarnings("resource")
        public boolean saveImageToAppPrivateFolder(String imageName, Uri ImageUri) throws IOException{
            Log.d(TAG,"saveImageToAppPrivateFolder");
            File dirFile=mContext.getFilesDir();
            File destinationFile = new File(dirFile,imageName);
            File sourceFile = new File(ImageUri.getPath());

            if (!sourceFile.exists()){
                return false;
            }

            FileChannel source = null;
            FileChannel destination = null;

            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destinationFile).getChannel();

            if(source !=null && destination !=null){
                destination.transferFrom(source, 0, source.size());
            }

            if(source != null){
                source.close();
            }

            if(destination!= null){
                destination.close();
            }

            Log.d(TAG,"saveImageToAppPrivateFolder destination " + destinationFile.getAbsolutePath());
            Log.d(TAG,"saveImageToAppPrivateFolder source   " + sourceFile.getAbsolutePath());
            return true;
        }
    }




