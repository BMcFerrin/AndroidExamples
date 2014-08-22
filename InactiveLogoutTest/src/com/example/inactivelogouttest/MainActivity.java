package com.example.inactivelogouttest;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	
	MainActivity context;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private BroadcastReceiver br;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		setupAlarm();
	}
	
	private void setupAlarm() {
        br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				// TODO Auto-generated method stub
				// DO YOUR LOGOUT LOGIC HERE
                Toast.makeText(c, "You are now logged out.", Toast.LENGTH_LONG).show();
			}
          };
        registerReceiver(br, new IntentFilter("com.myapp.logout") );
        alarmIntent = PendingIntent.getBroadcast( this, 0, new Intent("com.myapp.logout"),0);
        alarmMgr = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	@Override
    protected void onStop() {
		super.onStop();
        alarmMgr.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (5*1000), alarmIntent );
    }
	
	@Override
	protected void onDestroy() {
	       alarmMgr.cancel(alarmIntent);
	       unregisterReceiver(br);
	       super.onDestroy();
	}

}
