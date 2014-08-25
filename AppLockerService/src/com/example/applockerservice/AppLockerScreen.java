package com.example.applockerservice;

import com.example.applockerservice.MainActivity.PlaceholderFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;

public class AppLockerScreen extends Activity {
	
	Button SkipBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock_screen);
		System.out.println(" [+] - Activity started!");
		
		SkipBtn = (Button) findViewById(R.id.testBtn);
		SkipBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	                           
            	// send broadcast to allow opening of camera app
            	sendMessage();            	
            	/*
            	// open the camera
            	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	startActivity(intent);
            	*/           	
            }
        });
	}
	
	private void sendMessage() {
		System.out.println(" [+] - sending message...");
		Intent intent = new Intent("my-event");
		// add data
		intent.putExtra("message", "turn_camera_lock_off");
		intent.putExtra("app_to_open", "com.sec.android.app.camera"); // somehow need to get correct app to open
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	} 
}
