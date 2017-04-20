package app.compsci702g6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static final String EXTRA_MESSAGE = "app.compsci702g6.MESSAGE";
    public static String ratio = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.exercise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void calculateTime(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText4);
        String caloriesString = editText.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.editText6);
        ;       String weightString = editText2.getText().toString();

        intent.putExtra(EXTRA_MESSAGE, new String[] {caloriesString, weightString, ratio});
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ratio = "0.119";
                System.out.println("Walking ratio: " + ratio);
                break;
            case 1:
                ratio = "0.170";
                System.out.println("Jogging ratio: " + ratio);
                break;
            case 2:
                ratio = "0.208";
                System.out.println("Running ratio: " + ratio);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
