package views.littleant.com.customview.screenshots;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import views.littleant.com.customview.R;

public class ScreenshotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScreenshotActivity";
    private ImageView imageView;
    Button button;
    EditText editText;
    public static final int MIN_INPUT_TEXT = 0;
    ScreenshotTools.ScreenShotResponse response;

    public static void startActivity(Context context, ScreenshotTools.ScreenShotResponse path) {
        Intent intent = new Intent();
        intent.setClass(context, ScreenshotActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        response = getIntent().getParcelableExtra("path");
        boolean isLandscape = response.width > response.height;
        if (true) {
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            setContentView(R.layout.activity_screenshot_lanscape);
        } else {
            setContentView(R.layout.activity_screenshot_portrait);
        }

        imageView = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);

        editText.addTextChangedListener(textWatcher);
        button.setOnClickListener(this);
//        File file = new File(response.path);
//        Glide.with(this).load(file).into(imageView);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = s.length() > MIN_INPUT_TEXT;
            int res = enable ? R.drawable.share_button_enable_bg : R.drawable.share_button_disable_bg;
            button.setBackgroundResource(res);
        }
    };


    @Override
    public void onClick(View v) {
        button.setEnabled(false);
        button.setText("分享中");
        button.setTextColor(Color.parseColor("#80ffffff"));

        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setText("分享到汇友圈");
                jointPicture();
            }
        }, 3000);
    }

    private void jointPicture() {
        try {
            Paint paint = new Paint();
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmapTitle = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.share_image_title, options).copy(Bitmap.Config.ARGB_8888,true);
            Bitmap bitmapBase = BitmapFactory.decodeStream(new FileInputStream(response.path));
            Bitmap bitmapResult = bitmapBase.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(bitmapResult);
            canvas.drawBitmap(bitmapTitle,0,0,paint);

            int lineStartX = bitmapBase.getWidth() - bitmapTitle.getWidth();
            int lineStartY = bitmapTitle.getHeight();

            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(lineStartX,0,bitmapBase.getWidth(),lineStartY,paint);
            paint.setColor(Color.parseColor("#000000"));
            canvas.drawLine(lineStartX,lineStartY,bitmapBase.getWidth(),lineStartY,paint);

            imageView.setImageBitmap(bitmapResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
