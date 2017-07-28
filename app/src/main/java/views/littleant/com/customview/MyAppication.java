package views.littleant.com.customview;

import android.app.Application;

/**
 * Created by hou on 2017/7/25.14:22
 * introduce :
 */

public class MyAppication extends Application{

    private static MyAppication myAppication;

    @Override
    public void onCreate() {
        super.onCreate();

        myAppication = this;
    }

    public static MyAppication getApplication() {
        return myAppication;
    }
}
