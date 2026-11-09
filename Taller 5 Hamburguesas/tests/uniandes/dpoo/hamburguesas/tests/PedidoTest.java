package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest
{
    private static final String SEPARADOR = "----------------\n";

    @TempDir
    Path directorioTemporal;

    private Pedido pedido;

    private ProductoMenu corral;

    private ProductoMenu papas;

    private ProductoMenu gaseosa;

    private Combo combo;

    private ProductoAjustado corralAjustado;

    @BeforeEach
    void setUp( ) throws Exception
    {
        pedido = new Pedido( "Matias", "Calle 123 # 45-67" );

        corral = new ProductoMenu( "corral", 14000 );
        papas = new ProductoMenu( "papas medianas", 5500 );
        gaseosa = new ProductoMenu( "gaseosa", 5000 );

        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>( );
        items.add( corral );
        items.add( papas );
        items.add( gaseosa );
        combo = new Combo( "combo corral", 0.10, items ); // 22050

        corralAjustado = new ProductoAjustado( corral );
        corralAjustado.agregarIngrediente( new Ingrediente( "queso mozzarella", 2500 ) );
        corralAjustado.eliminarIngrediente( new Ingrediente( "tomate", 1000 ) ); // 16500
    }

    private String leerArchivo( File archivo ) throws IOException
    {
        return new String( Files.readAllBytes( archivo.toPath( ) ), Charset.defaultCharset( ) );
    }

    // ---------------------------------------------------------------
    // Identificador y datos del cliente
    // ---------------------------------------------------------------

    @Test
    void testGetIdPedidoSeIncrementa( )
    {
        Pedido segundo = new Pedido( "Ana", "Carrera 7" );
        Pedido tercero = new Pedido( "Luis", "Avenida 1" );

        assertEquals( pedido.getIdPedido( ) + 1, segundo.getIdPedido( ), "Cada pedido nuevo debe tener el siguiente identificador." );
        assertEquals( segundo.getIdPedido( ) + 1, tercero.getIdPedido( ), "Cada pedido nuevo debe tener el siguiente identificador." );
    }

    @Test
    void testGetIdPedidoNoCambiaAlCrearOtrosPedidos( )
    {
        int id = pedido.getIdPedido( );
        new Pedido( "Ana", "Carrera 7" );
        assertEquals( id, pedido.getIdPedido( ), "El identificador de un pedido existente no puede cambiar." );
    }

    @Test
    void testGetNombreCliente( )
    {
        assertEquals( "Matias", pedido.getNombreCliente( ), "El nombre del cliente no es el esperado." );
    }

    // ---------------------------------------------------------------
    // Precios
    // ---------------------------------------------------------------

    @Test
    void testPrecioPedidoVacio( )
    {
        assertEquals( 0, pedido.getPrecioTotalPedido( ), "Un pedido sin productos debe costar 0." );
    }

    @Test
    void testPrecioConUnProducto( )
    {
        pedido.agregarProducto( corral );
        // neto 14000 + IVA 2660
        assertEquals( 16660, pedido.getPrecioTotalPedido( ), "El total debe ser el neto más el 19% de IVA." );
    }

    @Test
    void testPrecioConVariosProductos( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );
        // neto 19500 + IVA 3705
        assertEquals( 23205, pedido.getPrecioTotalPedido( ), "El total debe sumar todos los productos más el IVA." );
    }

    @Test
    void testPrecioConProductoRepetido( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( corral );
        // neto 28000 + IVA 5320
        assertEquals( 33320, pedido.getPrecioTotalPedido( ), "Un producto agregado dos veces se debe cobrar dos veces." );
    }

    @Test
    void testPrecioConComboYProductoAjustado( )
    {
        pedido.agregarProducto( combo );
        pedido.agregarProducto( corralAjustado );
        // neto 22050 + 16500 = 38550; IVA = 7324 (se trunca 7324.5); total 45874
        assertEquals( 45874, pedido.getPrecioTotalPedido( ), "El pedido debe aceptar cualquier tipo de producto." );
    }

    @Test
    void testIvaSeTruncaAEntero( )
    {
        pedido.agregarProducto( new ProductoMenu( "producto raro", 1005 ) );
        // IVA = 190.95 -> 190; total = 1195
        assertEquals( 1195, pedido.getPrecioTotalPedido( ), "El IVA debe ser un entero truncado." );
        assertTrue( pedido.generarTextoFactura( ).contains( "IVA:          190\n" ), "El IVA de la factura debe ser 190." );
    }

    // ---------------------------------------------------------------
    // generarTextoFactura
    // ---------------------------------------------------------------

    @Test
    void testGenerarTextoFacturaEncabezado( )
    {
        String[] lineas = pedido.generarTextoFactura( ).split( "\n" );

        assertEquals( "Cliente: Matias", lineas[ 0 ], "La primera línea debe tener el nombre del cliente." );
        assertTrue( lineas[ 1 ].startsWith( "Direcci" ) && lineas[ 1 ].endsWith( ": Calle 123 # 45-67" ), "La segunda línea debe tener la dirección: " + lineas[ 1 ] );
        assertEquals( "----------------", lineas[ 2 ], "Debe haber un separador después del encabezado." );
    }

    @Test
    void testGenerarTextoFacturaPedidoVacio( )
    {
        String texto = pedido.generarTextoFactura( );

        assertTrue( texto.contains( "Calle 123 # 45-67" ), "La factura debe incluir la dirección." );
        assertTrue( texto.endsWith( SEPARADOR + "Precio Neto:  0\n" + "IVA:          0\n" + "Precio Total: 0\n" ), "Los totales de un pedido vacío deben ser 0. Factura:\n" + texto );
    }

    @Test
    void testGenerarTextoFacturaConProductosDelMenu( )
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( papas );

        String texto = pedido.generarTextoFactura( );

        String productos = corral.generarTextoFactura( ) + papas.generarTextoFactura( );
        String cola = SEPARADOR + productos + SEPARADOR + "Precio Neto:  19500\n" + "IVA:          3705\n" + "Precio Total: 23205\n";

        assertTrue( texto.startsWith( "Cliente: Matias\n" ), "La factura debe empezar con el cliente." );
        assertTrue( texto.endsWith( cola ), "Los productos y los totales no son los esperados. Factura:\n" + texto );
    }

    @Test
    void testGenerarTextoFacturaConComboYProductoAjustado( )
    {
        pedido.agregarProducto( combo );
        pedido.agregarProducto( corralAjustado );

        String texto = pedido.generarTextoFactura( );

        String productos = combo.generarTextoFactura( ) + corralAjustado.generarTextoFactura( );
        String cola = SEPARADOR + productos + SEPARADOR + "Precio Neto:  38550\n" + "IVA:          7324\n" + "Precio Total: 45874\n";

        assertTrue( texto.endsWith( cola ), "La factura debe incluir el texto de cada producto y los totales. Factura:\n" + texto );
    }

    @Test
    void testGenerarTextoFacturaMantieneElOrdenDeLosProductos( )
    {
        pedido.agregarProducto( papas );
        pedido.agregarProducto( corral );

        String texto = pedido.generarTextoFactura( );

        assertTrue( texto.indexOf( "papas medianas" ) < texto.indexOf( "corral" ), "Los productos deben aparecer en el orden en que se agregaron." );
    }

    // ---------------------------------------------------------------
    // guardarFactura
    // ---------------------------------------------------------------

    @Test
    void testGuardarFactura( ) throws IOException
    {
        pedido.agregarProducto( corral );
        pedido.agregarProducto( combo );
        pedido.agregarProducto( corralAjustado );

        File archivo = new File( directorioTemporal.toFile( ), "factura.txt" );
        pedido.guardarFactura( archivo );

        assertTrue( archivo.exists( ), "El archivo de la factura debe existir." );
        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivo ), "El archivo debe contener exactamente el texto de la factura." );
    }

    @Test
    void testGuardarFacturaPedidoVacio( ) throws IOException
    {
        File archivo = new File( directorioTemporal.toFile( ), "vacio.txt" );
        pedido.guardarFactura( archivo );

        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivo ) );
    }

    @Test
    void testGuardarFacturaSobrescribeElArchivo( ) throws IOException
    {
        File archivo = new File( directorioTemporal.toFile( ), "factura.txt" );

        pedido.agregarProducto( corral );
        pedido.guardarFactura( archivo );

        pedido.agregarProducto( papas );
        pedido.guardarFactura( archivo );

        assertEquals( pedido.generarTextoFactura( ), leerArchivo( archivo ), "El archivo debe tener la última versión de la factura." );
    }

    @Test
    void testGuardarFacturaNoModificaElPedido( ) throws IOException
    {
        pedido.agregarProducto( corral );
        int totalAntes = pedido.getPrecioTotalPedido( );
        String textoAntes = pedido.generarTextoFactura( );

        pedido.guardarFactura( new File( directorioTemporal.toFile( ), "factura.txt" ) );

        assertEquals( totalAntes, pedido.getPrecioTotalPedido( ) );
        assertEquals( textoAntes, pedido.generarTextoFactura( ) );
    }

    @Test
    void testGuardarFacturaEnCarpetaInexistenteLanzaExcepcion( )
    {
        File archivo = new File( directorioTemporal.toFile( ), "carpeta_que_no_existe/factura.txt" );

        assertThrows( FileNotFoundException.class, ( ) -> pedido.guardarFactura( archivo ), "Si no se puede crear el archivo se debe lanzar FileNotFoundException." );
    }

    @Test
    void testGuardarFacturaEnUnDirectorioLanzaExcepcion( )
    {
        File directorio = directorioTemporal.toFile( );

        assertThrows( FileNotFoundException.class, ( ) -> pedido.guardarFactura( directorio ), "No se puede guardar una factura sobre un directorio." );
    }
}
