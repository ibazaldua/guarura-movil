/**
 * 
 */
package mx.bazaldua.guaruramovil.free.view.helper;

import java.text.NumberFormat;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.Util;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.FrecuenteVO;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class FrecuenteHelper {

	/**
	 * Devuelve el objeto <code>FrecuenteVO</code> de acuerdo a los valores de los parametreos enviados
	 * @param view		Objeto contenedor de la vista
	 * @return				Objeto <code>FrecuenteVO</code>
	 */
	public static FrecuenteVO getFrecuenteVO ( View view ) {
		FrecuenteVO vo       = new FrecuenteVO();
		TextView lblTelefono = (TextView) view.findViewById( R.id.lblTelefono );
		CheckBox chkRemarcar = (CheckBox) view.findViewById( R.id.chkRemarcar );
		TextView txtMinutos  = (TextView) view.findViewById( R.id.txtMinutos );
		TextView txtSegundos = (TextView) view.findViewById( R.id.txtSegundos );
		
		vo.setTelefono( lblTelefono.getText().toString() );
		vo.setRemarcar( chkRemarcar.isChecked() );
		vo.setMinutos( Integer.valueOf( Util.isNullOrEmpty( txtMinutos.getText() ) ? "0" : txtMinutos.getText().toString() ) );
		vo.setSegundos( Integer.valueOf( Util.isNullOrEmpty( txtSegundos.getText() ) ? "0" : txtSegundos.getText().toString() ) );
		
		return vo;
	}

	/**
	 * Devuelve un objeto <code>FrecuenteVO</code> a partir de un objeto <code>ContactVO</code>
	 * @param contactVO	Objeto contenedor de los valores del contacto	
	 * @return	<code>FrecuenteVO</code> utilizado para el modulo de numeros frecuentes
	 */
	public static FrecuenteVO getFrecuenteVOFromContactVO(ContactVO contactVO) {
		FrecuenteVO vo = null;
		if ( contactVO != null ) {
			vo = new FrecuenteVO();
			vo.setId( contactVO.getId() );
			vo.setNombre( contactVO.getNombre() );
			vo.setTelefono( contactVO.getTelefono() );
			vo.setTipoTelefono( contactVO.getTipoTelefono() );
			vo.setDescTipoTelPersonalizado(contactVO.getDescTipoTelPersonalizado() );
			vo.setFoto( contactVO.getFoto() );
		}
		return vo;
	}

	/**
	 * Establece los datos de la vista derivado de los datos de <code>FrecuenteVO</code>
	 * @param frecuenteVO	Objeto contenedor de los datos
	 * @param view			Vista en donde se pondra la informacion.
	 * @param context		Contexto de la aplicacion
	 */
	public static void setDatosView(FrecuenteVO frecuenteVO, View view, Context context) {
		TextView lblNombre     = (TextView) view.findViewById(R.id.lblNombre); 
		TextView lblTelefono   = (TextView) view.findViewById(R.id.lblTelefono);
		TextView lblTipo       = (TextView) view.findViewById(R.id.lblTipo);
		ImageView imgFoto      = (ImageView) view.findViewById(R.id.imgFoto);
		CheckBox chkRemarcar   = (CheckBox) view.findViewById(R.id.chkRemarcar);
		TextView txtMinutos    = (TextView) view.findViewById(R.id.txtMinutos);
		TextView txtSegundos   = (TextView) view.findViewById(R.id.txtSegundos);
		NumberFormat format    = NumberFormat.getInstance();
		TextWatcher watcher    = null;
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean remarcar       = pref.getBoolean( Configuracion.REMARCAR, Constants.CONF_DEFAULT_REMARCAR );
		String minutos         = pref.getString(Configuracion.MINUTOS, "00");
		String segundos        = pref.getString(Configuracion.SEGUNDOS, "00");
		
		format.setMinimumIntegerDigits(2);
		
		lblNombre.setText( frecuenteVO.getNombre() );
		lblTelefono.setText( frecuenteVO.getTelefono() );
		lblTipo.setText( ContactUtils.getPhoneType(frecuenteVO.getTipoTelefono(), frecuenteVO.getDescTipoTelPersonalizado(), context) );
		if ( frecuenteVO.getFoto() != null ) imgFoto.setImageBitmap( frecuenteVO.getFoto() );
		chkRemarcar.setChecked( frecuenteVO.getRemarcar() == null ? remarcar : frecuenteVO.getRemarcar() );
		txtMinutos.setText( frecuenteVO.getMinutos() == null ? format.format( Integer.valueOf( minutos )) : format.format( frecuenteVO.getMinutos() ) );
		txtSegundos.setText( frecuenteVO.getSegundos() == null ? format.format( Integer.valueOf( segundos ) ) : format.format( frecuenteVO.getSegundos() ) );
		
		//Validaciones
		watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if ( arg0.length() > 0 && Integer.valueOf(arg0.toString()) > 59 ) arg0.delete(arg0.length() - 1, arg0.length()); 
			}
		};
		txtMinutos.addTextChangedListener( watcher );
		txtSegundos.addTextChangedListener( watcher );
	}

}
