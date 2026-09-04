package uniandes.dpoo.aerolinea.exceptions;

import uniandes.dpoo.aerolinea.modelo.Vuelo;

/**
 * Esta clase se usa para anunciar que se intentó vender un tiquete para un vuelo que ya está lleno
 */
@SuppressWarnings("serial")
public class VueloSobrevendidoException extends Exception
{

    public VueloSobrevendidoException( Vuelo vuelo )
    {
        super( "El vuelo " + vuelo.getRuta( ).getCodigoRuta( ) + " del " + vuelo.getFecha( ) + " no tiene cupo" );
    }

}
