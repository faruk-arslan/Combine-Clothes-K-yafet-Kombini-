package com.frdevstudio.fr.project;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.frdevstudio.fr.project.MainActivity.weather;

public class AddClothes extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG=AddClothes.class.getSimpleName();
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();

    private FirebaseAuth mAuth;
    FirebaseVisionImage image;
    List<String> items;
    List<String> itemsSubCat;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterSubCat;
    String path;
    ImageView img;
    Button colorPickerBtn;
    Button saveBtn;
    Spinner dropdown;
    Spinner dropDownSubCategory;
    ProgressBar progressBar;
    int domimantColor=0xffffffff;
    int counter=0;
    RadioGroup radioGroup;
    String radioButtonVal=null;
    String storagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothes);
        //instance of firebase authentication
        mAuth=FirebaseAuth.getInstance();
        //get the radiogroup
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        //set radiogroup's onclick listener
        radioGroup.setOnCheckedChangeListener(radioButtonCheck);
        //get the spinners from the xml.
        dropdown= findViewById(R.id.spinner);
        dropDownSubCategory=findViewById(R.id.spinner_subcategory);
        //create a list of items for the spinners.
        items=new ArrayList<>();
        itemsSubCat=new ArrayList<>();
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapterSubCat=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,itemsSubCat);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropDownSubCategory.setAdapter(adapterSubCat);
        //color picker button
        colorPickerBtn=(Button) findViewById(R.id.btn_colorpicker);
        //save button
//        saveBtn=(Button) findViewById(R.id.btn_save);
        //set onclick listener for color picker button
        colorPickerBtn.setOnClickListener(buttonClick);
        //set onclick listener for save button
