package com.frdevstudio.fr.project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.frdevstudio.fr.project.MainActivity.clothesArrayList;
import static com.frdevstudio.fr.project.MainActivity.obj;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WardrobeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WardrobeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WardrobeFragment extends Fragment implements AdapterView.OnItemClickListener {

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    ClothesAdapter mAdapter;
    ListView listView;


    // Create a reference from an HTTPS URL
// Note that in the URL, characters are URL escaped!
    StorageReference httpsReference;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WardrobeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WardrobeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WardrobeFragment newInstance(String param1, String param2) {
        WardrobeFragment fragment = new WardrobeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instance of firebase authentication
        mAuth=FirebaseAuth.getInstance();
//        Log.v(MainActivity.class.getSimpleName(),"Auth: "+mAuth.getCurrentUser().getEmail().toString());
        if(mAuth.getCurrentUser()==null){
            Intent signIn=new Intent(getActivity(),SignIn.class);
            startActivity(signIn);
        }else{
            getDataFromDb();

        }
//        clothesArrayList.clear();
//        obj.clear();
//        if(clothesArrayList.size()==0) getDataFromDb();
//        getDataFromDb();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_wardrobe, container, false);


        mAdapter=new ClothesAdapter(getActivity(),obj);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(mAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),CameraOps.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(getActivity(), "Item: " + i, Toast.LENGTH_SHORT).show();
        Intent clothesEditIntent=new Intent(getContext(),ClothesEdit.class);
        Clothes clicked;
        try {
            clicked=(Clothes) obj.get(i);
        }catch (Exception e){
            return;
        }

        clothesEditIntent.putExtra("id_extra",clicked.getmId());
        clothesEditIntent.putExtra("tag_extra",clicked.getmTag());
        clothesEditIntent.putExtra("category_extra",clicked.getmCategory());
        clothesEditIntent.putExtra("subcategory_extra",clicked.getmSubCategory());
        clothesEditIntent.putExtra("color_extra",clicked.getmColor());
        clothesEditIntent.putExtra("download_uri_extra",clicked.getmDownloadUri());
//        clothesEditIntent.putExtra("create_time_extra",clothesArrayList.get(i).getmCreateTime());
        startActivity(clothesEditIntent);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected void updateData(){
        getDataFromDb();
    }

    private void getDataFromDb(){
        clothesArrayList.clear();
        obj.clear();

        db.collection("users").document(mAuth.getUid()).
                collection("wardrobe").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Clothes c=document.toObject(Clothes.class);
                    clothesArrayList.add(c);
                }
//                obj.addAll(clothesArrayList);

                //sam for categories (to compare with current clothes)
                List<String> sam = new ArrayList<>(Arrays.asList("Üst Giyim","Alt Giyim","Ayakkabı"));
                //counter for delete a category header that don't not have any items
                int counter;
                for(String s:sam){
                    obj.add(new String(s));
                    counter=0;
                    //iterate each clothes and add it to the obj list as ordered
                    for(Clothes c:clothesArrayList){
                        if(c.getmTag().equals(s)){
                            obj.add(c);
                            counter++;
                        }
                    }
                    //if category don't have any clothes, delete it from list
                    //that will appear on listview
                    if(counter==0) obj.remove(obj.size()-1);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
