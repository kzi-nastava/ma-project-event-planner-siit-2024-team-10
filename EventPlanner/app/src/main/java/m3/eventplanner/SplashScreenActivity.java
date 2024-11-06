package m3.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // requested five seconds wait until homescreen is shown
        int SPLASH_TIME_OUT = 5000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("EventPlanner", "onStart()");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("EventPlanner", "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("EventPlanner", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("EventPlanner", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("EventPlanner", "onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("EventPlanner", "onRestart()");
    }
}