//        saveBtn.setOnClickListener(buttonClick);
        //make dropdowns hidden at the start
        dropdown.setVisibility(View.GONE);
        dropDownSubCategory.setVisibility(View.GONE);

        progressBar=(ProgressBar) findViewById(R.id.progressbar);


        progressBar.setVisibility(View.GONE);

        dropdown.setOnItemSelectedListener(this);
        dropDownSubCategory.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().hasExtra("path")){
            //get image file from path
            File imgFile = new  File(getIntent().getStringExtra("path"));
//            path=imgFile.getAbsolutePath();
            //resize the image and override to original one
//            File resizedImgFile=ResizeAndOverride(imgFile);
            //get the resized image path (same as the original one)
            path=imgFile.getAbsolutePath();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if(counter==0){
                img = (ImageView) findViewById(R.id.imageView);
                //crop and set the pic to the imageview
                setPic();
                Log.d(AddClothes.class.getSimpleName(), "width : " + img.getWidth());
                Log.d(AddClothes.class.getSimpleName(), "height : " + img.getHeight());
            }
            counter++;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Back pressed",Toast.LENGTH_SHORT).show();
    }

    private RadioGroup.OnCheckedChangeListener radioButtonCheck=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {

            List<String> upClothingCat = Arrays.asList( "T-Shirt", "Kazak", "Sweatshirt", "Gömlek", "Mont");
            List<String> bottomClothingCat = Arrays.asList( "Kot&Pantolon", "Eşofman", "Şort");
            List<String> shoesSub = Arrays.asList( "Spor", "Günlük", "Bot&Outdoor");
            // Check which radio button was clicked
            switch (i){
                case R.id.up:
                    //make subcategory dropdown hidden
                    dropDownSubCategory.setVisibility(View.GONE);
                    //make the category dropdown visible
                    dropdown.setVisibility(View.VISIBLE);
                    //clear the arraylist
                    items.clear();
                    //add relevant items
                    items.addAll(upClothingCat);
                    //notify the spinner adapter
                    adapter.notifyDataSetChanged();
                    radioButtonVal="Üst Giyim";
                    break;
                case R.id.down:
                    //make subcategory dropdown hidden
                    dropDownSubCategory.setVisibility(View.GONE);
                    //make the category dropdown visible
                    dropdown.setVisibility(View.VISIBLE);
                    items.clear();
                    items.addAll(bottomClothingCat);
                    adapter.notifyDataSetChanged();
                    radioButtonVal="Alt Giyim";
                    break;
                case R.id.shoes:
                    //make subcategory dropdown visible
                    dropDownSubCategory.setVisibility(View.VISIBLE);
                    //make the category dropdown invisible
                    dropdown.setVisibility(View.GONE);
                    items.clear();
                    dropDownSubCategory.setVisibility(View.VISIBLE);
                    itemsSubCat.clear();
                    itemsSubCat.addAll(shoesSub);
                    adapter.notifyDataSetChanged();
                    adapterSubCat.notifyDataSetChanged();
                    radioButtonVal="Ayakkabı";
                    break;
            }
        }
    };

    private View.OnClickListener buttonClick =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_colorpicker:
                    showColorPickerDialog();
                    break;
            }
        }
    };

    public void showColorPickerDialog(){
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(domimantColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
//                        Toast.makeText(AddClothes.this,"Touched: "+selectedColor,
//                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                        Toast.makeText(AddClothes.this,"Selected: "+selectedColor,
//                                Toast.LENGTH_SHORT).show();
                        domimantColor=selectedColor;
                        colorPickerBtn.setBackgroundColor(domimantColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    // Generate palette synchronously and return it
    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }
    //for dropdowns
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
//        String a= (String) adapterView.getItemAtPosition(i);
//        Toast.makeText(this,a,Toast.LENGTH_LONG).show();
        List<String> coatSub = Arrays.asList( "Kışlık", "Mevsimlik");
        List<String> shoesSub = Arrays.asList( "Spor", "Günlük", "Bot&Outdoor");

        switch (adapterView.getId()){
            case R.id.spinner:
                switch (dropdown.getSelectedItem().toString()){
                    case "Mont":
                        //make the subcategory dropdown visible
                        dropDownSubCategory.setVisibility(View.VISIBLE);
                        //clear the subcategory array
                        itemsSubCat.clear();
                        //add relevant items to subcategory array
                        itemsSubCat.addAll(coatSub);
                        adapterSubCat.notifyDataSetChanged();
                        break;
                    default:
                        //make the subcategory dropdown invisible
                        dropDownSubCategory.setVisibility(View.GONE);
                        break;

                }
                break;
            case R.id.spinner_subcategory:
                break;
            default:
                break;
        }
    }
    //for color palette
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Another interface callback
//        Toast.makeText(this, "Choose Countries :", Toast.LENGTH_SHORT).show();
    }


    public Bitmap orientateBitmap(Bitmap bitmap){
        ExifInterface ei = null;
        Bitmap rotatedBitmap = null;
        try {
            ei = new ExifInterface(getIntent().getStringExtra("path"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

//    private FirebaseVisionImage imageFromBitmap(Bitmap bmp) {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
//        return image;
//    }

//    private void labelObjects(FirebaseVisionImage image){
//        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
//                .getOnDeviceImageLabeler();
//
//        labeler.processImage(image)
//                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
//                    @Override
//                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
//                        // Task completed successfully
//                        // ...
//                        ArrayList<String> predictions=new ArrayList<>();
//                        for (FirebaseVisionImageLabel label: labels) {
//                            String text = label.getText();
//                            String entityId = label.getEntityId();
//                            float confidence = label.getConfidence();
//                            Log.v(AddClothes.class.getSimpleName(),text);
//                            Log.v(AddClothes.class.getSimpleName(),entityId);
//                            Log.v(AddClothes.class.getSimpleName(), String.valueOf(confidence));
////                            if(confidence>=75) predictions.add(text);
//                            if(confidence>=0.75) items.add(text);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Task failed with an exception
//                        // ...
//                    }
//                });
//    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();
        Log.v(AddClothes.class.getSimpleName(), String.valueOf(targetW));
        Log.v(AddClothes.class.getSimpleName(), String.valueOf(targetH));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        //resized bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        //get the rotated bitmap
        Bitmap rotatedBmp=orientateBitmap(bitmap);
        //set the image
        img.setImageBitmap(rotatedBmp);
        //
        //create a palette
        Palette p=createPaletteSync(bitmap);
        //get the dominant color
        domimantColor=p.getDominantColor(0xffffffff);
        //set it as a background of pickcolor button
        colorPickerBtn.setBackgroundColor(domimantColor);

    }

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

    private void saveImg(){
        colorPickerBtn.setVisibility(View.GONE);
        //check if the radiobutton clicked
        if(radioGroup.getCheckedRadioButtonId() == -1 || radioButtonVal==null){
            //if radiobutton is not clicked show toast and return
            Toast.makeText(this,"Lütfen tür seçimi yapın",Toast.LENGTH_SHORT).show();
            return;
        }
        img.setDrawingCacheEnabled(true);
        img.buildDrawingCache();
        Bitmap bitmap=img.getDrawingCache();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        img.setDrawingCacheEnabled(false);
        byte[] data=baos.toByteArray();

        final String path="pics/clothes/"+ UUID.randomUUID()+".png";
        storagePath=path;
        StorageReference pics=storage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg").setCustomMetadata("mykey","myval")
                .build();

        UploadTask uploadTask = pics.putBytes(data,metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                progressBar.setProgress(currentprogress);
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return task.getResult().getMetadata().getReference().getDownloadUrl();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressBar.setVisibility(View.GONE);
                Uri downloadUri=task.getResult();
                saveClothes(downloadUri,storagePath);
                Log.v(TAG,"Uri: "+downloadUri);
            }
        });



//        Task<Uri> task=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                return task.getResult().getMetadata().getReference().getDownloadUrl();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                Uri downloadUri=task.getResult();
//                saveClothes(downloadUri,storagePath);
//                Log.v(TAG,"Uri: "+downloadUri);
//            }
//        });
    }

    private void saveClothes(Uri downloadUri,String path){

        String tag = radioButtonVal;
        String category;
        String subCategory;
        if(radioButtonVal.equals("Ayakkabı")) category="Ayakkabı";
        else category= dropdown.getSelectedItem().toString();
        if(dropDownSubCategory.getSelectedItem()==null) subCategory="";
        else subCategory=dropDownSubCategory.getSelectedItem().toString();
        Date currentTime = Calendar.getInstance().getTime();
        //create new Clothes object with relevant data
        DocumentReference docRef=db.collection("users").document(mAuth.getUid()).
                collection("wardrobe").document();
        String id=docRef.getId();
//        Toast.makeText(this,"Dominant color: "+String.valueOf(domimantColor),Toast.LENGTH_SHORT).show();
        Clothes clothesObj=new Clothes(id,tag,category,subCategory,currentTime,domimantColor,downloadUri.toString());
        docRef.set(clothesObj);
//        db.collection("users").document(mAuth.getUid()).
//                collection("wardrobe").add(clothesObj);
        goBack();
    }

    private void goBack(){
        Intent i=new Intent(AddClothes.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private class LoadImgTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            int count = urls.length;
            String jsonResult = WeatherOperations.getData(urls[0]);
            return jsonResult;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveImg();

                // User chose the "Settings" item, show the app settings UI...
                return true;



            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
