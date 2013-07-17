package com.bizo_mobile.ip_camera;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ServerTest extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_test);
		this.imageView = (ImageView) this.findViewById(R.id.imageView1);
		Button photoButton = (Button) this.findViewById(R.id.button1);
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_test, menu);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 20, out);
			Thread server = new Thread(new Server(out));
			imageView.setImageBitmap(photo);
			server.start();
		}
	}

}
