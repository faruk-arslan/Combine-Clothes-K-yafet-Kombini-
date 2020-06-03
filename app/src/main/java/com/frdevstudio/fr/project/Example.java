package com.frdevstudio.fr.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Example extends AppCompatActivity {
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    ClothesAdapter mAdapter;
    ListView listView;
    private ArrayList<Clothes> clothesArrayList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        new QueryTask().execute("http://dataservice.accuweather.com/forecasts/v1/daily/1day/316938?apikey=rHFKC2GYvDHVmhhKAMGpE0GALg9sNiMD&language=tr&metric=true");


//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        // Inflate the layout for this fragment
//        StorageReference imgRef = storageRef.child("pics/clothes/78ed36e8-2be9-430f-a832-ee68a39b45ac.png");
//        final long ONE_MEGABYTE = 1024 * 1024;
//        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                setImg(bmp);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//    }
//    private void getDataFromDb(){
//        db.collection("users").document(mAuth.getUid()).
//                collection("wardrobe").get().
//                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@android.support.annotation.NonNull Task<QuerySnapshot> task) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            clothesArrayList.add(document.toObject(Clothes.class));
//                        }
//                        listView.setAdapter(mAdapter);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//    }
//    private void setImg(Bitmap b){
//        ImageView imageView=(ImageView) findViewById(R.id.imageView);
//        imageView.setImageBitmap(b);
//    }
    }
    private class QueryTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            int count = urls.length;
            String jsonResult = WeatherOperations.getData(urls[0]);
            return jsonResult;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            Log.v(WeatherOperations.class.getSimpleName(), "JSON result: " + result);

        }
    }
}
