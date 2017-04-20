package app.compsci702g6;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    private long hoursToRun = 0;
    private long minutesToRun = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String[] calculationInfo = intent.getStringArrayExtra(MainActivity.EXTRA_MESSAGE);

        String caloriesString = calculationInfo[0];
        String weightString = calculationInfo[1];
        String ratioString = calculationInfo[2];

        int calories = Integer.parseInt(caloriesString);
        int weight = Integer.parseInt(weightString);
        double ratio = Double.parseDouble(ratioString);

        double minutes = calories/(weight*ratio);
        minutesToRun = Math.round(minutes);

        if (minutesToRun > 60) {
            hoursToRun = minutesToRun/60;
            minutesToRun = minutesToRun%60;
        }

        String message = "You have to exercise for ";

        if (hoursToRun > 0) {
            message = message + hoursToRun + " hours and " + minutesToRun + " minutes";
        }
        else {
            message = message + minutesToRun + " minutes";
        }

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }
}

