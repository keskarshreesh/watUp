package com.sdpd.watup.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.sdpd.watup.R;


public class WaterLevelGraph extends Fragment {
    public WaterLevelGraph() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_water_level_graph, container, false);
        populateGraphView(view);

        return view;
    }

    private void populateGraphView(View view) {
        GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                new GraphViewData(1, 2.0d)
                , new GraphViewData(2, 1.5d)
                , new GraphViewData(3, 2.5d)
                , new GraphViewData(4, 1.0d)
        });

        LineGraphView graphView = new LineGraphView(
                getActivity() // context
                , "Water Consumption Chart" // heading
        );

        graphView.addSeries(exampleSeries); // data

        try {
            LinearLayout layout;
            layout = view.findViewById(R.id.graph1);
            layout.addView(graphView);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
