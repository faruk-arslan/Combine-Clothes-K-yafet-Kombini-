package com.frdevstudio.fr.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraOps extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        dispatchTakePictureIntent();
//        galleryAddPic();
    }

//    private Bitmap scaleImage(Uri imgUri) throws FileNotFoundException {
//        BitmapFactory.Options o=new BitmapFactory.Options();
//        o.inJustDecodeBounds=true;
//        BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri),
//                null,o);
//        final int requiredSize=140;
//
//        int width=o.outWidth,height=o.outHeight;
//        int scale=1;
//        while (true) {
//            if (width / 2 < requiredSize || height / 2 < requiredSize) break;
//            width/=2;
//            height/=2;
//            scale*=2;
//        }
//        BitmapFactory.Options o2=new BitmapFactory.Options();
//        o2.inSampleSize=scale;
//        return BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUri),
//                null,o2);
//    }

    private void sendCapturedImagePath(){
        Intent i = new Intent(this, AddClothes.class);
        i.putExtra("path", mCurrentPhotoPath);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            File imgFile = new  File(mCurrentPhotoPath);
            if(imgFile.exists()){
//                rotateAndSave(imgFile);
                //Resize the image and override it
//                ResizeAndOverride(imgFile);

                //Bitmap resizedBmp = BitmapFactory.decodeFile(ResizeAndOverride(imgFile).getAbsolutePath());
//                Uri imgUri = Uri.fromFile(imgFile);
                sendCapturedImagePath();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.frdevstudio.fr.project",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    public Bitmap orientateBitmap(File file){
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//        ExifInterface ei = null;
//        Bitmap rotatedBitmap = null;
//        try {
//            ei = new ExifInterface(mCurrentPhotoPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                ExifInterface.ORIENTATION_UNDEFINED);
//        switch(orientation) {
//
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                rotatedBitmap = rotateImage(bitmap, 90);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                rotatedBitmap = rotateImage(bitmap, 180);
//                break;
//
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                rotatedBitmap = rotateImage(bitmap, 270);
//                break;
//
//            case ExifInterface.ORIENTATION_NORMAL:
//            default:
//                rotatedBitmap = bitmap;
//        }
//        return rotatedBitmap;
//    }
//
//    public Bitmap rotateImage(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
//                matrix, true);
//    }

//    private void rotateAndSave(File file){
//        //String photopath = tempphoto.getPath().toString();
//        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath);
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
//
//        FileOutputStream fOut;
//        try {
//            fOut = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
//            fOut.flush();
//            fOut.close();
//
//        } catch (FileNotFoundException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }


//    public File ResizeAndOverride(File file){
//        try {
//            // BitmapFactory options to downsize the image
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            o.inSampleSize = 6;
//            // factor of downsizing the image
//
//            FileInputStream inputStream = new FileInputStream(file);
//            //Bitmap selectedBitmap = null;
//            BitmapFactory.decodeStream(inputStream, null, o);
//            inputStream.close();
//
//            // The new size we want to scale to
//            final int REQUIRED_SIZE=75;
//
//            // Find the correct scale value. It should be the power of 2.
//            int scale = 1;
//            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
//
//            BitmapFactory.Options o2 = new BitmapFactory.Options();
//            o2.inSampleSize = scale;
//            inputStream = new FileInputStream(file);
//
//            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
//            inputStream.close();
//
//            // here i override the original image file
//            file.createNewFile();
//            FileOutputStream outputStream = new FileOutputStream(file);
//
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
//
//            return file;
//        } catch (Exception e) {
//            return null;
//        }
//    }

//    private void scalePic() {
//        // Get the dimensions of the View
//        int targetW = imageView.getWidth();
//        int targetH = imageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, boptions);
//        imageView.setImageBitmap(bitmap);
//    }

//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

}
