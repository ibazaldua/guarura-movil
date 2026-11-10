/**
 * 
 */
package mx.bazaldua.guaruramovil.free.view.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.ListaNegraVO;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class ListaNegraHelper {

	public static ListaNegraVO getListaNegraVO() {
		return null;
	}

	/**
	 * 
	 * @param vo
	 * @param tipoContacto
	 * @return
	 */
	public static ListaNegraVO getListaNegraVOFromContactVO(ContactVO vo, int tipoContacto) {
		ListaNegraVO listaNegraVO = null;
		if ( vo != null ) {
			listaNegraVO = new ListaNegraVO();

			listaNegraVO.setId( vo.getId() );
			listaNegraVO.setTelefono( vo.getTelefono() );
			listaNegraVO.setTipoTelefono( vo.getTipoTelefono() );
			listaNegraVO.setTipoContacto( tipoContacto );
		}
		return listaNegraVO;
	}

	/**
	 * 
	 * @param lstElementos
	 * @param activity 
	 * @return
	 */
	public static Map<Integer, ?> getMapConsulta(List<ListaNegraVO> lstElementos, Activity activity) {
		Map<Integer, Object> mapResult = new HashMap<Integer, Object>();
		
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

        Long idTemp = -10L;
        Map<String, String> curGroupMap    = null;
        List<Map<String, String>> children = null;
        Map<String, String> curChildMap    = null;
        for (ListaNegraVO elemento : lstElementos) {
			if ( idTemp.longValue() != elemento.getId().longValue() ) {
				curGroupMap = new HashMap<String, String>();
				children    = new ArrayList<Map<String, String>>();
				idTemp      = elemento.getId();
	        	curGroupMap.put(Constants.EXTRA_PARAM_NOMBRE, elemento.getNombre() );
	        	curGroupMap.put(Constants.EXTRA_PARAM_ID, elemento.getId().toString() );
	        	groupData.add(curGroupMap);
	        	childData.add(children);
			}
			
			curChildMap = new HashMap<String, String>();
			curChildMap.put( Constants.EXTRA_PARAM_TELEFONO , elemento.getTelefono() );
			curChildMap.put( Constants.EXTRA_PARAM_TIPO_TELEFONO, ContactUtils.getPhoneType( elemento.getTipoTelefono(), elemento.getDescTipoTelPersonalizado(), activity)  );
            children.add(curChildMap);
		}
        if ( childData != null ) childData.add(children);
        mapResult.put(0, groupData);
        mapResult.put(1, childData);

		return mapResult;
	}

	/**
	 * Devuelve un objeto <code>ListaNegraVO</code>
	 * @param view	Objeto contenedor del numero capturado
	 * @param contentResolver 
	 * @return		<code>ListaNegraVO</code> contenedor de los datos para agregar
	 */
	public static ListaNegraVO getListaNegraVOFromNumber(View view, ContentResolver contentResolver) {
		ListaNegraVO vo = new ListaNegraVO();
		ContactVO contactVO = null;
		
		
		vo.setId( Long.valueOf( Constants.CONTACTO_DECONOCIDO ) );
		vo.setTelefono( ((TextView)view.findViewById(R.id.txtTelefono)).getText().toString() );
		vo.setTipoContacto( Constants.CONTACTO_DECONOCIDO );
		
		contactVO = ContactUtils.getContactByPhone(vo.getTelefono(), contentResolver);
		if ( contactVO != null ) vo.setId( contactVO.getId() );
//		vo.setTipoTelefono( Phone.TYPE_OTHER );
		
		return vo;
	}
	
	/**
	 * Devuelve un objeto <code>ListaNegraVO</code>
	 * @param view	Objeto contenedor del numero capturado
	 * @param contentResolver 
	 * @return		<code>ListaNegraVO</code> contenedor de los datos para agregar
	 */
	public static ListaNegraVO getListaNegraVOFromNumber(Intent intent, ContentResolver resolver) {
		ListaNegraVO vo       = null;
		ContactVO contactVO   = null;
		CharSequence telefono = intent.getCharSequenceExtra( Constants.EXTRA_PARAM_TELEFONO );
		
		if ( telefono != null  ) {
			vo = new ListaNegraVO();
			
			vo.setId( Long.valueOf( Constants.CONTACTO_DECONOCIDO ) );
			vo.setTelefono( telefono.toString() );
			vo.setTipoContacto( Constants.CONTACTO_DECONOCIDO );
			
			contactVO = ContactUtils.getContactByPhone(vo.getTelefono(), resolver);
			if ( contactVO != null ) vo.setId( contactVO.getId() );
//			vo.setTipoTelefono( Phone.TYPE_OTHER );
		}
		

		
		return vo;
	}

}
