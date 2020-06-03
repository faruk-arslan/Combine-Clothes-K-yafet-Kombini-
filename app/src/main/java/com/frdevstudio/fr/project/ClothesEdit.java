package com.frdevstudio.fr.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ClothesEdit extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG=ClothesEdit.class.getSimpleName();
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();

    private FirebaseAuth mAuth;
    FirebaseVisionImage image;
    List<String> items;
    List<String> itemsSubCat;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterSubCat;
    ImageView img;
    Button colorPickerBtn;
    Spinner dropdown;
    Spinner dropDownSubCategory;
    int domimantColor=0xffffffff;
    int counter=0;
    RadioGroup radioGroup;
    String radioButtonVal=null;
    String storagePath;

    String id,tag,cat,subCat,downloadUri;
    int color;
//    Date createTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_edit);

        img = (ImageView) findViewById(R.id.imageView);
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
        //set onclick listener for color picker button
        colorPickerBtn.setOnClickListener(buttonClick);
        //make dropdowns hidden at the start
        dropdown.setVisibility(View.GONE);
        dropDownSubCategory.setVisibility(View.GONE);

        dropdown.setOnItemSelectedListener(this);
        dropDownSubCategory.setOnItemSelectedListener(this);
        getIntentData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            showAlertDialog();
            return true;
        }else if(id==R.id.action_save){
            saveClothes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData(){
        if(getIntent().hasExtra("id_extra")&&
                getIntent().hasExtra("tag_extra")&&
                getIntent().hasExtra("category_extra")&&
                getIntent().hasExtra("subcategory_extra")&&
                getIntent().hasExtra("color_extra")&&
                getIntent().hasExtra("download_uri_extra")){

            id=getIntent().getStringExtra("id_extra");
            tag=getIntent().getStringExtra("tag_extra");
            cat=getIntent().getStringExtra("category_extra");
            subCat=getIntent().getStringExtra("subcategory_extra");
            color= getIntent().getIntExtra("color_extra",0);
            downloadUri=getIntent().getStringExtra("download_uri_extra");

            loadPreviousChooses();
            loadImg();
        }
    }

    private void loadPreviousChooses(){
        //get the previous tag selection
        switch (tag){
            case "Üst Giyim":
                radioGroup.check(R.id.up);
                //get the previous category selection
                switch (cat){
                    case "T-Shirt":
                        dropdown.setSelection(0);
                        break;
                    case "Kazak":
                        dropdown.setSelection(1);
                        break;
                    case "Sweatshirt":
                        dropdown.setSelection(2);
                        break;
                    case "Gömlek":
                        dropdown.setSelection(3);
                        break;
                    case "Mont":
                        dropdown.setSelection(4);
//                        dropDownSubCategory.setVisibility(View.VISIBLE);
                        if(subCat.equals("Kışlık")) dropDownSubCategory.setSelection(0);
                        else if(subCat.equals("Mevsimlik")) dropDownSubCategory.setSelection(1);
                }
                break;
            case "Alt Giyim":
                radioGroup.check(R.id.down);
                switch (cat){
                    case "Kot&Pantolon":
                        dropdown.setSelection(0);
                        break;
                    case "Eşofman":
                        dropdown.setSelection(1);
                        break;
                    case "Şort":
                        dropdown.setSelection(2);
                        break;
                }
                break;
            case "Ayakkabı":
                radioGroup.check(R.id.shoes);
                dropdown.setVisibility(View.GONE);
                if(subCat.equals("Spor")) dropDownSubCategory.setSelection(0);
                else if(subCat.equals("Günlük")) dropDownSubCategory.setSelection(1);
                else dropDownSubCategory.setSelection(2);
                break;
            default:
                break;
        }



        colorPickerBtn.setBackgroundColor(color);
    }

    private void loadImg(){
        Picasso.get().load(downloadUri).into(img);
    }

    @Override
    public void onBackPressed() {
        if(isDataChanged()){
            new AlertDialog.Builder(this)
                    .setTitle("Kaydedilmemiş değişiklikler var")
                    .setMessage("Değişiklikleri kaydetmek ister misiniz?")
                    //.setNegativeButton(android.R.string.no, null)
                    .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
//                        ClothesEdit.super.onBackPressed();
                            finish();
                        }
                    })
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            saveClothes();
//                            ClothesEdit.super.onBackPressed();
                        }
                    }).create().show();
        }else{
            finish();
        }


