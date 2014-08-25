package com.example.applockerservice;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class LockService extends Service{

	ScheduledFuture execHandle;
	
	private String theApp = "camera";
	
	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
    	LockService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LockService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
    public void onCreate(){
        super.onCreate();
        //UNABLE TO SETCONTENTVIEW HERE. METHOD DOESN'T WORK
        /*
        while(true) {
            Toast.makeText(LockService.this,
                    "Your Message", Toast.LENGTH_LONG).show();
        }
        */

     // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            new IntentFilter("my-event"));
    }
    
 // handler for received Intents for the "my-event" event 
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // Extract data included in the Intent
        String message = intent.getStringExtra("message");
        String appToOpen = intent.getStringExtra("app_to_open");
        System.out.println(" [+] - got message: " + message);
        System.out.println(" [+] - got App: " + appToOpen);
        
        // change value
        theApp = message;
        // start the desired app
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(appToOpen);        
        startActivity( LaunchIntent );        
      }
    };
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	// Unregister since the activity is not visible
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    String CURRENT_PACKAGE_NAME = "com.example.applockerservice";
    String lastAppPN = "";
    boolean noDelay = false;
    public static LockService instance;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

    	// test
    	//System.out.println(" [+] - checking activity on top...");
    	//Log.e("activity on Top", " - checking...");
        //Toast.makeText(LockService.this, "Checking activity on top", Toast.LENGTH_LONG).show();
        //checkRunningApps(); 
    	
        scheduleMethod();
        
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        //Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;

        return START_STICKY;
    }

    private void scheduleMethod() {
        // TODO Auto-generated method stub

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        execHandle = scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
            	System.out.println(" [+] - checking activity on top...");
                checkRunningApps();
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public void checkRunningApps() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager
                .getRunningTasks(1);
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        String activityOnTop = ar.topActivity.getPackageName();

        Log.e("activity on Top", "" + activityOnTop);
        //Toast.makeText(LockService.this, "activity on Top" + activityOnTop, Toast.LENGTH_LONG).show();
        
        if (activityOnTop.contains(theApp)) { // camera example
        	System.out.println(" [+] - camera found to be running");
            
            Intent localIntent = new Intent("android.intent.action.VIEW");
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setClassName("com.example.applockerservice", "com.example.applockerservice.AppLockerScreen");
            startActivity(localIntent);
        }
        
        
/*
// Provide the packagename(s) of apps here, you want to show password activity
        if (activityOnTop.contains("com.android.camera")  // you can make this check even better
                || activityOnTop.contains(CURRENT_PACKAGE_NAME)) {
            while(true) {
                Toast.makeText(LockService.this,
                        "Your Message", Toast.LENGTH_LONG).show();
            }

        } else {
            // DO nothing
        }
*/
    }
    
    public void StopExecuters() {
    	execHandle.cancel(true);
    }

    public void stop() {    
    	// Unregister since the activity is not visible
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    	instance.StopExecuters();
        if (instance != null) {        	
            instance.stopSelf();            
        }
    }
}
