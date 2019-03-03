package com.sdpd.watup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    boolean doubleBackToExitPressedOnce = false;
    private int RC_SIGN_IN = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDbRef;
    private ProgressDialog mProgress;

    private LinearLayout mGoogleSignInLayout;
    private TextView mGoogleSignInText;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        attachID();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInLayout.setOnClickListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Typeface ralewayBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");

        mGoogleSignInText.setTypeface(ralewayBold);

    }

    private void attachID() {
        mGoogleSignInLayout = findViewById(R.id.layout_google_sign_in);
        mGoogleSignInText = findViewById(R.id.text_google_sign_in);

    }

    private void launchGoogleSignInIntent() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e(TAG, "launchGoogleSignInIntent: ");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: resCode = " + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_CANCELED) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mProgress.setMessage("Signing in...");
            mProgress.show();
            if (result.isSuccess()) {
                Log.e(TAG, "onActivityResult: success");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "onActivityResult: not success" + result.getStatus().getStatusMessage());
                mProgress.dismiss();
//                Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Login failed", Snackbar.LENGTH_LONG);
                Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "Login failed", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
            }
        } else {
            Log.d(TAG, "onActivityResult: " + resultCode);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
//                            Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "Authentication failed.", Snackbar.LENGTH_LONG);
                            Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "Authentication failed.", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snackbar.show();
                            mProgress.dismiss();
                        } else {
                            mUser = mAuth.getCurrentUser();
                            if (mUser != null) {
                                checkUser(mUser.getUid());
                                mProgress.dismiss();
                            }else {
                                logout();
                                mProgress.dismiss();
                            }
                        }
                    }
                });

    }

    public void checkUser(String UID){
        usersDbRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersDbRef.child("df").setValue("apple");


        Toast.makeText(LoginActivity.this, usersDbRef.toString(), Toast.LENGTH_SHORT).show();
        usersDbRef.child("df").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Toast.makeText(LoginActivity.this, "yelo", Toast.LENGTH_SHORT).show();
                if (dataSnapshot!=null){
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }else{
                    usersDbRef.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    usersDbRef.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    usersDbRef.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void logout() {
        mAuth.signOut();
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_google_sign_in: {
            case R.id.layout_google_sign_in: {
                if (!isNetworkAvailable(getApplicationContext())) {
//                    Snackbar snackbar = Snackbar.make(mGoogleSignInBtn, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    Snackbar snackbar = Snackbar.make(mGoogleSignInLayout, "No Internet. Can't Sign In.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snackbar.show();
                } else {
                    launchGoogleSignInIntent();
                }
                break;
            }
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onDestroy() {
        if (mProgress != null) mProgress.dismiss(); //fix window leak
        super.onDestroy();
    }
}
