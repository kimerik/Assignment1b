package com.example.eriksson.assignment1b;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class MyActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener , AdapterView.OnItemSelectedListener, android.widget.CompoundButton.OnCheckedChangeListener {

    // Skapar alla variablar som håller våra värden
    Integer billValue = 0;
    Integer tipValue = 0;
    Integer availabilityValue = 0;
    Integer checkboxFreindlyValue = 0;
    Integer checkboxOpinionValue = 0;
    Integer checkboxSpecialValue = 0;
    Integer spinnerValue = 0;
    Integer waitingValue = 0;

    //Skapar alla vyer som vi behöver
    SeekBar seekBar;
    CheckBox checkboxFriendly,checkboxSpecials,checkboxOpinion;
    RadioButton radioButtonOk, radioButtonBad, radioButtonGood;
    EditText editFinalBill,editTip,editBill;
    Chronometer timer;

    // Array som håller alla värden i ratingadapter
    ArrayAdapter<CharSequence> ratingtAdapter;
    Spinner ratingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

    // Tidtagare
    timer = (Chronometer)findViewById(R.id.chronometer);

    ratingSpinner = (Spinner)findViewById(R.id.ratingSpinner);
    ratingSpinner.setOnItemSelectedListener(this);

    //Hämtar våran adapter
    ratingtAdapter = ArrayAdapter.createFromResource(this, R.array.ratingtArray, android.R.layout.simple_spinner_item);
    ratingtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Kopplar adapter till våran spinner
    ratingSpinner.setAdapter(ratingtAdapter);

    checkboxFriendly = (CheckBox)findViewById(R.id.checkboxFreindly);
    checkboxFriendly.setOnCheckedChangeListener(this);

    checkboxOpinion = (CheckBox)findViewById(R.id.checkboxOpinion);
    checkboxOpinion.setOnCheckedChangeListener(this);

    checkboxSpecials = (CheckBox)findViewById(R.id.checkboxSpecials);
    checkboxSpecials.setOnCheckedChangeListener(this);

    radioButtonBad = (RadioButton)findViewById(R.id.radioButtonBad);
    radioButtonBad.setOnClickListener(this);

    radioButtonOk = (RadioButton)findViewById(R.id.radioButtonOk);
    radioButtonOk.setOnClickListener(this);

    radioButtonGood = (RadioButton)findViewById(R.id.radioButtonGood);
    radioButtonGood.setOnClickListener(this);

    editFinalBill = (EditText)findViewById(R.id.editFinalBill);

    editTip = (EditText)findViewById(R.id.editTip);

    editBill = (EditText)findViewById(R.id.editBill);
    editBill.setText("0");

    seekBar = (SeekBar)findViewById(R.id.seekBar);
    seekBar.setOnSeekBarChangeListener(this);
    seekBar.setMax(50);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Hämtar id
        int id = item.getItemId();
        switch (id) {
            // Starta timer
            case R.id.action_start:
                if (!timerRun) {
                    timer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
                    timer.start()
                    ;
                    timerRun = true;
                }
                break;
            // Pausa timer
            case R.id.action_pause:
                if (timerRun) {
                    timer.stop();
                    timeElapsed = SystemClock.elapsedRealtime() - timer.getBase();
                    timerRun = false;
                }
                break;
            // Starta om våran timer
            case R.id.action_reset:
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime());
                timerRun = false;
                timeElapsed = 0L;
                break;
        }
        // Kalylera om alla värden efter att vi har ändrat.
        calculateFinalPrice();
        return super.onOptionsItemSelected(item);
    }

    Long timeElapsed = 0L;
    Boolean timerRun = false;

    @Override
    public void onClick(View v) {
       Integer id = v.getId();

        switch (id){
            case R.id.radioButtonBad:
                availabilityValue = 0;
                break;
            case R.id.radioButtonOk:
                availabilityValue = 5;

                break;
            case R.id.radioButtonGood:
                availabilityValue = 10;

                break;
            default:
        }

        calculateFinalPrice();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CharSequence rating = ratingtAdapter.getItem(position);
        String currentRating = String.valueOf(rating);
        if(currentRating.equals("Good")){
        spinnerValue = 10;
        }
        else if(currentRating.equals("Ok")){
         spinnerValue = 5;
        }
        else if(currentRating.equals("Bad")){
         spinnerValue = 0;
        }

        calculateFinalPrice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Integer id = buttonView.getId();
        switch (id){
            case R.id.checkboxOpinion:
                if(isChecked){
                    checkboxOpinionValue = 5;
                }
                else{
                    checkboxOpinionValue = 0;
                }
                break;
            case R.id.checkboxFreindly:
                if(isChecked){
                     checkboxFreindlyValue = 5;
                }
                else{
                    checkboxFreindlyValue = 0;
                }
                break;
            case  R.id.checkboxSpecials:
                if(isChecked){
                    checkboxSpecialValue = 5;
                }
                else{
                    checkboxSpecialValue = 0;
                }
                break;
             default:
        }

       calculateFinalPrice();
    }

    public void calculateFinalPrice(){
        // Kollar så att det är ett nummer i editBill !
        if(isNumeric(editBill.getText().toString())){

            if(timeElapsed <= 30000 && timeElapsed != 0){
                waitingValue = 30;
            }
            else{
                waitingValue = 0;
            }

            tipValue = seekBar.getProgress()+waitingValue+checkboxFreindlyValue+checkboxOpinionValue+checkboxSpecialValue+billValue+spinnerValue+availabilityValue;
            billValue = Integer.parseInt(editBill.getText().toString());
            editTip.setText(tipValue.toString());
            editFinalBill.setText(Integer.toString(billValue + tipValue));
        }
        else{
            Toast.makeText(this,R.string.is_nan, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        calculateFinalPrice();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
       calculateFinalPrice();
    }
}
