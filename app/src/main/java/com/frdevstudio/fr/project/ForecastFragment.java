package com.frdevstudio.fr.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import static com.frdevstudio.fr.project.MainActivity.weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView icon;
    TextView minTemp,maxTemp,headlineText,dayIconPhrase;
    View bg;

    private OnFragmentInteractionListener mListener;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForecastFragment newInstance(String param1, String param2) {
        ForecastFragment fragment = new ForecastFragment();
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
        new QueryTask().execute("http://dataservice.accuweather.com/forecasts/v1/daily/1day/316938?apikey=x4FlGQaO7x3Aot6TA5kwUkCDRyERgUUM&language=tr&metric=true");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_forecast, container, false);
        bg=(View) view.findViewById(R.id.forecast_view);
        icon=(ImageView) view.findViewById(R.id.icon);
        minTemp=(TextView) view.findViewById(R.id.tv_temp_min);
        maxTemp=(TextView) view.findViewById(R.id.tv_temp_max);
        headlineText=(TextView) view.findViewById(R.id.tv_headline_text);
        dayIconPhrase=(TextView) view.findViewById(R.id.tv_day_icon_phrase);

        setBackground();

//        setIcon(10);
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

    private class QueryTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            int count = urls.length;
            String jsonResult = WeatherOperations.getData(urls[0]);
            return jsonResult;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
//            Log.v(getActivity().getClass().getSimpleName(),"ahdsdadsa"+result);
            try {
                weather=WeatherOperations.parseJSON(result);
                fillWeatherInfo(weather);
//                Log.v(getActivity().getClass().getSimpleName(),"aha"+weather.toString());
//                Log.v(getActivity().getClass().getSimpleName(),
//                        "Weahter object: "+String.valueOf(weather)
//                +"\n"+"Headline Text: "+weather.getmHeadlineText()
//                +"\n"+"Category: "+weather.getmCategory()
//                +"\n"+"Day Icon Phrase: "+weather.getmDayIconPhrase()
//                +"\n"+"Severity: "+weather.getmSeverity()
//                +"\n"+"Icon: "+weather.getmIcon()
//                +"\n"+"Min: "+weather.getmMinTemp()
//                +"\n"+"Max: "+weather.getmMaxTemp());
            } catch (JSONException e) {
//                Log.v(getActivity().getClass().getSimpleName(),"olmadııı");
                e.printStackTrace();
            }
        }
    }

    private void fillWeatherInfo(Weather weather) {
        if(weather==null) return;
//                        Log.v(getActivity().getClass().getSimpleName(),
//                        "Weahter object: "+String.valueOf(weather)
//                +"\n"+"Headline Text: "+weather.getmHeadlineText()
//                +"\n"+"Category: "+weather.getmCategory()
//                +"\n"+"Day Icon Phrase: "+weather.getmDayIconPhrase()
//                +"\n"+"Severity: "+weather.getmSeverity()
//                +"\n"+"Icon: "+weather.getmIcon()
//                +"\n"+"Min: "+weather.getmMinTemp()
//                +"\n"+"Max: "+weather.getmMaxTemp());
        String minTempText=String.valueOf(weather.getmMinTemp())+"°C";
        String maxTempText=String.valueOf(weather.getmMaxTemp())+"°C";
        setIcon(weather.getmIcon());
        minTemp.setText(minTempText);
        maxTemp.setText(maxTempText);
        headlineText.setText(weather.getmHeadlineText());
        dayIconPhrase.setText(weather.getmDayIconPhrase());
    }

    public void setBackground(){
        Context context=getContext();
        int imageresource = getActivity().getResources().getIdentifier("pine_bg", "drawable", getActivity().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageresource);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bg.setBackground(bitmapDrawable);
    }

    private void setIcon(int iconNum) {

//        int imageresource = getActivity().getResources().getIdentifier("i03_s", "drawable", getActivity().getPackageName());
//        icon.setImageResource(imageresource);

        String iconName="i";
        if(iconNum<10) iconName+="0"+iconNum+"_s";
        else if(iconNum>=10) iconName+=iconNum+"_s";

//        Log.v(getActivity().getClass().getSimpleName(),"aaaaa--"+iconName);
        //get image resource id by image name (without file extension)
        if(isAdded()){
            int imageresource = getActivity().getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName());
            icon.setImageResource(imageresource);
        }else{

        }

    }

}
