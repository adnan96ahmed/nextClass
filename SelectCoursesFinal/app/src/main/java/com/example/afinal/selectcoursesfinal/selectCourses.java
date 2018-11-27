package com.example.afinal.selectcoursesfinal;

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

import static android.util.Log.ASSERT;

public class selectCourses extends AppCompatActivity {
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
    private EditText editText;
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

        //Initialize boolean array
        mandatory = new boolean[MAXNUM];
        for (int i = 0; i < MAXNUM; i++){
            mandatory[i] = false;
        }

        //Initialize seekBar & relevant variables
        courseLoadNumText = findViewById(R.id.courseLoadNum);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(4);
        seekBar.setProgress(2);
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
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (counter < MAXNUM) {     //If the course limit hasn't been reached
                    if(addToArray()) {      //If the string given is valid
                        displayText();      //Display courses to both layouts
                    }
                    editText.getText().clear(); //Reset add field
                }
            }
        });

        //Initialize confirm button
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (counter > 0){
                    //LINKING CODE GOES HERE

                }
                else{   //Give warning if no course is added
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please add one ore more valid course", Toast.LENGTH_SHORT);
                    toast.show();
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
        int length2;

        //Parser created to make sure course matches proper format
        String[] parsedString = editText.getText().toString().split("\\s+");
        if (parsedString.length == 2){
            length1 = parsedString[0].length();
            length2 = parsedString[1].length();
            if ((length1 > 2)&&(length1 < 5)&&(parsedString[0].matches("[a-zA-Z]+"))&&
                (length2 == 4)&&(parsedString[1].matches("[0-9]+"))){

                //Add string to array, raise counter & return true
                courses[counter] = editText.getText().toString().toUpperCase();
                counter++;
                return true;
            }
        }
/********************************************************************************************/
        //REMOVE THIS WHEN TESTING IS COMPLETE
        else if (parsedString[0].equals("Gg")){
            courses[counter] = ("CIS 2750");
            counter++;
            displayText();
            courses[counter] = ("CIS 3110");
            counter++;
            displayText();
            courses[counter] = ("CIS 3490");
            counter++;
            displayText();
            courses[counter] = ("CIS 3760");
            counter++;
            displayText();
        }
/********************************************************************************************/
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
        textViewL.setTextSize(24);
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
        textViewT.setTextSize(24);
        newRow.addView(textViewT);
        newRow.addView(manSwitch);
        advancedLayout.addView(newRow, counter-1);
        advancedLayout.setColumnStretchable(0, true);
    }
}
