package test.draggerdemo;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by hou on 2017/3/15.11:40
 * introduce :
 */


@Singleton
@Component(modules = {CustomModule.class})
public interface CustomComponent {
    String getName();
}