//        Toast.makeText(this,"Back pressed",Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(this, "Spinner category",Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(this, "Spinner subcategory",Toast.LENGTH_SHORT).show();
                break;
            default:
//                Toast.makeText(this, "Default",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    //for color palette
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Another interface callback
//        Toast.makeText(this, "Choose Countries :", Toast.LENGTH_SHORT).show();
    }

//    private void checkBeforeSave(){
//        if(cat.equals(dropdown.getSelectedItem().toString())&&
//        subCat.equals(dropDownSubCategory.getSelectedItem().toString())&&
//        )
//    }


    private void saveClothes(){

        String tagg = radioButtonVal;
        String category;
        String subCategory;
        if(radioButtonVal.equals("Ayakkabı")) category="Ayakkabı";
        else category= dropdown.getSelectedItem().toString();

        if(dropDownSubCategory.getSelectedItem()==null) subCategory="";
        else subCategory=dropDownSubCategory.getSelectedItem().toString();

        if(!(radioButtonVal.equals("Ayakkabı")||category.equals("Mont")))
            subCategory="";

        Date currentTime = Calendar.getInstance().getTime();
        //create new Clothes object with relevant data
        DocumentReference docRef=db.collection("users").document(mAuth.getUid()).
                collection("wardrobe").document(id);

        db.collection("users").document(mAuth.getUid()).
                collection("wardrobe").document(id).set(new Clothes(id,tagg,category,subCategory,currentTime,domimantColor,downloadUri));

//        docRef.update(new Clothes(id,tag,cat,subCat,currentTime,color,downloadUri))
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                    }
//                });
//
//        Clothes clothesObj=new Clothes(id,tag,category,subCategory,currentTime,domimantColor,downloadUri.toString());
//        db.collection("users").document(mAuth.getUid()).
//                collection("wardrobe").add(clothesObj);
        goBack();
    }

    private void DeleteClothes(){
        db.collection("users").document(mAuth.getUid()).
                collection("wardrobe").document(id).delete();
        goBack();
    }

    private void showAlertDialog(){
//        AlertDialog.Builder builder=new AlertDialog.Builder(this,
//                "Silme İşlemi","Bu kıyafeti silmek istiyor musunuz?");
//        final AlertDialog dialog=builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        }).create();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(111111);
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(111111);
//            }
//        });
//
//        dialog.show();

        new AlertDialog.Builder(this)
                .setTitle("Silme İşlemi")
                .setMessage("Bu kıyafeti silmek istiyor musunuz?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        DeleteClothes();
                    }})
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private boolean isDataChanged(){

        if(tag.equals("Üst Giyim")){
            if(tag.equals(radioButtonVal)){
                if(subCat.equals("")) {
                    if(cat.equals(dropdown.getSelectedItem().toString()))
                        return false;
                    else return true;
                }
                else{
                    if(cat.equals(dropdown.getSelectedItem().toString())&&
                            subCat.equals(dropDownSubCategory.getSelectedItem().toString()))
                        return false;
                    else return true;
                }
            } else return true;
        }else if(tag.equals("Alt Giyim")){
            if(tag.equals(radioButtonVal)){
                if(cat.equals(dropdown.getSelectedItem().toString()))
                    return false;
                else return true;
            }else return true;
        }else{
            if(tag.equals(radioButtonVal)){
                if(subCat.equals(dropDownSubCategory.getSelectedItem().toString()))
                    return false;
                else return true;
            } else return true;
        }
    }

    private void goBack(){

//        Bundle bundle = new Bundle();
//        bundle.putString("isNewClothes", status);
//        WardrobeFragment fragment = new WardrobeFragment();
//        fragment.setArguments(bundle);

//        Toast.makeText(this,"goBack status: "+status,Toast.LENGTH_SHORT).show();
        Intent i=new Intent(ClothesEdit.this,MainActivity.class);
//        i.putExtra("isDataChanged_extra","yes");
        startActivity(i);
        finish();

    }
}
