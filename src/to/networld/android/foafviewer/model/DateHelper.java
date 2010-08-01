package to.networld.android.foafviewer.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Alex Oberhauser
 *
 */
public final class DateHelper {
	
	/**
	 * Date conversion function that takes the date as string and returns the 
	 * representation of the data as long.
	 * 
	 * @param _date Takes a string that looks like yyyy-MM-dd
	 * @return The representation of the date as long value.
	 */
	public static long convertStringToLong(String _date) {
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		 try {
			Date date = df.parse(_date);
			return date.getTime();
		} catch (ParseException e) {
			return (long) 0.0;
		}
	}
}
