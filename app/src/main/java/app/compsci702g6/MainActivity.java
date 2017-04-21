package app.compsci702g6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void calculateMinutes(View view) {
        Intent intent = new Intent(this, CalculateMinutes.class);
        startActivity(intent);
    }

    public void calculateCalories(View view) {
        Intent intent = new Intent(this, CalculateCalories.class);
        startActivity(intent);
    }
}
