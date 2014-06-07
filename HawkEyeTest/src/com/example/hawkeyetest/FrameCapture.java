package com.example.hawkeyetest;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class FrameCapture extends Activity implements SurfaceHolder.Callback, PreviewCallback {

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	static final String TAG = "Frame Capture Activity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_frame_capture);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.custom, null);
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if(previewing){
			camera.stopPreview();
			previewing = false;
		}
		if (camera != null){
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.getParameters().setPreviewFormat(ImageFormat.RGB_565);
				camera.startPreview();
				previewing = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Called when the surface is created. Releases any previously
	 * opened cameras and creates a single instance of the Camera
	 * class.
	 * @param holder the SurfaceHolder object created in the onCreate
	 * method
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (camera != null) {
			camera.release();
			camera = null;
		}
		camera = Camera.open();
		camera.getParameters().setPreviewFormat(ImageFormat.RGB_565);
		camera.startPreview();
	}
	
	/**
	 * Called when the surface is destroyed (when the activity
	 * is ended). Stops the camera preview and releases the camera.
	 * @param holder the SurfaceHolder object created in the onCreate
	 */
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}
	
	/**
	 * Inherited from the PreviewCallback interface.
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera cam) {
		Log.i(TAG, "Frame logged");
	}
	
	/**
	 * Called when the Start button is clicked. Begins capturing
	 * frames at 30 fps in the RGB format and stores the RGB values
	 * of each frame captured in an array.
	 */
	public void startCapture(View view) {
		Parameters parameters = camera.getParameters();
		Size size = parameters.getPreviewSize();
	}
	
	/**
	 * Called when the Stop button is clicked. Stops capturing
	 * frames and writes the array to a csv file.
	 */
	public void stopCapture(View view) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.frame_capture, menu);
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
}