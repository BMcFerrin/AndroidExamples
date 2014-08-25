package com.example.applockerservice;

import java.util.List;

import com.example.applockerservice.LockService.LocalBinder;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	static Button StartBtn;
	static Button StopBtn;
	static Context context;
	LockService mService;
	private boolean mBound = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		context = this;
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			StartBtn = (Button) getActivity().findViewById(R.id.startBtn);
			StartBtn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {           		            	
	                // start the service
	            	Intent i = new Intent(getActivity(), LockService.class);
	            	// potentially add data to the intent
	            	//i.putExtra("KEY1", "data needed for the service");
	            	getActivity().bindService(i, mConnection, BIND_AUTO_CREATE);//.startService(i); 
	            	getActivity().startService(i);	            	
	            }
	        });
			
			StopBtn = (Button) getActivity().findViewById(R.id.stopBtn);
			StopBtn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {	            
	                // stop the service
	            	mService.stop();
	            }
	        });
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);							
			
			return rootView;
		}		
	}
	
	public String checkRunningApps() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);        
        //Toast.makeText(context, "getting activities...", Toast.LENGTH_LONG).show();        
        ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
        //Toast.makeText(context, "getting top...", Toast.LENGTH_LONG).show();
        String activityOnTop = ar.topActivity.getPackageName();
        
        return activityOnTop;
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };      

}
