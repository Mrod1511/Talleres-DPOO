package uniandes.dpoo.aerolinea.tiquetes;

import java.util.HashSet;
import java.util.Set;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

/**
 * Esta clase representa al módulo del sistema que es capaz de generar nuevos tiquetes, asignándole a cada uno un código único.
 */
public class GeneradorTiquetes
{
    /**
     * Un conjunto con los códigos que ya han sido usados anteriormente para otros tiquetes.
     * 
     * Este conjunto se utiliza para no correr el riesgo de repetir un código.
     */
    private static Set<String> codigos = new HashSet<String>( );

    /**
     * Construye un nuevo tiquete con los datos dados y con un identificador que corresponde a una cadena con 7 dígitos
     * @param vuelo El vuelo al que está asociado el tiquete
     * @param cliente El ciente que compró el tiquete
     * @param tarifa El valor que se le cobró al cliente por el tiquete
     * @return El nuevo tiquete, inicializado con un código único
     */
    public static Tiquete generarTiquete( Vuelo vuelo, Cliente cliente, int tarifa )
    {
        int numero = ( int ) ( Math.random( ) * 10e7 );
        String codigo = "" + numero;
        while( codigos.contains( codigo ) )
        {
            numero = ( int ) ( Math.random( ) * 10e7 );
            codigo = "" + numero;
        }

        while( codigo.length( ) < 7 )
            codigo = "0" + codigo;

        return new Tiquete( codigo, vuelo, cliente, tarifa );
    }

    /**
     * Registra que un cierto tiquete ya fue vendido, para que el generador de tiquetes no vaya a generar otro tiquete con el mismo código
     * @param unTiquete El tiquete existente
     */
    public static void registrarTiquete( Tiquete unTiquete )
    {
    	codigos.add( unTiquete.getCodigo( ) );
    }

    /**
     * Revisa si ya existe un tiquete con el código dado
     * @param codigoTiquete El código que se quiere consultar
     * @return Retorna true si ya se tenía registrado un tiquete con el código dado
     */
    public static boolean validarTiquete( String codigoTiquete )
    {
    	return codigos.contains( codigoTiquete );
    }
}
