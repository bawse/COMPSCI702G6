package app.compsci702g6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    public static final String EXTRA_MESSAGE = "app.compsci702g6.CALCULATE_MESSAGE";
    public static String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void calculateMinutes(View view) {
        Intent intent = new Intent(this, Calculate.class);
        activity = "minutes";

        intent.putExtra(EXTRA_MESSAGE, activity);
        startActivity(intent);
    }

    public void calculateCalories(View view) {
        Intent intent = new Intent(this, Calculate.class);
        activity = "calories";

        intent.putExtra(EXTRA_MESSAGE, activity);
        startActivity(intent);
    }
}
