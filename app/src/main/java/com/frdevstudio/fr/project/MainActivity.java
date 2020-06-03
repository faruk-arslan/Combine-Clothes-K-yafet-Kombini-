package com.frdevstudio.fr.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WardrobeFragment.OnFragmentInteractionListener,
CombineFragment.OnFragmentInteractionListener,ForecastFragment.OnFragmentInteractionListener{

    protected static ArrayList<Clothes> clothesArrayList= new ArrayList<>();
    protected static ArrayList<Object> obj=new ArrayList<>();
    protected  static Weather weather;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage= FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        updateUI();
        getSharedPreferences();

//        getIntectData();


//        db.collection("users").document(mAuth.getUid()).
//                collection("wardrobe")



//        Intent i=new Intent(MainActivity.this,Example.class);
//        startActivity(i);

//        Intent i=new Intent(MainActivity.this,SignUp.class);
//        startActivity(i);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Gardırobum"));
        tabLayout.addTab(tabLayout.newTab().setText("Kombin"));
        tabLayout.addTab(tabLayout.newTab().setText("Hava Durumu"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        captureButton=(Button)findViewById(R.id.button);
//        captureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this,CameraOps.class);
//                startActivity(intent);
//            }
//        });
    }

    private void getSharedPreferences(){
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sharedpref_key), Context.MODE_PRIVATE);
        String cityName = sharedPref.getString(getString(R.string.city), null);
//        Toast.makeText(this,cityName,Toast.LENGTH_SHORT).show();
    }

    private void logOut(){
        mAuth.signOut();
        updateUI();
    }

    private void updateUI(){
        if(mAuth.getCurrentUser()==null) {
            Intent i=new Intent(MainActivity.this,SignIn.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i=new Intent(MainActivity.this,PreferencesActivity.class);
            startActivity(i);
            return true;
        }else if(id==R.id.action_logout){
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAttachFragment(Fragment fragment) {

    }

//    private void getIntectData() {
//        if (getIntent().hasExtra("isDataChanged_extra")) {
//            if (getIntent().getStringExtra("isDataChanged_extra").equals("yes")) {
//                Toast.makeText(this,"yesss",Toast.LENGTH_SHORT).show();
//                WardrobeFragment wardrobeFragment = (WardrobeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_wardrobe_container);
//                if (wardrobeFragment != null) {
//                    Toast.makeText(this,"fragment not null",Toast.LENGTH_SHORT).show();
//                    wardrobeFragment.updateData();
//                }
//                else {
//                    Toast.makeText(this,"fragment null",Toast.LENGTH_SHORT).show();
//                    WardrobeFragment newFragment = new WardrobeFragment();
//                    Bundle args = new Bundle();
//                    args.putString(WardrobeFragment.ARG_PARAM1, "yes");
//                    newFragment.setArguments(args);
//
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                    // Replace whatever is in the fragment_container view with this fragment,
//                    // and add the transaction to the back stack so the user can navigate back
//                    transaction.replace(R.id.pager, newFragment);
//                    transaction.addToBackStack(null);
//
//                    // Commit the transaction
//                    transaction.commit();
//                }
//            }
//        }
//    }
}
