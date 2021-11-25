package pollub.pam.foser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class MyForegroundService extends Service {
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final String CHANNEL_NAME = "FoSer service channel";

    public static final String JOB_MESSAGE = "message";
    public static final String JOB_TIME = "time";
    public static final String JOB_SYNC = "sync";
    public static final String JOB_DOUBLE_SPEED = "double_speed";

    private String jobMessage;
    private Boolean jobTime, jobSync, jobDoubleSpeed;

    @Override
    public void onCreate() { super.onCreate(); }

    @Override
    public void onDestroy() { super.onDestroy(); }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        jobMessage = intent.getStringExtra(JOB_MESSAGE);
        jobTime = intent.getBooleanExtra(JOB_TIME,false);
        jobSync = intent.getBooleanExtra(JOB_SYNC,false);
        jobDoubleSpeed = intent.getBooleanExtra(JOB_DOUBLE_SPEED,false);

        createNotificationChannel();

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.circle)
                .setContentTitle(getString(R.string.service_title))
                .setShowWhen(jobTime)
                .setContentText(jobMessage)
                .setLargeIcon(BitmapFactory.decodeResource (getResources() , R.drawable.circle ))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        doWork();

        return START_NOT_STICKY;
    }

    private void doWork() {
        String info = "Start working..."    + "\n"
                + "time: "         + jobTime.toString()    + "\n"
                + "sync: "         + jobSync.toString()    + "\n"
                + "double_speed: " + jobDoubleSpeed.toString();

        Toast.makeText(this, info ,Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
