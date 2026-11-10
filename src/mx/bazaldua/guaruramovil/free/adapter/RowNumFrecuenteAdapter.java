package mx.bazaldua.guaruramovil.free.adapter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.vo.FrecuenteVO;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Iván Bazaldúa Ramírez
 *
 */
public class RowNumFrecuenteAdapter extends ArrayAdapter<FrecuenteVO> {

	Activity context;
	List<FrecuenteVO> lstElementos;
	Map<Long, Bitmap> mapFotos;
	int textViewResourceId;
	
	/**
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param lstElementos
	 */
	public RowNumFrecuenteAdapter(Activity context, int textViewResourceId, List<FrecuenteVO> lstElementos) {
		super(context, textViewResourceId, lstElementos);
		this.context            = context;
		this.lstElementos       = lstElementos;
		this.textViewResourceId = textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder       = null;
		NumberFormat format     = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits(2);
		
		if ( convertView == null ) {
			LayoutInflater inflater = context.getLayoutInflater();	
			convertView = inflater.inflate(textViewResourceId, null);
			holder = new ViewHolder();
			
			holder.lblNombre   = (TextView)convertView.findViewById(R.id.lblNombre);
			holder.lblTelefono = (TextView)convertView.findViewById(R.id.lblTelefono);
			holder.lblDuracion = (TextView)convertView.findViewById(R.id.lblDuracionLlamada);
			holder.imgRemarcar = (ImageView)convertView.findViewById(R.id.imgRemarcar);
			holder.imgFoto     = (ImageView)convertView.findViewById(R.id.imgFoto);			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblNombre.setText( lstElementos.get( position ).getNombre() );
		holder.lblTelefono.setText( lstElementos.get( position ).getTelefono() );
		holder.lblDuracion.setText( format.format( lstElementos.get( position ).getMinutos() ) + ":" + format.format( lstElementos.get( position ).getSegundos() ) );
		holder.imgRemarcar.setImageResource( lstElementos.get( position ).getRemarcar() ? android.R.drawable.checkbox_on_background  : android.R.drawable.checkbox_off_background );
        if ( lstElementos.get(position).getFoto() != null ) holder.imgFoto.setImageBitmap( lstElementos.get( position ).getFoto() );
        else holder.imgFoto.setImageResource( R.drawable.icon );
        
        return convertView;
	}

	/**
	 * @return the mapFotos
	 */
	public Map<Long, Bitmap> getMapFotos() {
		return mapFotos;
	}

	/**
	 * @param mapFotos the mapFotos to set
	 */
	public void setMapFotos(Map<Long, Bitmap> mapFotos) {
		this.mapFotos = mapFotos;
	}
	
	/**
	 * Clase para agilizar la vista en ListView
	 * @author Iván Bazaldúa Ramírez
	 *
	 */
	class ViewHolder {
        TextView lblNombre;
        TextView lblTelefono;
        TextView lblDuracion;
        ImageView imgRemarcar;
        ImageView imgFoto;
	}

}
