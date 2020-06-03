package com.frdevstudio.fr.project;

import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class WeatherOperations {

    private WeatherOperations(){}

    public static String getData(String queryUrl){
        // Create the URL with given query URL
        URL url=null;
        try {
            url=new URL(queryUrl);
        } catch (MalformedURLException e) {
            Log.e(WeatherOperations.class.getSimpleName(),"Error when creating URL!");
        }
        //JSON result string for storage the JSON result as a String
        String jsonResult="";
        if(url!=null){
            HttpURLConnection httpURLConnection=null;
            InputStream inputStream=null;
            try {
                //Make HTTP URL connection
                httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000/*msec*/);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                Log.v(WeatherOperations.class.getSimpleName(),"Connection established.");
                //Check the response code
                if(httpURLConnection.getResponseCode()==200){
                    Log.v(WeatherOperations.class.getSimpleName(),"Response is OK.");
                    //Get and read input stream
                    inputStream=httpURLConnection.getInputStream();
                    StringBuilder stringBuilder=new StringBuilder();
                    //Check the input stream
                    if(inputStream!=null){
                        //Evaluate the input stream and store as a string
                        InputStreamReader isr=new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                        BufferedReader bufferedReader=new BufferedReader(isr);
                        String line=bufferedReader.readLine();
                        while(line!=null){
                            stringBuilder.append(line);
                            line=bufferedReader.readLine();
                        }
                        jsonResult=stringBuilder.toString();
//                        Log.v(WeatherOperations.class.getSimpleName(),"JSON result: "+jsonResult);
                        // Send the JSON result to the function for get the parsed JSON elements

                        //Return the news as a result of this function
                        return jsonResult;
                    }
                }
            } catch (IOException e) {
                Log.e(WeatherOperations.class.getSimpleName(),"Error when making HTTP URL connection!");
            }
        }
        else {
            Log.e(WeatherOperations.class.getSimpleName(),"URL is null!");
        }
        return jsonResult;
    }

    public static Weather parseJSON(String jsonResult) throws JSONException {
        JSONObject mainJSON=new JSONObject(jsonResult);

        JSONObject headline=mainJSON.getJSONObject("Headline");
        int severity=headline.getInt("Severity");
        String headlineText=headline.getString("Text");
        String category=headline.getString("Category");
        String mobileLink=headline.getString("MobileLink");

        JSONArray dailyForecasts=mainJSON.getJSONArray("DailyForecasts");
        JSONObject daily=dailyForecasts.getJSONObject(0);

        JSONObject temperature=daily.getJSONObject("Temperature");
        JSONObject tempMin=temperature.getJSONObject("Minimum");
        long minTemp=tempMin.getLong("Value");
        JSONObject tempMax=temperature.getJSONObject("Maximum");
        long maxTemp=tempMax.getLong("Value");

        JSONObject day=daily.getJSONObject("Day");
        int icon=day.getInt("Icon");
        String dayIconPhrase=day
                .getString("IconPhrase");

        Log.v(Weather.class.getSimpleName(),"headlineee"+headlineText);

        return new Weather(headlineText,category,dayIconPhrase,severity,icon,minTemp,maxTemp);
    }

}
