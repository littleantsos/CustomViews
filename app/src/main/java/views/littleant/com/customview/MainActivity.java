package views.littleant.com.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import views.littleant.com.customview.screenshots.ScreenshotActivity;
import views.littleant.com.customview.screenshots.ScreenshotTools;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity", "onCreate");
        ScreenshotTools screenshotTools = new ScreenshotTools();
        screenshotTools.setOnScreenshotListener(new ScreenshotTools.ScreenShotListener() {
            @Override
            public void onScreenShot(ScreenshotTools.ScreenShotResponse response) {
                Log.i("Mian","OnscreenShot " + response.path + " " + response.dataToken);
                ScreenshotActivity.startActivity(MainActivity.this, response);
            }
        });

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenshotActivity.startActivity(MainActivity.this, null);
            }
        });
    }

}
