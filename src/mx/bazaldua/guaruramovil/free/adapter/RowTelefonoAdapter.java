package mx.bazaldua.guaruramovil.free.adapter;

import java.util.List;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.vo.PhoneVO;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Iván Bazaldúa Ramírez
 *
 */
public class RowTelefonoAdapter extends ArrayAdapter<PhoneVO> {

	Activity context;
	List<PhoneVO> lstElementos;
	int textViewResourceId;
	
	
	public RowTelefonoAdapter(Activity context, int textViewResourceId, List<PhoneVO> lstElementos) {
		super(context, textViewResourceId, lstElementos);
		this.context            = context;
		this.lstElementos       = lstElementos;
		this.textViewResourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if ( convertView == null ) {
			LayoutInflater inflater = context.getLayoutInflater();
			convertView = inflater.inflate(textViewResourceId, null);
			
			holder = new ViewHolder();
			holder.lblTipo        = (TextView)convertView.findViewById(R.id.lblTipo);
			holder.lblNumero      = (TextView)convertView.findViewById(R.id.lblNumero);
	        
	        convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.lblTipo.setText( ContactUtils.getPhoneType(lstElementos.get( position ).getType(), lstElementos.get( position ).getDescType(), context) );
		holder.lblNumero.setText( lstElementos.get( position ).getNumber() );
        
        return convertView;
	}
	
	/**
	 * Clase para agilizar la vista en ListView
	 * @author Iván Bazaldúa Ramírez
	 *
	 */
	class ViewHolder {
		TextView lblTipo;
		TextView lblNumero;
	}
}
