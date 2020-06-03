package com.frdevstudio.fr.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.primitives.Ints;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PreferencesActivity extends AppCompatActivity {
    private String cityName=null;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSharedPreferences();

        final String[] cityNames={"İstanbul","Ankara","İzmir","Adana","Adıyaman","Afyonkarahisar","Ağrı","Aksaray","Amasya","Antalya","Ardahan","Artvin","Aydın","Balıkesir","Bartın","Batman","Bayburt","Bilecik","Bingöl","Bitlis","Bolu","Burdur","Bursa","Çanakkale","Çankırı","Çorum","Denizli","Diyarbakır","Düzce","Edirne","Elazığ","Erzincan","Erzurum","Eskişehir","Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Iğdır","Isparta","Kahramanmaraş","Karabük","Karaman","Kars","Kastamonu","Kayseri","Kırıkkale","Kırklareli","Kırşehir","Kilis","Kocaeli","Konya","Kütahya","Malatya","Manisa","Mardin","Mersin","Muğla","Muş","Nevşehir","Niğde","Ordu","Osmaniye","Rize","Sakarya","Samsun","Siirt","Sinop","Sivas","Şırnak","Tekirdağ","Tokat","Trabzon","Tunceli","Şanlıurfa","Uşak","Van","Yalova","Yozgat","Zonguldak"};

        spinner = (Spinner) findViewById(R.id.city_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, cityNames);
// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        int index=Arrays.asList(cityNames).indexOf(cityName);
        spinner.setSelection(index);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pref, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveSharedPref();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void saveSharedPref() {
        cityName=spinner.getSelectedItem().toString();
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(cityName==null){
            return;
        }
        editor.putString(getString(R.string.city), cityName);
        Toast.makeText(this,cityName+" kaydedildi",Toast.LENGTH_SHORT).show();
        editor.commit();

        finish();
    }

    private void getSharedPreferences(){
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpref_key), Context.MODE_PRIVATE);
        cityName = sharedPref.getString(getString(R.string.city), null);
        Toast.makeText(this,cityName,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(cityName.equals(spinner.getSelectedItem().toString())) finish();
        else showAlertDialog();
//        super.onBackPressed();
    }

    private void showAlertDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Kaydedilmemiş Değişiklik")
                .setMessage("Şehir değişikliğini kaydetmek istiyor musunuz?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveSharedPref();
                    }})
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

}
