package com.frdevstudio.fr.project;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import static com.frdevstudio.fr.project.MainActivity.clothesArrayList;
import static com.frdevstudio.fr.project.MainActivity.weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CombineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CombineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CombineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView view1;
    ImageView view2;
    ImageView view3;
    ImageView view4;
    View bg;
    Button combineButton;


    private OnFragmentInteractionListener mListener;

    public CombineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CombineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CombineFragment newInstance(String param1, String param2) {
        CombineFragment fragment = new CombineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_combine, container, false);
//        Button button=(Button) view.findViewById(R.id.combine_btn);
//        button.setOnClickListener(buttonClick);
        bg=(View) view.findViewById(R.id.combine_layout);

        view1=(ImageView) view.findViewById(R.id.combine_img);
        view1.setVisibility(View.GONE);
        view2=(ImageView) view.findViewById(R.id.combine_img2);
        view2.setVisibility(View.GONE);
        view3=(ImageView) view.findViewById(R.id.combine_img3);
        view3.setVisibility(View.GONE);
        view4=(ImageView) view.findViewById(R.id.combine_img4);
        view4.setVisibility(View.GONE);
        combineButton=view.findViewById(R.id.btn_combine);

        setBackground();

        combineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCombine();
            }
        });

//        displayInfo();
        // Inflate the layout for this fragment
        return view;
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

//    private void displayInfo(){
//        if(clothesArrayList!=null) Toast.makeText(getContext(),"Clothes not null",Toast.LENGTH_SHORT).show();
//        if(weather!=null) Toast.makeText(getContext(),"Weather not null",Toast.LENGTH_SHORT).show();
//    }

//    private View.OnClickListener buttonClick =  new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()){
//                case R.id.combine_btn:
////                    displayInfo();
//                    makeCombine();
//                    break;
//            }
//        }
//    };

    public void setBackground(){
        Context context=getContext();
        int imageresource = getActivity().getResources().getIdentifier("bg_add_edit", "drawable", getActivity().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageresource);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bg.setBackground(bitmapDrawable);
    }

    private void makeCombine(){

        if(clothesArrayList==null||weather==null){
            Toast.makeText(getContext(),"Veriler Yükleniyor...",Toast.LENGTH_SHORT).show();
            return;
        }
        //copy all clothes
        ArrayList<Clothes> clothesCombine= new ArrayList<>();
        clothesCombine.addAll(clothesArrayList);
        Weather weatherCombine=weather;
        ArrayList<Clothes> toDelete= new ArrayList<>();

        for(Clothes c:clothesCombine){


            Log.v(MainActivity.class.getSimpleName(),"<><>"+c.getmCategory());
            if(weatherCombine.getmMaxTemp()<15){
                if(c.getmCategory().equals("T-Shirt")||c.getmCategory().equals("Şort"))
                    toDelete.add(c);
            }else if(weatherCombine.getmMaxTemp()>=15&&weatherCombine.getmMaxTemp()<21){
                if(c.getmSubCategory().equals("Kışlık")||c.getmCategory().equals("Kazak")
                ||c.getmSubCategory().equals("Bot&Outdoor"))
                    toDelete.add(c);
            }else{
                if(c.getmSubCategory().equals("Kışlık")||c.getmCategory().equals("Kazak")
                ||c.getmCategory().equals("Sweatsirt")||c.getmSubCategory().equals("Bot&Outdoor"))
                    toDelete.add(c);
            }
        }

        for(Clothes d:toDelete){
            clothesCombine.remove(d);
        }

        ArrayList<Clothes> upClothing= new ArrayList<>();
        ArrayList<Clothes> upClothingOut= new ArrayList<>();
        ArrayList<Clothes> downClothing= new ArrayList<>();
        ArrayList<Clothes> shoesClothing= new ArrayList<>();

        for (Clothes cl:clothesCombine){
            if(cl.getmTag().equals("Üst Giyim")){
                if(cl.getmCategory().equals("T-Shirt")||cl.getmCategory().equals("Kazak")|| cl.getmCategory().equals("Sweatshirt")||cl.getmCategory().equals("Gömlek")){
                    upClothing.add(cl);
                }else{
                    upClothingOut.add(cl);
                }
            }else if(cl.getmTag().equals("Alt Giyim")){
                downClothing.add(cl);
            }else{
                shoesClothing.add(cl);
            }
        }
        int upRnd,upOutRnd,downRnd,shoesRnd;
        upRnd=upClothing.size();
        upOutRnd=upClothingOut.size();
        downRnd=downClothing.size();
        shoesRnd=shoesClothing.size();
        Random rnd=new Random();

        Clothes upChoose = upClothing.get(rnd.nextInt(upRnd));
        Clothes upChoose2 = upClothingOut.get(rnd.nextInt(upOutRnd));
        Clothes downChoose = downClothing.get(rnd.nextInt(downRnd));
        Clothes shoesChoose = shoesClothing.get(rnd.nextInt(shoesRnd));



//        Clothes upChoose = null;
//        Clothes upChoose2 = null;
//        Clothes downChoose = null;
//        Clothes shoesChoose = null;
//        for (Clothes cl:clothesCombine){
//            if(cl.getmTag().equals("Üst Giyim")){
//                if(upChoose==null)
//                    if(cl.getmCategory().equals("T-Shirt")||cl.getmCategory().equals("Kazak")||
//                    cl.getmCategory().equals("Sweatshirt")||cl.getmCategory().equals("Gömlek"))
//                        upChoose=cl;
//                if(upChoose2==null)
//                    if(cl.getmSubCategory().equals("Mevsimlik")||cl.getmSubCategory().equals("Kışlık"))
//                        upChoose2=cl;
//            }
//            else if(cl.getmTag().equals("Alt Giyim")&&downChoose==null) downChoose=cl;
//            else if(shoesChoose==null) shoesChoose=cl;
//        }


//        Log.v(MainActivity.class.getSimpleName(),"Up: "+upChoose.getmCategory()+
//        "\n"+"Up2: "+upChoose2.getmSubCategory()+
//        "\n"+"Down: "+downChoose.getmCategory()+
//        "\n"+"Shoes: "+shoesChoose.getmCategory());

//        int imageresource = getActivity().getResources().getIdentifier("not_found_image", "drawable", getActivity().getPackageName());
//        icon.setImageResource(imageresource);

        view1.setVisibility(View.VISIBLE);
        view2.setVisibility(View.VISIBLE);
        view3.setVisibility(View.VISIBLE);
        view4.setVisibility(View.VISIBLE);

        if(upChoose==null) view1.setImageResource(R.drawable.not_found_image);
        else Picasso.get().load(upChoose.getmDownloadUri()).into(view1);
        if(upChoose2==null) view2.setImageResource(R.drawable.not_found_image);
        else Picasso.get().load(upChoose2.getmDownloadUri()).into(view2);
        if(downChoose==null) view3.setImageResource(R.drawable.not_found_image);
        else Picasso.get().load(downChoose.getmDownloadUri()).into(view3);
        if(shoesChoose==null) view4.setImageResource(R.drawable.not_found_image);
        else Picasso.get().load(shoesChoose.getmDownloadUri()).into(view4);

    }

}
