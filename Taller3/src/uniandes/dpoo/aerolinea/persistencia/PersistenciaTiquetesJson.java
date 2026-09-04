package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.ClienteRepetidoException;
import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteTiqueteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteNatural;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class PersistenciaTiquetesJson implements IPersistenciaTiquetes
{

    private static final String NOMBRE_CLIENTE = "nombre";
    private static final String TIPO_CLIENTE = "tipoCliente";
    private static final String CLIENTE = "cliente";
    private static final String USADO = "usado";
    private static final String TARIFA = "tarifa";
    private static final String CODIGO_TIQUETE = "codigoTiquete";
    private static final String FECHA = "fecha";
    private static final String CODIGO_RUTA = "codigoRuta";

    /**
     * Carga la información de los clientes y tiquetes vendidos por la aerolínea, y actualiza la estructura de objetos que se encuentra dentro de la aerolínea
     * @param archivo La ruta al archivo que contiene la información que se va a cargar
     * @param aerolinea La aerolínea dentro de la cual debe almacenarse la información
     * @throws IOException Se lanza esta excepción si hay problemas leyendo el archivo
     * @throws InformacionInconsistenteException Se lanza esta excepción si hay información inconsistente dentro del archivo, o entre el archivo y el estado de la aerolínea
     */
    @Override
    public void cargarTiquetes( String archivo, Aerolinea aerolinea ) throws IOException, InformacionInconsistenteException
    {
        String jsonCompleto = new String( Files.readAllBytes( new File( archivo ).toPath( ) ) );
        JSONObject raiz = new JSONObject( jsonCompleto );

        cargarClientes( aerolinea, raiz.getJSONArray( "clientes" ) );
        cargarTiquetes( aerolinea, raiz.getJSONArray( "tiquetes" ) );
    }

    /**
     * Salva en un archivo toda la información sobre los clientes y los tiquetes vendidos por la aerolínea
     * @param archivo La ruta al archivo donde debe quedar almacenada la información
     * @param aerolinea La aerolínea que tiene la información que se quiere almacenar
     * @throws IOException Se lanza esta excepción si hay problemas escribiendo el archivo
     */
    @Override
    public void salvarTiquetes( String archivo, Aerolinea aerolinea ) throws IOException
    {
        JSONObject jobject = new JSONObject( );

        // Salvar clientes
        salvarClientes( aerolinea, jobject );

        // Salvar tiquetes
        salvarTiquetes( aerolinea, jobject );

        // Escribir la estructura JSON en un archivo
        PrintWriter pw = new PrintWriter( archivo );
        jobject.write( pw, 2, 0 );
        pw.close( );
    }

    /**
     * Carga los clientes de la aerolínea a partir de un archivo JSON
     * @param aerolinea La aerolínea donde deben quedar los clientes
     * @param jClientes El elemento JSON donde está la información de los clientes
     * @throws ClienteRepetidoException Lanza esta excepción si alguno de los clientes que se van a cargar tiene el mismo identificador que otro cliente
     */
    private void cargarClientes( Aerolinea aerolinea, JSONArray jClientes ) throws ClienteRepetidoException
    {
        int numClientes = jClientes.length( );
        for( int i = 0; i < numClientes; i++ )
        {
            JSONObject cliente = jClientes.getJSONObject( i );
            String tipoCliente = cliente.getString( TIPO_CLIENTE );
            Cliente nuevoCliente = null;
            // En las siguientes líneas se utilizan dos estrategias para implementar la carga de objetos: en la primera estrategia, la carga de los objetos
            // lo hace alguien externo al objeto que se carga; en la segunda estrategia, los objetos saben cargarse.
            // En general es una mala idea mezclar las dos estrategias: acá lo hacemos para ilustrar las dos posibilidades y mostrar las ventajas y desventajas de cada una.
            // Lo que sí es recomendable es seleccionar una estrategia y usarla consistentemente para cargar y salvar.
            if( ClienteNatural.NATURAL.equals( tipoCliente ) )
            {
                // 1. En esta estrategia, en ESTA clase se realiza todo lo que tiene que ver con cargar objetos de la clase ClienteNatural
                // Al revisar el código de la clase ClienteNatural, no hay nada que tenga que ver con cargar o salvar.
                // En este caso, la persistencia es una preocupación transversal de la que no se ocupa la clase ClienteNatural
                String nombre = cliente.getString( NOMBRE_CLIENTE );
                nuevoCliente = new ClienteNatural( nombre );
            }
            else
            {
                // 2. En esta estrategia, en la clase ClienteCorporativo se realiza una parte de lo que tiene que ver con cargar objetos de la clase ClienteCorporativo.
                // La clase ClienteCorporativo tiene un método para cargar y otro para salvar.
                // En este caso, la persistencia es una preocupación de la cual se ocupa la clase ClienteCorporativo
                nuevoCliente = ClienteCorporativo.cargarDesdeJSON( cliente );
            }
            if( !aerolinea.existeCliente( nuevoCliente.getIdentificador( ) ) )
                aerolinea.agregarCliente( nuevoCliente );
            else
                throw new ClienteRepetidoException( nuevoCliente.getTipoCliente( ), nuevoCliente.getIdentificador( ) );
        }
    }

    /**
     * Salva la información de los clientes de la aerolínea dentro del objeto json que se recibe por parámetro.
     * 
     * La información de los clientes queda dentro de la llave 'clientes'
     * @param aerolinea La aerolínea que tiene la información
     * @param jobject El objeto JSON donde debe quedar la información de los clientes
     */
    private void salvarClientes( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jClientes = new JSONArray( );
        for( Cliente cliente : aerolinea.getClientes( ) )
        {
            // Acá también se utilizaron dos estrategias para salvar los clientes.
            // Para los clientes naturales, esta clase extrae la información de los objetos y la organiza para que luego sea salvada.
            // Para los clientes corporativos, la clase ClienteCorporativo hace todo lo que está en sus manos para persistir un cliente
            if( ClienteNatural.NATURAL.equals( cliente.getTipoCliente( ) ) )
            {
                JSONObject jCliente = new JSONObject( );
                jCliente.put( NOMBRE_CLIENTE, cliente.getIdentificador( ) );
                jCliente.put( TIPO_CLIENTE, ClienteNatural.NATURAL );
                jClientes.put( jCliente );
            }
            else
            {
                ClienteCorporativo cc = ( ClienteCorporativo )cliente;
                JSONObject jCliente = cc.salvarEnJSON( );
                jClientes.put( jCliente );
            }
        }

        jobject.put( "clientes", jClientes );
    }

    /**
     * Carga los tiquetes de la aerolínea a partir de un archivo JSON
     * @param aerolinea La aerolínea donde deben quedar los tiquetes
     * @param jTiquetes El elemento JSON donde está la información de los tiquetes
     * @throws InformacionInconsistenteTiqueteException Lanza esta excepción si la información de alguno de los tiquetes no es consistente con el resto de elementos de la
     *         aerolínea (ej. es un tiquete para un vuelo que no existe, o fue comprado por un cliente que no existe, etc.)
     */
    private void cargarTiquetes( Aerolinea aerolinea, JSONArray jTiquetes ) throws InformacionInconsistenteTiqueteException
    {
        int numTiquetes = jTiquetes.length( );
        for( int i = 0; i < numTiquetes; i++ )
        {
            JSONObject tiquete = jTiquetes.getJSONObject( i );

            // Extraer y validar la información del tiquete
            String codigoRuta = tiquete.getString( CODIGO_RUTA );
            Ruta laRuta = aerolinea.getRuta( codigoRuta );
            if( laRuta == null )
                throw new InformacionInconsistenteTiqueteException( "ruta", codigoRuta );

            String fechaVuelo = tiquete.getString( FECHA );
            Vuelo elVuelo = aerolinea.getVuelo( codigoRuta, fechaVuelo );
            if( elVuelo == null )
                throw new InformacionInconsistenteTiqueteException( "vuelo", codigoRuta + " en " + fechaVuelo );

            String codigoTiquete = tiquete.getString( CODIGO_TIQUETE );
            boolean existe = GeneradorTiquetes.validarTiquete( codigoTiquete );

            if( existe )
                throw new InformacionInconsistenteTiqueteException( "tiquete", codigoTiquete, false );

            int tarifa = tiquete.getInt( TARIFA );
            boolean tiqueteUsado = tiquete.getBoolean( USADO );

            String identificadorCliente = tiquete.getString( CLIENTE );
            Cliente elCliente = aerolinea.getCliente( identificadorCliente );
            if( elCliente == null )
                throw new InformacionInconsistenteTiqueteException( "cliente", identificadorCliente );

            // Construir y registrar el tiquete
            Tiquete nuevoTiquete = new Tiquete( codigoTiquete, elVuelo, elCliente, tarifa );
            if( tiqueteUsado )
                nuevoTiquete.marcarComoUsado( );
            GeneradorTiquetes.registrarTiquete( nuevoTiquete );
        }
    }

    /**
     * Salva la información de los tiquetes de la aerolínea dentro del objeto json que se recibe por parámetro.
     * 
     * La información de los tiquetes queda dentro de la llave 'tiquetes'
     * @param aerolinea La aerolínea que tiene la información
     * @param jobject El objeto JSON donde debe quedar la información de los tiquetes
     */
    private void salvarTiquetes( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jTiquetes = new JSONArray( );
        for( Tiquete tiquete : aerolinea.getTiquetes( ) )
        {
            JSONObject jTiquete = new JSONObject( );
            jTiquete.put( CODIGO_TIQUETE, tiquete.getCodigo( ) );
            jTiquete.put( CODIGO_RUTA, tiquete.getVuelo( ).getRuta( ).getCodigoRuta( ) );
            jTiquete.put( FECHA, tiquete.getVuelo( ).getFecha( ) );
            jTiquete.put( TARIFA, tiquete.getTarifa( ) );
            jTiquete.put( USADO, tiquete.esUsado( ) );
            jTiquete.put( CLIENTE, tiquete.getCliente( ).getIdentificador( ) );

            jTiquetes.put( jTiquete );
        }
        jobject.put( "tiquetes", jTiquetes );
    }

}
