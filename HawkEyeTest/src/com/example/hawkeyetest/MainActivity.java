package com.example.hawkeyetest;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	
	static final int REQUEST_VIDEO_CAPTURE = 1;
	static final String EXTRA_MESSAGE = "com.example.hawkeyetest.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayActivity.class);
		EditText editText = (EditText) findViewById(R.id.enter_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	public void recordVideo(View view) {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.i("Video Recording", "Entered the onActivityResult method");
		if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
	        //Log.i("Video Recording", "Entered the 'if' block");
	    	Uri videoUri = data.getData();
	    	//Log.i("Video Recording", "Received the intent data");
	        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
	        //Log.i("Video Recording", "Initialised data retriever");
	        String videoPath = getRealPathFromURI(videoUri);
	        //Log.i("Video Recording", videoPath);
	        mRetriever.setDataSource(videoPath);
	        Log.i("Video Recording", "Set retriever's data source");
	        
	        //ArrayList<Bitmap> bArray = new ArrayList<Bitmap>();
	        //Log.i("Video Recording", "Initiliased bitmap array");
	        
	        String durata = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
	        int durata_millisec = Integer.parseInt(durata);
	        int durata_video_micros = durata_millisec * 1000; 
	        int durata_secondi = durata_millisec / 1000; 
	        String bitrate = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);    
	        int fps = 10;
	        int numeroFrameCaptured = fps * durata_secondi;
	        ArrayList<Bitmap> frames = new ArrayList<Bitmap>();
	        Bitmap bmFrame;
	        double totalFotogramas = durata_millisec/1000*fps; //video duration(micro seg)/1000s*fotogramas/s

	        for(int i = 0; i < totalFotogramas; i++){
	        bmFrame = mRetriever.getFrameAtTime(100000*i , MediaMetadataRetriever.OPTION_CLOSEST);
	        frames.add(bmFrame);
	        }
	        Log.i("Bitmap array values", frames.toString());
	        Log.i("Bitmap array size", String.valueOf(frames.size()));
	    }
	}
	
	public String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	public void frameCapture(View view) {
		Intent intent = new Intent(this, FrameCapture.class);
		startActivity(intent);
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

}
