package com.sdpd.watup.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sdpd.watup.R;


public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        onAutomaticButtonClicked(view);

        return view;
    }

    public void onAutomaticButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.auto_switch:
                if (checked)
                    // Pirates are the best
                    Toast.makeText(getActivity().getApplicationContext(), "Main Switch Control Automated", Toast.LENGTH_SHORT).show();
                break;
            case R.id.notifications:
                if (checked)
                    // Ninjas rule
                    Toast.makeText(getActivity().getApplicationContext(), "Notifications on", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
