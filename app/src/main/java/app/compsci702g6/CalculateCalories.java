package app.compsci702g6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CalculateCalories extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_MESSAGE = "app.compsci702g6.CALORIES_MESSAGE";
    public static String ratio = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exercise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void calculateCalories(View view) {
        Intent intent = new Intent(this, DisplayCaloriesActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText2);
        String timeString = editText.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.editText5);
        String weightString = editText2.getText().toString();

        intent.putExtra(EXTRA_MESSAGE, new String[] {timeString, weightString, ratio});
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
