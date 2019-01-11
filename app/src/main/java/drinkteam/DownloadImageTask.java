package drinkteam;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import drinkteam.controleur.Config;

/**
 * Created by Timothée on 05/06/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Object>
{
	private View bmImage;
	private String mode;
	
	public DownloadImageTask(View bmImage) {
		this.bmImage = bmImage;
	}
	
	protected Object doInBackground(String... urls) {
		this.mode = urls[0];
		String urldisplay = urls[1];
		
		try {
			if(mode.equals(Config.STR_USER_PROFILE)){
				InputStream in = new URL(urldisplay).openStream();
				return BitmapFactory.decodeStream(in);
			}
			else if(mode.equals(Config.STR_USER_COVER)){
				InputStream is = new URL(urldisplay).openStream();
				return Drawable.createFromStream(is, "src name");
			}
		}
		catch (Exception e) {
			Log.d("myapp", "image download error");
			Log.d("myapp", e.getMessage());
		}
		
		return null;
	}
	
	protected void onPostExecute(Object result) {
		if(mode.equals(Config.STR_USER_PROFILE)){
			RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory
					.create(Resources.getSystem(), (Bitmap)result);
			RBD.setCornerRadius(200);
			RBD.setAntiAlias(true);
			((ImageView)bmImage).setImageDrawable(RBD);
		}
		else if(mode.equals(Config.STR_USER_COVER)){
			bmImage.setBackground((Drawable)result);
			bmImage.getBackground().setColorFilter(0x7f000000, PorterDuff.Mode.DARKEN);
		}
	}
}
