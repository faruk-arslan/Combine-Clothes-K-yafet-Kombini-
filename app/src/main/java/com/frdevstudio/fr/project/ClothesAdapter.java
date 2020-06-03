package com.frdevstudio.fr.project;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//Object-->Clothes
public class ClothesAdapter extends ArrayAdapter<Object> {

    ArrayList<Object> mObjects;
    private static final int CLOTHES_ITEM=0;
    private static final int HEADER_ITEM=1;
    private LayoutInflater layoutInflater;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    //sonradan
    private Context mContext;
    private List<Clothes> mClothes;

    //bu da yeni
    public ClothesAdapter(Context context, ArrayList<Object> objects) {
        super(context, 0,objects);
        mObjects=objects;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    public ClothesAdapter(Activity context, ArrayList<Clothes> clothes) {
//        super(context, 0,clothes);
//        //sonradan
//        mClothes=clothes;
//    }


    //bu fonksiyon da sonradan
    @Override
    public int getCount() {
        return mObjects.size();
//        return mClothes.size();
    }

    //sonrdan
    @Nullable
    @android.support.annotation.Nullable
    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }
    //sonra
    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if(mObjects.get(position) instanceof Clothes){
            return CLOTHES_ITEM;
        }else{
            return HEADER_ITEM;
        }
    }
    //sonra
    @Override
    public int getViewTypeCount() {
        return 2;
        //return super.getViewTypeCount();
    }

    @NonNull
    @android.support.annotation.NonNull
    @Override
    public View getView(int position, @Nullable @android.support.annotation.Nullable View convertView, @NonNull @android.support.annotation.NonNull ViewGroup parent) {

        if(convertView==null){
            switch (getItemViewType(position)){
                case CLOTHES_ITEM:
                    convertView=layoutInflater.inflate(R.layout.clothes_view,null);
                    break;
                case HEADER_ITEM:
                    convertView=layoutInflater.inflate(R.layout.clothes_header_view,null);
                    break;
            }
        }
        switch (getItemViewType(position)){
            case CLOTHES_ITEM:

                View listItemView=convertView;
                //check if the existing view is being reused, otherwise inflate the view
                if(listItemView==null){
                    listItemView= LayoutInflater.from(getContext()).inflate(R.layout.clothes_view,parent,false);
                }
                //get the {@link Clothes} object located at this position in the list
                Clothes currentClothes=(Clothes)getItem(position);
                //get the image and views
                ImageView imageView=(ImageView) listItemView.findViewById(R.id.image);
                ImageView ımageViewColor=(ImageView) listItemView.findViewById(R.id.image_color);
                TextView categoryTextView=(TextView)listItemView.findViewById(R.id.tv_category);
                TextView subCategoryTextView=(TextView) listItemView.findViewById(R.id.tv_subcategory);
                //set their contents
                categoryTextView.setText(currentClothes.getmCategory());
                subCategoryTextView.setText(currentClothes.getmSubCategory());
                ımageViewColor.setBackgroundColor(currentClothes.getmColor());
                Picasso.get().load(currentClothes.getmDownloadUri()).into(imageView);
                break;
            case HEADER_ITEM:
                TextView header=(TextView) convertView.findViewById(R.id.header_tv);
                header.setText((String)mObjects.get(position));
                break;
        }
        return convertView;

//        View listItemView=convertView;
//        //check if the existing view is being reused, otherwise inflate the view
//        if(listItemView==null){
//            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.clothes_view,parent,false);
//        }
//        //get the {@link Clothes} object located at this position in the list
//        Clothes currentClothes=getItem(position);
//        //get the image and views
//        ImageView imageView=(ImageView) listItemView.findViewById(R.id.image);
//        TextView tagTextView=(TextView)listItemView.findViewById(R.id.tv_tag);
//        TextView categoryTextView=(TextView)listItemView.findViewById(R.id.tv_category);
//        //set their contents
//        tagTextView.setText(currentClothes.getmTag());
//        categoryTextView.setText(currentClothes.getmCategory());
////        Bitmap b=getImg(currentClothes.getmPath());
////        imageView.setImageBitmap(currentClothes.getmImage());
//
//        //sonradan
//        Picasso.get().load(currentClothes.getmDownloadUri()).into(imageView);
//
//        // Download directly from StorageReference using Glide
//        // (See MyAppGlideModule for Loader registration)
//        // Inflate the layout for this fragment
////        StorageReference imgRef = storageRef.child(currentClothes.getmPath());
////        Glide.with(getContext()).load(imgRef).into(imageView);
////        Bitmap img=getImg(currentClothes.getmDownloadUri());
////        while(img==null);
////        imageView.setImageBitmap(img);
////        Bitmap b=getImg(currentClothes.getmPath());
////        imageView.setImageBitmap(b);
//        //return the whole list item layout so that it can be shown in the ListView
//        return listItemView;
////        return super.getView(position, convertView, parent);
    }
//    public Bitmap getImg(String path){
//        final Bitmap[] bmp = new Bitmap[1];
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        // Inflate the layout for this fragment
//        StorageReference imgRef = storageRef.child(path);
//        final long ONE_MEGABYTE = 1024 * 1024;
//        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                if(b!=null){
//                    Log.v(MainActivity.class.getSimpleName(),
//                            "heyo"+b.toString());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
//            @Override
//            public void onComplete(@android.support.annotation.NonNull Task<byte[]> task) {
//                notifyDataSetChanged();
//            }
//        });
//        return bmp[0];
//    }
}
