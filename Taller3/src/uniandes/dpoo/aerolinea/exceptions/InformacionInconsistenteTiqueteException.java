package uniandes.dpoo.aerolinea.exceptions;

/**
 * Esta clase se usa para indicar que, al cargar un archivo, se encontraro inconsistencias bien sea dentro del archivo, o entre el archivo y el estado de los clientes y
 * tiquetes de la aerolínea.
 */
@SuppressWarnings("serial")
public class InformacionInconsistenteTiqueteException extends InformacionInconsistenteException
{
    public InformacionInconsistenteTiqueteException( String tipoElemento, String identificadorElemento )
    {
        this( tipoElemento, identificadorElemento, true );
    }

    /**
     * Inicializa la excepción construyendo un mensaje que avisa sobre la ausencia de un elemento que se esperaba, o sobre la existencia de un elemento que no debería haber
     * existido.
     * 
     * Dependiendo del valor del parámetro 'sentido', la excepción indica cuál de los dos problemas se presentó.
     * @param tipoElemento El tipo de elemento sobre el que se presentó el problema
     * @param identificadorElemento El identificador del elemento que se encontró cuando no se requería, o que no se encontró cuando debía existir
     * @param sentido
     */
    public InformacionInconsistenteTiqueteException( String tipoElemento, String identificadorElemento, boolean sentido )
    {
        super( "Dentro del conjunto de " + tipoElemento + ( sentido ? " NO" : " YA" ) + " existe un objeto con el identificador " + identificadorElemento );
    }
}
