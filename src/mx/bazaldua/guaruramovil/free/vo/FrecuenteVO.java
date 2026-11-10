/**
 * 
 */
package mx.bazaldua.guaruramovil.free.vo;


/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class FrecuenteVO extends ContactVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1290878041259915131L;
	
	private Integer minutos;
	private Integer segundos;
	private Boolean remarcar;

	/**
	 * @return the remarcar
	 */
	public Boolean getRemarcar() {
		return remarcar;
	}
	/**
	 * @param remarcar the remarcar to set
	 */
	public void setRemarcar(Boolean remarcar) {
		this.remarcar = remarcar;
	}
	/**
	 * @return the minutos
	 */
	public Integer getMinutos() {
		return minutos;
	}
	/**
	 * @param minutos the minutos to set
	 */
	public void setMinutos(Integer minutos) {
		this.minutos = minutos;
	}
	/**
	 * @return the segundos
	 */
	public Integer getSegundos() {
		return segundos;
	}
	/**
	 * @param segundos the segundos to set
	 */
	public void setSegundos(Integer segundos) {
		this.segundos = segundos;
	}

}
