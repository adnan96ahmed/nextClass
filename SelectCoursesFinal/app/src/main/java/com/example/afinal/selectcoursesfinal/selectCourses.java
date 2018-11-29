package com.example.afinal.selectcoursesfinal;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ServerError;
import com.android.volley.VolleyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import static android.util.Log.ASSERT;

public class selectCourses extends AppCompatActivity {
    private String[] myCourses = new String[]{};
    private JSONObject mySchedule = new JSONObject();
    private ArrayAdapter<String> adapter;

    //Change this value if the course limit needs to be changed
    private static final int MAXNUM = 6;
    private String term = "W19";

    //Courses stored in this array
    private String[] courses;
    private int counter;//Might need to switch to public

    //Advanced Settings
    private boolean[] mandatory;
    private int courseLoadNum;
    private TextView courseLoadNumText;
    private SeekBar seekBar;

    //Add field elements
    private AutoCompleteTextView editText;
    private Button addButton;

    //Layouts for normal and advanced modes
    private ViewFlipper viewFlipper;
    private LinearLayout linearLayout;
    private TableLayout advancedLayout;

    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);

        //Initialize string array
        courses = new String[MAXNUM];

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        //Initialize boolean array
        mandatory = new boolean[MAXNUM];
        for (int i = 0; i < MAXNUM; i++){
            mandatory[i] = false;
        }

        //Initialize seekBar & relevant variables
        courseLoadNumText = findViewById(R.id.courseLoadNum);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(4);
        seekBar.setProgress(3);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bool) {
                courseLoadNum = progress+2;
                courseLoadNumText.setText(String.valueOf(courseLoadNum));
                Log.println(ASSERT, "seekBar", "courseLoadNum: "+courseLoadNum);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        //Initialize int values
        counter = 0;
        courseLoadNum = 5;

        //Initialize layouts
        viewFlipper = findViewById(R.id.viewFlipper);
        advancedLayout = findViewById(R.id.advancedLayout);
        linearLayout = findViewById(R.id.linearLayout);

        //Initialize Add Field Elements
        editText = findViewById(R.id.editText);
        addButton = findViewById(R.id.addButton);
        addButton.setVisibility(View.INVISIBLE);

        //Initialize confirm button
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (counter > 0){

                    JSONObject jsonBody = new JSONObject();

                    try {
                        jsonBody.put("courseIDs", "[1,2,3,4,5]");
                        jsonBody.put("scheduleSize", "5");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    final String mRequestBody = jsonBody.toString();

                    makeRequest(queue, mRequestBody, "http://10.0.2.2:11770/W19/generate");

                    //LINKING CODE GOES HERE
//                    System.out.println(mySchedule.toString());
//                    Intent intent = new Intent(selectCourses.this, ScheduleActivity.class);
//                    intent.putExtra("courseInfo", mySchedule.toString());
//                    startActivity(intent);
                }
                else{   //Give warning if no course is added
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please add one ore more valid course", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, myCourses);
        editText.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("*******************");
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("match", editText.getText().toString());
                    jsonBody.put("limit", courseLoadNumText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String mRequestBody = jsonBody.toString();

                makeRequest(queue, mRequestBody, "http://10.0.2.2:11880/F18/search");
//                for (int i = 0; i < myCourses.length; i++) {
//                    System.out.println(myCourses[i]);
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//                Toast.makeText(getApplicationContext(), myCourses[pos] + " selected", Toast.LENGTH_LONG).show();
                if (counter < MAXNUM) {     //If the course limit hasn't been reached
                    if(addToArray()) {      //If the string given is valid
                        displayText();      //Display courses to both layouts
                    }
                    editText.getText().clear(); //Reset add field
                }
            }
        });
    }

    //Controls viewFlipper
    public void nextView(View v){
        viewFlipper.showNext();
    }

    public boolean addToArray(){
        int length1;

        //Parser created to make sure course matches proper format
        String[] parsedString = editText.getText().toString().split("\\s+");
        System.out.println(parsedString[0]);
        System.out.println(parsedString[1]);
        if (parsedString.length > 1){
            length1 = parsedString[0].length();
            if ((length1 > 2)&&(length1 < 5)&&(parsedString[0].matches("[a-zA-Z]+"))){
                courses[counter] = editText.getText().toString();
                counter++;
                return true;
            }
        }
        //Sends message if course info is invalid
        Toast toast = Toast.makeText(getApplicationContext(),
                "Please provide a proper course code", Toast.LENGTH_SHORT);
        toast.show();
        return false;

    }

    public void displayText(){
        final int currentValue = counter - 1;

        //Add new row to main layout
        LinearLayout.LayoutParams layoutParamsL = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textViewL = new TextView(this);
        textViewL.setLayoutParams(layoutParamsL);
        textViewL.setText(courses[counter-1]);
        textViewL.setTextColor(Color.BLACK);
        textViewL.setTextSize(18);
        linearLayout.addView(textViewL);

        //Add new row to advanced layout
        TableRow newRow = new TableRow(this);
        TableRow.LayoutParams layoutParamsT = new TableRow.LayoutParams
                (TableRow.LayoutParams.WRAP_CONTENT);
        TextView textViewT = new TextView(this);
        Switch manSwitch = new Switch(this);
        //Initialize switches
        manSwitch.setOnClickListener(new View.OnClickListener() {
            private boolean state = false;
            public void onClick(View v) {
                if (state){
                    state = false;//may have to make variable from counter-1
                    mandatory[currentValue] = false;
                }
                else{
                    state = true;
                    mandatory[currentValue] = true;
                }
                Log.println(ASSERT, "SWITCHES",
                        "Switch "+currentValue+": "+mandatory[currentValue]);
            }
        });
        newRow.setLayoutParams(layoutParamsT);
        textViewT.setText(" "+courses[counter-1]);
        textViewT.setTextColor(Color.BLACK);
        textViewT.setTextSize(18);
        newRow.addView(textViewT);
        newRow.addView(manSwitch);
        advancedLayout.addView(newRow, counter-1);
        advancedLayout.setColumnStretchable(0, true);
    }

    String[] addElement(String[] org, String added) {
        String[] result = Arrays.copyOf(org, org.length +1);
        result[org.length] = added;
        return result;
    }


    public void makeRequest(RequestQueue queue, final String mRequestBody, String url) {

//        String url = "http://10.0.2.2:11880/F18/search";
//        String url = "http://10.0.2.2:11770/W19/generate";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("Response is: " + response);
                        printCourses(response);
                        parseSchedule(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void parseSchedule(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = new JSONArray(jsonObject.get("courses").toString());
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject explrObject = jsonArray.getJSONObject(i);
//                //System.out.println(explrObject);
//                System.out.println(explrObject.get("dept").toString());
//                System.out.println(explrObject.get("code").toString());
//                System.out.println(explrObject.get("sections").toString());
//            }

            JSONObject passingInfo = jsonArray.getJSONObject(0);
            try {
                mySchedule.put("dept", passingInfo.get("dept").toString());
                mySchedule.put("code", passingInfo.get("code").toString());
                mySchedule.put("sections", passingInfo.get("sections").toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Intent intent = new Intent(selectCourses.this, ScheduleActivity.class);
            intent.putExtra("courseInfo", mySchedule.toString().replace("\\", ""));
            startActivity(intent);
            //System.out.println(jsonObject.get("schedules").toString());
            //System.out.println(mySchedule.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void printCourses(String response) {
        String[] replaceList = new String[]{};
        myCourses = replaceList;
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                //System.out.println(explrObject);
                String courseLabel = explrObject.get("dept").toString() + " " +
                        explrObject.get("code").toString() + " " +
                        explrObject.get("title").toString();
                myCourses = addElement(myCourses, courseLabel);
            }
            for (int i = 0; i < myCourses.length; i++) {
                System.out.println(myCourses[i]);
            }
            //final AutoCompleteTextView editText = findViewById(R.id.actv);
            adapter.clear();
            adapter.addAll(myCourses);
            adapter.notifyDataSetChanged();
            //editText.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
