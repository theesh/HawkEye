package com.example.hawkeyetest;

import java.io.ByteArrayOutputStream;
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
	
	/**
	 * Called when the video recording is complete. Gets the recorded
	 * video as a Uri, gets the real path from the Uri, initiliazes a
	 * mediametadataretriever, sets the video's path as its data
	 * source, finds the videos duration, sets the fps and extracts
	 * each frame of the video as a Bitmap (collected in an ArrayList
	 * Prints the memory address of each bitmap along with the frame
	 * number.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
	    	Uri videoUri = data.getData();
	        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
	        String videoPath = getRealPathFromURI(videoUri);
	        mRetriever.setDataSource(videoPath);
	        Log.i("Video Recording", "Set retriever's data source");
	        
	        String duration = mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
	        int duration_ms = Integer.parseInt(duration);
	        int fps = 10;
	        ArrayList<Bitmap> frames = new ArrayList<Bitmap>();
	        Bitmap bmFrame;
	        double totalFrames = duration_ms/1000*fps;
	        ByteArrayOutputStream out = new ByteArrayOutputStream();

	        for(int i = 0; i < totalFrames; i++) {
	        	bmFrame = mRetriever.getFrameAtTime(100000*i , MediaMetadataRetriever.OPTION_CLOSEST);
	        	frames.add(bmFrame);
	        	Log.i("Frame " + i + " :", bmFrame.toString());
	        }
	        Log.i("Bitmap array size", String.valueOf(frames.size()));
	    }
	}
	
	/**
	 * Returns the real path of a video given the Uri.
	 * @param contentUri
	 * @return the real path
	 */
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
