package test.draggerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomComponent customComponent =
                DaggerCustomComponent.builder().customModule(new CustomModule()).build();
        customComponent.getName();

        ((TextView)findViewById(R.id.hello_word)).setText(customComponent.getName());
    }
}
