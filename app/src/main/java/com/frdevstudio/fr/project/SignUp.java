package com.frdevstudio.fr.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG="SignUpActivity";
    private FirebaseAuth mAuth;
    //references for the edit texts
    TextView EditTextEmail;
    TextView EditTextPassword;
    Button ButtonSignUp;
    Button ButtonChooseCity;
    Spinner spinner;
    String cityName=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //get the email and password field references
        EditTextEmail=(TextView) findViewById(R.id.editTextEmail);
        EditTextPassword=(TextView) findViewById(R.id.editTextPassword);
        ButtonSignUp=(Button) findViewById(R.id.btn_sign_up);
        ButtonSignUp.setOnClickListener(this);

        final String[] cityNames={"İstanbul","Ankara","İzmir","Adana","Adıyaman","Afyonkarahisar","Ağrı","Aksaray","Amasya","Antalya","Ardahan","Artvin","Aydın","Balıkesir","Bartın","Batman","Bayburt","Bilecik","Bingöl","Bitlis","Bolu","Burdur","Bursa","Çanakkale","Çankırı","Çorum","Denizli","Diyarbakır","Düzce","Edirne","Elazığ","Erzincan","Erzurum","Eskişehir","Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Iğdır","Isparta","Kahramanmaraş","Karabük","Karaman","Kars","Kastamonu","Kayseri","Kırıkkale","Kırklareli","Kırşehir","Kilis","Kocaeli","Konya","Kütahya","Malatya","Manisa","Mardin","Mersin","Muğla","Muş","Nevşehir","Niğde","Ordu","Osmaniye","Rize","Sakarya","Samsun","Siirt","Sinop","Sivas","Şırnak","Tekirdağ","Tokat","Trabzon","Tunceli","Şanlıurfa","Uşak","Van","Yalova","Yozgat","Zonguldak"};
        spinner = (Spinner) findViewById(R.id.spinner_choose_city);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, cityNames);
        // Specify the layout to use when the list of choices appears
        //        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if(currentUser!=null) finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_up:
                createUser();
                break;
        }
    }
    //for popup menu
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
//        Toast.makeText(this,menuItem.getTitle().toString()+" clicked",Toast.LENGTH_SHORT).show();
        cityName=menuItem.getTitle().toString();
        return true;
    }

    private void saveSharedPref() {
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(cityName==null){
            Toast.makeText(this,"Şehir girilmeli",Toast.LENGTH_SHORT).show();
            return;
        }
        editor.putString(getString(R.string.city), cityName);
        editor.commit();
    }

    private void createUser(){
        String email,password;
        cityName=spinner.getSelectedItem().toString();
        //get the email and the password from view
        email=EditTextEmail.getText().toString().trim();
        password=EditTextPassword.getText().toString().trim();
        //check for the email and the password
        if(email.equals("")||password.equals("")||email==null||password==null||cityName==null){
            Toast.makeText(this,"Tüm alanlar doldurulmalı",Toast.LENGTH_SHORT).show();
            return;
        }
        //add the user to the database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUp.this, "Hesap oluşturma başarılı.",
                                    Toast.LENGTH_SHORT).show();
                            saveSharedPref();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Hesap oluşturmada bir şeyler ters gitti.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
