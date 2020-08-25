package com.appsinventiv.connectedboard.Utils;

import android.app.Application;
import android.content.Context;



/**
 * Created by AliAh on 11/04/2018.
 */

public class ApplicationClass extends Application {
    private static ApplicationClass instance;



    public static ApplicationClass getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


}
