package app.compsci702g6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayCaloriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_calories);

        Intent intent = getIntent();
        String[] calculationInfo = intent.getStringArrayExtra(CalculateCalories.EXTRA_MESSAGE);

        String timeString = calculationInfo[0];
        String weightString = calculationInfo[1];
        String ratioString = calculationInfo[2];

        int time = Integer.parseInt(timeString);
        int weight = Integer.parseInt(weightString);
        double ratio = Double.parseDouble(ratioString);

        double calories = weight*ratio*time;
        calories = Math.round(calories);

        String message = "You will burn " + calories + " calories.";

        TextView textView = (TextView) findViewById(R.id.textView9);
        textView.setText(message);
    }
}
