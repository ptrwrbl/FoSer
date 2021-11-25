package pollub.pam.foser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private Button buttonStart, buttonStop, buttonRestart;
    private TextView serviceState, serviceSettings;
    private String jobMessage;
    private Boolean jobTime, jobSync, jobDoubleSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart     = (Button)findViewById(R.id.button_start);
        buttonStop      = (Button)findViewById(R.id.button_stop);
        buttonRestart   = (Button)findViewById(R.id.button_restart);
        serviceState    = (TextView)findViewById(R.id.state_info);
        serviceSettings = (TextView)findViewById(R.id.settings_info);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_settings: startActivity(new Intent(this,SettingsActivity.class)); return true;
            case R.id.item_exit: finishAndRemoveTask(); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void clickStart(View view) {
        getPreferences();

        Intent startIntent = new Intent(this,MyForegroundService.class);
        startIntent.putExtra(MyForegroundService.JOB_MESSAGE, jobMessage);
        startIntent.putExtra(MyForegroundService.JOB_TIME, jobTime);
        startIntent.putExtra(MyForegroundService.JOB_SYNC, jobSync);
        startIntent.putExtra(MyForegroundService.JOB_DOUBLE_SPEED, jobDoubleSpeed);


        ContextCompat.startForegroundService(this, startIntent);
        updateUI();
    }

    public void clickStop(View view) {
        Intent stopIntent = new Intent(this, MyForegroundService.class);
        stopService(stopIntent);
        updateUI();

    }

    public void clickRestart(View view) {
        clickStop(view);
        clickStart(view);
    }

    private String getPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        jobMessage      = sharedPreferences.getString("message","FoSer");
        jobTime         = sharedPreferences.getBoolean("time", true);
        jobSync         = sharedPreferences.getBoolean("sync",true);
        jobDoubleSpeed  = sharedPreferences.getBoolean("double_speed", false);

        return "Message: "      + jobMessage            + "\n"
                + "time: "         + jobTime.toString()    + "\n"
                + "sync: "         + jobSync.toString()    + "\n"
                + "double_speed: " + jobDoubleSpeed.toString();
    }

    private void updateUI() {
        if(isMyForegroundServiceRunning()) {
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);
            buttonRestart.setEnabled(true);
            serviceState.setText(getString(R.string.info_service_running));
        }

        else {
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
            buttonRestart.setEnabled(false);
            serviceState.setText(getString(R.string.info_service_not_running));
        }

        serviceSettings.setText(getPreferences());
    }

    @SuppressWarnings("deprecation")
    private boolean isMyForegroundServiceRunning() {
        String myServiceName = MyForegroundService.class.getName();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo runningService : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            String runningServiceName = runningService.service.getClassName();
            if(runningServiceName.equals(myServiceName)) {
                return true;
            }
        }

        return false;
    }
}