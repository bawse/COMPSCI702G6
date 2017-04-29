package app.compsci702g6.Utilities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import app.compsci702g6.Activities.DisplayActivity;
import app.compsci702g6.Activities.MainActivity;
import app.compsci702g6.R;

public class Calculate extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_MESSAGE = "app.compsci701g6.ACTIVITY_MESSAGE";
    public static String ratio = "0";
    public static String activityMinutes = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String activity = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if (activity.equalsIgnoreCase("minutes")) {
            setContentView(R.layout.activity_calculate_minutes);
            activityMinutes = "true";

            createSpinner();
        }
        else {
            setContentView(R.layout.activity_calculate_calories);
            activityMinutes = "false";

            createSpinner();
        }
    }

    public void calculate(View view){
        Intent intent = new Intent(this, DisplayActivity.class);

        if (activityMinutes.equalsIgnoreCase("true")) {
            EditText editText = (EditText) findViewById(R.id.editText4);
            String caloriesString = editText.getText().toString();

            EditText editText2 = (EditText) findViewById(R.id.editText6);
            String weightString = editText2.getText().toString();

            intent.putExtra(EXTRA_MESSAGE, new String[] {"true", caloriesString, weightString, ratio});
        }
        else {
            EditText editText = (EditText) findViewById(R.id.editText2);
            String timeString = editText.getText().toString();

            EditText editText2 = (EditText) findViewById(R.id.editText5);
            String weightString = editText2.getText().toString();

            intent.putExtra(EXTRA_MESSAGE, new String[] {"false", timeString, weightString, ratio});
        }

        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ratio = "0.119";
                break;
            case 1:
                ratio = "0.170";
                break;
            case 2:
                ratio = "0.208";
                break;
            case 3:
                ratio = "0.142";
                break;
            case 4:
                ratio = "0.109";
                break;
            case 5:
                ratio = "0.138";
                break;
            case 6:
                ratio = "0.120";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void createSpinner() {
        Spinner spinner;

        if (activityMinutes.equalsIgnoreCase("true")) {
            spinner = (Spinner) findViewById(R.id.spinner);
        }
        else {
            spinner = (Spinner) findViewById(R.id.spinner2);
        }
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exercise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
}
