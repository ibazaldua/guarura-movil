package mx.bazaldua.guaruramovil.free.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.DateUtil;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.LlamadaVO;
import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.CallLog.Calls;
import android.text.format.DateFormat;
import android.util.Log;
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
public class RowLLamadaAdapter extends ArrayAdapter<LlamadaVO> {

	private Activity context;
	private List<LlamadaVO> lstElementos;
	private Map<Long, Bitmap> mapFotos;
	private int textViewResourceId;
	private Map<String, Long> telefonoBuscado = new HashMap<String, Long>();
	private java.text.DateFormat dateFormat;
	private java.text.DateFormat timeFormat;
	
	
	public RowLLamadaAdapter(Activity context, int textViewResourceId, List<LlamadaVO> lstElementos) {
		super(context, textViewResourceId, lstElementos);
		this.context            = context;
		this.lstElementos       = lstElementos;
		this.textViewResourceId = textViewResourceId;
		dateFormat              = DateFormat.getDateFormat(context);
		timeFormat              = DateFormat.getTimeFormat(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        int tipo          = lstElementos.get( position ).getTipo();
        Long id           = -1L;
        String telefono   = null;

        if ( convertView == null ) {
        	LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(textViewResourceId, null);
            holder      = new ViewHolder();
            holder.lblNombre   = (TextView)convertView.findViewById( R.id.lblNombre );
            holder.lblTelefono = (TextView)convertView.findViewById( R.id.lblTelefono );
            holder.lblDuracion = (TextView)convertView.findViewById( R.id.lblDuracionLlamada );
            holder.lblFecha    = (TextView)convertView.findViewById( R.id.lblFecha );
            holder.lblHora    = (TextView)convertView.findViewById( R.id.lblHora );
            holder.imgFoto     = (ImageView)convertView.findViewById( R.id.imgFoto );
            holder.imgTipo     = (ImageView)convertView.findViewById( R.id.imgTipo );

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
		try {
			telefono = lstElementos.get( position ).getTelefono();
			if ( telefonoBuscado.containsKey( telefono ) ) {
				lstElementos.get( position ).setId( telefonoBuscado.get(telefono) );
			} else {
				ContactVO vo = ContactUtils.getContactByPhone( telefono, context.getContentResolver());
				if ( vo != null ) id = vo.getId();
				
				if ( id > -1 && !mapFotos.containsKey( id ) && vo.getFoto() != null ) 	mapFotos.put( id,  vo.getFoto() );
				lstElementos.get( position ).setId(id );
				telefonoBuscado.put( telefono, id);
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
        
        holder.lblNombre.setText( lstElementos.get( position ).getNombre() );
        holder.lblTelefono.setText( telefono );
        holder.lblFecha.setText( dateFormat.format( lstElementos.get( position ).getFecha() ) );
        holder.lblHora.setText( timeFormat.format( lstElementos.get( position ).getFecha() ) );
        holder.lblDuracion.setText( DateUtil.getTimeString( lstElementos.get( position ).getDuracion(), true ) );
        
        if ( mapFotos.containsKey( lstElementos.get( position ).getId() ) ) {
        	holder.imgFoto.setImageBitmap( mapFotos.get( lstElementos.get( position ).getId() ) );
        } else {
        	holder.imgFoto.setImageResource( R.drawable.icon );
        }
        holder.imgTipo.setImageResource(   tipo == Calls.INCOMING_TYPE ? android.R.drawable.sym_call_incoming : 
        							tipo == Calls.OUTGOING_TYPE ? android.R.drawable.sym_call_outgoing :
        							tipo == Calls.MISSED_TYPE ? android.R.drawable.sym_call_missed : android.R.drawable.presence_busy );        
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
		TextView lblFecha;
		TextView lblHora;
		ImageView imgFoto;
		ImageView imgTipo;
	}

}
