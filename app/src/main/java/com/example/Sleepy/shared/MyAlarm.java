package com.example.Sleepy.shared;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Sleepy.R;
import com.example.Sleepy.ui.activities.AlarmActivity;
import com.example.Sleepy.ui.activities.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MyAlarm extends AppCompatActivity {
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static AlarmManager alarmManager;

    public static void setAlarm(Context context, Calendar time, View view){
        if(new MyPreferences.SettingsApp(context).isBuiltinAlarm()){
            if (time.before(Calendar.getInstance())){
                time.add(Calendar.DATE, 1);
            }

            alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time.getTimeInMillis(), getAlarmInfoPendingIntent(view));
            alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent(view));

            printInfo(view, time);
        }else{
            MyTimer.setAlarmInApp(time, context, view);
        }
    }

    private static void printInfo(View view, Calendar time) {
        Log.i("alarm", "Alarm SET " + sdf.format(time.getTime()));
        Snackbar.make(view, view.getContext().getString(R.string.alarm_set_for) + sdf.format(time.getTime()) + view.getContext().getString(R.string.remaining_time_snackbar) + MyTimer.calcRemainingTimeMinute(new Date(alarmManager.getNextAlarmClock().getTriggerTime()), view.getContext()), Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show();
    }

    private static PendingIntent getAlarmInfoPendingIntent(View view){
        Intent alarmInfoIntent = new Intent(view.getContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.i("alarm", "Alarm INFO");
        return PendingIntent.getActivity(view.getContext(), 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getAlarmActionPendingIntent(View view){
        Intent alarmInfoIntent = new Intent(view.getContext(), AlarmActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.i("alarm", "Alarm START");
        return PendingIntent.getActivity(view.getContext(), 1, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void cancelAlarm(View view){
        try{
            alarmManager.cancel(getAlarmActionPendingIntent(view));
        }catch (Exception ex){
            Log.e("ERROR", "Error cancel alarm " + ex.toString());
        }
    }

    @SuppressLint("MutatingSharedPrefs")
    private static void saveTimeAlarm(Calendar time, View view){
        SharedPreferences prefs = view.getContext().getSharedPreferences("ALARM_MANAGER", Context.MODE_PRIVATE);
        Set<String> TimeList = new HashSet<>();
        if (prefs.getStringSet("ALARM", null) != null) {
            TimeList = prefs.getStringSet("ALARM", null);
        }
        TimeList.add("" + sdf.format(time.getTime()));
        prefs.edit().putStringSet("ALARM", TimeList).apply();
    } //marking for deletion
}