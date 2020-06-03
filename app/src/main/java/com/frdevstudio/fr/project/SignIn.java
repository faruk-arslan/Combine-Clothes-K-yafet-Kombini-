package com.frdevstudio.fr.project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="SignInActivity";
    private FirebaseAuth mAuth;
    //references for the edit texts
    TextView EditTextEmail;
    TextView EditTextPassword;
    Button ButtonSignIn,ButtonSignUpRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //get the email and password field references
        EditTextEmail=(TextView) findViewById(R.id.editTextEmail);
        EditTextPassword=(TextView) findViewById(R.id.editTextPassword);
        ButtonSignIn=(Button) findViewById(R.id.btn_sign_in);
        ButtonSignIn.setOnClickListener(this);
        ButtonSignUpRedirect=(Button) findViewById(R.id.btn_sign_up_redirect);
        ButtonSignUpRedirect.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if(currentUser!=null){
            Intent i =new Intent(SignIn.this,MainActivity.class);
            startActivity(i);
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.btn_sign_up_redirect:
                Intent i=new Intent(SignIn.this,SignUp.class);
                startActivity(i);
                break;
        }
    }

    private void signIn(){
        String email,password;
        //get the email and the password from view
        email=EditTextEmail.getText().toString().trim();
        password=EditTextPassword.getText().toString().trim();
        //check for the email and the password
        if(email==""||password=="") return;
        //login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignIn.this, "Oturum açma başarılı."+user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Oturum açmada bir şeyler ters gitti.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(SignIn.this, "Oturum kapatıldı.",
                Toast.LENGTH_SHORT).show();
    }
}
