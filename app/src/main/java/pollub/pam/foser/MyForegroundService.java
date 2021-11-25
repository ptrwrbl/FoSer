package pollub.pam.foser;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

public class MyForegroundService extends Service {
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final String CHANNEL_NAME = "FoSer service channel";

    public static final String JOB_MESSAGE = "message";
    public static final String JOB_TIME = "time";
    public static final String JOB_SYNC = "sync";
    public static final String JOB_DOUBLE_SPEED = "double_speed";

    private String jobMessage;
    private Boolean jobTime, jobSync, jobDoubleSpeed;
    private final long jobPeriod = 2000;

    private Context ctx;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;

    private int jobCounter;
    private Timer jobTimer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Notification notification = new Notification.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.circle)
                    .setContentTitle(getString(R.string.service_title))
                    .setShowWhen(jobTime)
                    .setContentText(jobMessage + " " + String.valueOf(jobCounter))
                    .setLargeIcon(BitmapFactory.decodeResource (getResources() , R.drawable.circle ))
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(1, notification);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        notificationIntent = new Intent(ctx, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        jobCounter = 0;
        jobTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                jobCounter++;
                handler.post(runnable);
            }
        };

    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        jobTimer.cancel();
        jobTimer.purge();
        jobTimer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        jobMessage = intent.getStringExtra(JOB_MESSAGE);
        jobTime = intent.getBooleanExtra(JOB_TIME,false);
        jobSync = intent.getBooleanExtra(JOB_SYNC,false);
        jobDoubleSpeed = intent.getBooleanExtra(JOB_DOUBLE_SPEED,false);

        createNotificationChannel();

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
        if(jobSync) {
            jobTimer.schedule(timerTask, 0L, jobDoubleSpeed ? jobPeriod / 2L : jobPeriod);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
