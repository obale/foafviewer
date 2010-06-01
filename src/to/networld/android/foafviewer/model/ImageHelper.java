package to.networld.android.foafviewer.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public final class ImageHelper {
	/**
	 * Fetches a image from a URL.
	 * 
	 * @param _imageURL The URL to a portrait image.
	 * @return A Drawable object that could be shown in the GUI.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Drawable getDrawable(String _imageURL) throws MalformedURLException,IOException {
		URL url = new URL(_imageURL);
		InputStream is = (InputStream)url.getContent();
		Drawable drawable = Drawable.createFromStream(is, "pic");
		return drawable;
	}
}
