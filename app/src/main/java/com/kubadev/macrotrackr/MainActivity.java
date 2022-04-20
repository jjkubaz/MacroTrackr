package com.kubadev.macrotrackr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kubadev.macrotrackr.R;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView currentFrac, currentPerc;
    EditText inputText;
    Context context;
    SharedPreferences myPrefs;
    SharedPreferences.Editor prefsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getBaseContext();
        inputText = (EditText) findViewById(R.id.inputCal);
        currentFrac = (TextView) findViewById(R.id.calFrac);
        currentPerc = (TextView) findViewById(R.id.calPerc);
        initialize();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.changeGoal:
                showAlertBox();
                return true;
            case R.id.clearGoal:
                showYesNo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize()
    {
        System.out.println("YES");
        myPrefs = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        System.out.println("YES2");
        prefsEdit = myPrefs.edit();

        int current = myPrefs.getInt("INTAKE", 0);
        int goal = myPrefs.getInt("GOAL", -1);
        int percent = (current * 100) / goal;
        changeViews(current, goal, percent);

        if(goal == -1)
        {
            showAlertBox();
        }

    }

    public void onAddGoal(View view)
    {

        if(!checkInputValid(inputText.getText().toString(), true))
        {
            inputText.setText("");
            return;
        }

        int textIntake = Integer.parseInt(inputText.getText().toString());
        int currentIntakeInt = myPrefs.getInt("INTAKE", 0);
        int newIntake = textIntake + currentIntakeInt;
        prefsEdit.putInt("INTAKE", newIntake);
        prefsEdit.commit();

        int goal = myPrefs.getInt("GOAL", -1);
        int percent = (newIntake * 100) / goal;
        changeViews(newIntake, goal, percent);

    }

    private boolean checkInputValid(String toTest, boolean testGoal)
    {
        if(toTest.equals(""))
        {
            Toast.makeText(context, "Input could not be parsed", Toast.LENGTH_SHORT).show();
            return false;
        }

        try
        {
            int temp = Integer.parseInt(toTest);
        } catch(NumberFormatException nfe)
        {
            Toast.makeText(context, "Input could not be parsed", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(testGoal && myPrefs.getInt("GOAL", -1) == -1)
        {
            Toast.makeText(context, "Goal not set", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void changeViews(int current, int goal, int perc)
    {
        inputText.setText("");
        currentFrac.setText(current + "/" + goal);
        currentPerc.setText(perc + "%");
    }

    public void onSubGoal(View view)
    {

        if(!checkInputValid(inputText.getText().toString(), true))
        {
            inputText.setText("");
            return;
        }

        int textIntake = Integer.parseInt(inputText.getText().toString());
        int currentIntakeInt = myPrefs.getInt("INTAKE", 0);
        int newIntake = currentIntakeInt - textIntake;
        prefsEdit.putInt("INTAKE", newIntake);
        prefsEdit.commit();
        int goal = myPrefs.getInt("GOAL", 0);
        int percent = (newIntake * 100) / goal;
        changeViews(newIntake, goal, percent);
    }

    private void showYesNo()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Would you like to reset today's intake?");

// Set up the inpu
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the tex

// Set up the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefsEdit.putInt("INTAKE", 0);
                prefsEdit.commit();
                int goal = myPrefs.getInt("GOAL", 0);
                int percent = 0;
                changeViews(0, goal, percent);
                Toast.makeText(context, "Day Intake Reset", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showAlertBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Goal");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("INT TO PUT IN : " + input.getText().toString());

                if(!checkInputValid(input.getText().toString(), false))
                {
                    //Toast.makeText(context, "Could not parse goal", Toast.LENGTH_SHORT).show();
                    return;
                }

                prefsEdit.putInt("GOAL", Integer.parseInt(input.getText().toString()));
                prefsEdit.commit();
                int current = myPrefs.getInt("INTAKE", 0);
                int goal = myPrefs.getInt("GOAL", -1);
                System.out.println(myPrefs.getInt("GOAL", -1) + " VAL OF MYGOAL");
                int percent = (current * 100) / goal;
                changeViews(current, goal, percent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}