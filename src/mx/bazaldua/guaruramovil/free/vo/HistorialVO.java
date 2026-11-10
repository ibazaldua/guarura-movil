/**
 * 
 */
package mx.bazaldua.guaruramovil.free.vo;

import java.util.Comparator;

import mx.bazaldua.guaruramovil.free.util.Constants;
import android.util.Log;


/**
 * @author bazalduai
 *
 */
public class HistorialVO  implements Comparator<HistorialVO>, Cloneable {
	private int type;
	private String number; 
	private Long date;
	private Long duration;
	private String name;
	private Long _id;
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	/**
	 * @return the duration
	 */
	public Long getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String cached_name) {
		this.name = cached_name;
	}
	/**
	 * @return the _id
	 */
	public Long get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(Long _id) {
		this._id = _id;
	}
	
	@Override
	public int compare(HistorialVO arg0, HistorialVO arg1) {
		return arg1.getDate().compareTo( arg0.getDate() );
	}
	
	@Override
	public Object clone() {
		Object object = null;
		try {
			object = super.clone();
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
		return object;
	}
}
