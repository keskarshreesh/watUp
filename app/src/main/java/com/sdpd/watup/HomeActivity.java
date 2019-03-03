package com.sdpd.watup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdpd.watup.Fragments.Settings;
import com.sdpd.watup.Fragments.TankMeter;
import com.sdpd.watup.Fragments.WaterControl;
import com.sdpd.watup.Fragments.WaterLevelGraph;

public class HomeActivity extends FragmentActivity {

    private FirebaseUser mUser;

    private Settings mSettings;
    private TankMeter mTankMeter;
    private WaterLevelGraph mWaterLevelGraph;
    private WaterControl mWaterControl;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_tank_meter:
                    fragmentTransaction.replace(R.id.main, mTankMeter);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_water_level_graph:
                    fragmentTransaction.replace(R.id.main, mWaterLevelGraph);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_water_control:
                    fragmentTransaction.replace(R.id.main, mWaterControl);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_settings:
                    fragmentTransaction.replace(R.id.main, mSettings);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mSettings = new Settings();
        mTankMeter = new TankMeter();
        mWaterControl = new WaterControl();
        mWaterLevelGraph = new WaterLevelGraph();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        launchRelevantActivitiesIfNeeded();
    }

    private void launchRelevantActivitiesIfNeeded() {

        if(mUser==null){
            Intent i = new Intent(HomeActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}

