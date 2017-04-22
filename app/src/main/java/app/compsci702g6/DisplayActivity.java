package app.compsci702g6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    private long hoursToRun = -1;
    private long minutesToRun = -1;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String[] calculationInfo = intent.getStringArrayExtra(Calculate.EXTRA_MESSAGE);

        String activityMinutes = calculationInfo[0];
        String weightString = calculationInfo[2];
        String ratioString = calculationInfo[3];
        String caloriesString;
        String timeString;
        String message;

        int weight = Integer.parseInt(weightString);
        double ratio = Double.parseDouble(ratioString);
        int calories = -1;
        int time = -1;

        if (activityMinutes.equals("true")) {
            setContentView(R.layout.activity_display_message);

            caloriesString = calculationInfo[1];
            calories = Integer.parseInt(caloriesString);

            double minutes = calories/(weight*ratio);
            minutesToRun = Math.round(minutes);

            if (minutesToRun > 60) {
                hoursToRun = minutesToRun/60;
                minutesToRun = minutesToRun%60;
            }
            message = "You have to exercise for ";

            if (hoursToRun > 0) {
                message = message + hoursToRun + " hours and " + minutesToRun + " minutes";
            }
            else {
                message = message + minutesToRun + " minutes";
            }
            textView = (TextView) findViewById(R.id.textView);
        }
        else {
            setContentView(R.layout.activity_display_calories);

            timeString = calculationInfo[1];
            time = Integer.parseInt(timeString);

            double caloriesCalulated = weight*ratio*time;
            caloriesCalulated = Math.round(caloriesCalulated);

            message = "You will burn " + caloriesCalulated + " calories.";
            textView = (TextView) findViewById(R.id.textView9);
        }
        textView.setText(message);
    }

    public void home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
