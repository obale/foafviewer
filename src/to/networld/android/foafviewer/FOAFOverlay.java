package to.networld.android.foafviewer;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Alex Oberhauser
 *
 */
public class FOAFOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> foafOverlays = new ArrayList<OverlayItem>();
	private Context foafContext;
	
	public FOAFOverlay(Drawable foafMarker) {
		super(boundCenterBottom(foafMarker));
	}
	
	public FOAFOverlay(Drawable foafMarker, Context context) {
		super(boundCenterBottom(foafMarker));
		this.foafContext = context;
	}
	
	public void addOverlay(OverlayItem geoEntry) {
	    this.foafOverlays.add(geoEntry);
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = this.foafOverlays.get(index);
	  if ( this.foafContext != null) {
		  WebView wv = new WebView(this.foafContext);
		  Dialog dialog = new Dialog(this.foafContext);
		  wv.loadData(item.getSnippet(), "text/html", "utf-8");
		  dialog.setTitle(item.getTitle());
		  dialog.setContentView(wv, new LinearLayout.LayoutParams(450, 500));
		  dialog.show();
		  return true;
	  }
	  return false;
	}

	
	@Override
	protected OverlayItem createItem(int i) { return this.foafOverlays.get(i); }

	@Override
	public int size() { return this.foafOverlays.size(); }

}
