package to.networld.android.foafviewer.error;

import to.networld.android.foafviewer.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * XXX: Seams not to work from the profile window (inner class thread)!!!
 * 
 * @author Alex Oberhauser
 *
 */
public class ErrorDialog extends Dialog {
	private final String errorTitle;
	private final String errorMessage;
	
    private final android.view.View.OnClickListener okButtonListener = new android.view.View.OnClickListener() {
		public void onClick(View _view) {
			System.out.println("Dismiss ErrorDialog!");
			dismiss();
		}
    };
	
	public ErrorDialog(Context _context, String _errorTitle, String _errorMessage) {
		super(_context);
		this.errorTitle = _errorTitle;
		this.errorMessage = _errorMessage;
	}
	
	@Override
	public void onStop() {
		System.out.println("OnStop in ErrorDialog");
	}
	
	@Override
	public void onStart() {
		this.setContentView(R.layout.alert_dialog);
		this.setCancelable(true);
		this.setTitle(this.errorTitle);
		TextView text = (TextView) this.findViewById(R.id.alert_msg);
		text.setText(this.errorMessage);
		ImageView image = (ImageView) this.findViewById(R.id.alert_image);
		image.setImageResource(R.drawable.error_icon);
		Button okButton = (Button)this.findViewById(R.id.alert_ok);
		if (okButton != null)
			okButton.setOnClickListener(this.okButtonListener);	
		this.show();
	}

}
