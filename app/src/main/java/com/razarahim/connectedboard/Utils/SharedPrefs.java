package com.razarahim.connectedboard.Utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.razarahim.connectedboard.Models.StudentModel;
import com.razarahim.connectedboard.Models.TeacherModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }

    public static void setLoggedInAs(String token) {
        preferenceSetter("setLoggedInAs", token);
    }

    public static String getLoggedInAs() {
        return preferenceGetter("setLoggedInAs");
    }

    public static void setDepartment(String token) {
        preferenceSetter("setDepartment", token);
    }

    public static String getDepartment() {
        return preferenceGetter("setDepartment");
    }

    public static void setTeacherModel(TeacherModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("TeacherModel", json);
    }


    public static TeacherModel getTeacherModel() {
        Gson gson = new Gson();
        TeacherModel model = gson.fromJson(preferenceGetter("TeacherModel"), TeacherModel.class);
        return model;
    }

    public static void setStudentModel(StudentModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("StudentModel", json);
    }

    public static StudentModel getStudentModel() {
        Gson gson = new Gson();
        StudentModel model = gson.fromJson(preferenceGetter("StudentModel"), StudentModel.class);
        return model;
    }


    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }


    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

}
