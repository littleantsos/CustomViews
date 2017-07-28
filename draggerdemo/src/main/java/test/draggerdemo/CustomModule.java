package test.draggerdemo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by hou on 2017/3/15.11:01
 * introduce :
 */
@Module
public class CustomModule {

    @Provides
    @Singleton
    String getName(){
        return "hou";
    }
}
