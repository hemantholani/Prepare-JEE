package com.madhouse.prepare_jee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.madhouse.prepare_jee.R;
import com.madhouse.prepare_jee.adapter.ProgressDialogAdapter;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 666;
    private TextView preTxt, pareTxt, byTxt, poweredByTxt, skip;
    private Typeface poppins, poppins_light, ench_celeb, poppins_bold;
    private Button loginBtn;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private boolean btnClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preTxt = findViewById(R.id.txt_pre);
        pareTxt = findViewById(R.id.txt_pare);
        byTxt = findViewById(R.id.txt_by);
        skip = findViewById(R.id.skip);
        loginBtn = findViewById(R.id.login_button);
        poweredByTxt = findViewById(R.id.powered_by);
        poppins = Typeface.createFromAsset(getAssets(), "fonts/poppins.ttf");
        poppins_light = Typeface.createFromAsset(getAssets(), "fonts/poppins_light.ttf");
        poppins_bold = Typeface.createFromAsset(getAssets(), "fonts/poppins_bold.ttf");
        ench_celeb = Typeface.createFromAsset(getAssets(), "fonts/enchanting_celebrations.ttf");
        preTxt.setTypeface(poppins_light);
        pareTxt.setTypeface(poppins_bold);
        poweredByTxt.setTypeface(poppins_bold);
        byTxt.setTypeface(ench_celeb);
        loginBtn.setTypeface(poppins_bold);
        skip.setTypeface(poppins_bold);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View prepareByLayout = findViewById(R.id.prepare_by_layout);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                android.support.v4.util.Pair<View, String> pair = android.support.v4.util.Pair.create(prepareByLayout, prepareByLayout.getTransitionName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, pair);
                startActivity(intent, optionsCompat.toBundle());
            }
        });
        //GoogleSignIn Start
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("620207220278-k7p01lov9u8uoi62ru0hlbgbco40ceq9.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClicked = true;
                signIn();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.login_coordinator), "SignInFailed", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signIn();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final ProgressDialogAdapter progressDialogAdapter = new ProgressDialogAdapter(LoginActivity.this);
        progressDialogAdapter.showDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("LoggedIn", true);
                            editor.commit();
                            View prepareByLayout = findViewById(R.id.prepare_by_layout);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            android.support.v4.util.Pair<View, String> pair = android.support.v4.util.Pair.create(prepareByLayout, prepareByLayout.getTransitionName());
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, pair);
                            startActivity(intent, optionsCompat.toBundle());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            final Snackbar snackbar = Snackbar.make(findViewById(R.id.login_coordinator), "SignInFailed", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    signIn();
                                    snackbar.dismiss();
                                }
                            });
                            progressDialogAdapter.hideDialog();
                            snackbar.show();

                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPref", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LoggedIn", true);
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


}

