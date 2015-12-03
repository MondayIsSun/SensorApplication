package mr.lmd.personal.sensorapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void btnOpen(View view) {
        Log.d("ELSeed", "btnOpen");
        OrientationSensorController.getInstance(this).openOrientationSensor(45);
    }

    public void btnClose(View view) {
        Log.d("ELSeed", "btnClose");
        OrientationSensorController.getInstance(this).closeOrientationSensor();
    }
}
