package com.example.afinal.selectcoursesfinal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends Fragment {
    private TextView textView;
    private String jsonString = "courses: { “dept”: MATH, “code”: 4240,  “sections”: [{“ref”:1,“meetings”:[{“days”:10,“type”:“LEC”,“building”:“MCKN”,“room”:“230\",“timeStart”:1000,“timeEnd”:1120}]}] }";

    public TableFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        drawSchedule(view);
        return view;
    }

    public void drawSchedule(View view){
        try {
            JSONObject schedule = new JSONObject(jsonString);
            JSONArray courses = schedule.getJSONArray("courses");
            JSONObject course = courses.getJSONObject(0);
            String dept = course.getString("dept");
            String code = course.getString("code");
            String full = dept + code;
            textView = view.findViewById(R.id.mon1030);
            textView.setText(full);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
