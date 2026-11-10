/**
 * 
 */
package mx.bazaldua.guaruramovil.free.provider;

import android.net.Uri;

/**
 * @author Ivan Bazaldua Ramirez
 *
 */
public class ContactSIM {
	public static final Uri CONTENT_URI		= Uri.parse("content://icc/adn/"); 
	public static final Uri CONTENT_URI_15	= Uri.parse("content://sim/adn/");
	public static final String _ID			= "_id";
	public static final String NAME			= "name";
	public static final String NUMBER		= "number";
	public static final String EMAILS		= "emails";
	
	
}
