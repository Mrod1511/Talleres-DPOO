package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest
{
    private ProductoMenu base;

    private Ingrediente queso;

    private Ingrediente huevo;

    private Ingrediente tomate;

    private Ingrediente cebolla;

    private ProductoAjustado ajustado;

    @BeforeEach
    void setUp( ) throws Exception
    {
        base = new ProductoMenu( "corral", 14000 );
        queso = new Ingrediente( "queso mozzarella", 2500 );
        huevo = new Ingrediente( "huevo", 2500 );
        tomate = new Ingrediente( "tomate", 1000 );
        cebolla = new Ingrediente( "cebolla", 1000 );
        ajustado = new ProductoAjustado( base );
    }

    // ---------------------------------------------------------------
    // getNombre
    // ---------------------------------------------------------------

    @Test
    void testGetNombre( )
    {
        assertEquals( "corral", ajustado.getNombre( ), "El nombre debe ser el del producto base." );
    }

    @Test
    void testGetNombreNoCambiaConAjustes( )
    {
        ajustado.agregarIngrediente( queso );
        ajustado.eliminarIngrediente( tomate );
        assertEquals( "corral", ajustado.getNombre( ), "Los ajustes no deben cambiar el nombre." );
    }

    // ---------------------------------------------------------------
    // getPrecio
    // ---------------------------------------------------------------

    @Test
    void testGetPrecioSinAjustes( )
    {
        assertEquals( 14000, ajustado.getPrecio( ), "Sin ajustes el precio debe ser el del producto base." );
    }

    @Test
    void testGetPrecioConUnAgregado( )
    {
        ajustado.agregarIngrediente( queso );
        assertEquals( 16500, ajustado.getPrecio( ), "El precio debe sumar el costo del ingrediente agregado." );
    }

    @Test
    void testGetPrecioConVariosAgregados( )
    {
        ajustado.agregarIngrediente( queso );
        ajustado.agregarIngrediente( huevo );
        assertEquals( 19000, ajustado.getPrecio( ), "El precio debe sumar el costo de todos los ingredientes agregados." );
    }

    @Test
    void testGetPrecioConAgregadoRepetido( )
    {
        ajustado.agregarIngrediente( huevo );
        ajustado.agregarIngrediente( huevo );
        assertEquals( 19000, ajustado.getPrecio( ), "Un ingrediente agregado dos veces se debe cobrar dos veces." );
    }

    @Test
    void testGetPrecioConEliminadosNoCambia( )
    {
        ajustado.eliminarIngrediente( tomate );
        ajustado.eliminarIngrediente( cebolla );
        assertEquals( 14000, ajustado.getPrecio( ), "Eliminar ingredientes no debe reducir el precio." );
    }

    @Test
    void testGetPrecioConAgregadosYEliminados( )
    {
        ajustado.agregarIngrediente( queso );
        ajustado.eliminarIngrediente( tomate );
        ajustado.eliminarIngrediente( cebolla );
        assertEquals( 16500, ajustado.getPrecio( ), "Solo los ingredientes agregados deben afectar el precio." );
    }

    @Test
    void testGetPrecioNoModificaElProductoBase( )
    {
        ajustado.agregarIngrediente( queso );
        ajustado.getPrecio( );
        assertEquals( 14000, base.getPrecio( ), "El producto base no debe cambiar de precio." );
    }

    // ---------------------------------------------------------------
    // generarTextoFactura
    // ---------------------------------------------------------------

    @Test
    void testGenerarTextoFacturaSinAjustes( )
    {
        String texto = ajustado.generarTextoFactura( );
        String[] lineas = texto.split( "\n" );

        assertFalse( texto.contains( "@" ), "El texto no debe contener la representación por defecto de un objeto." );
        assertEquals( 2, lineas.length, "La factura sin ajustes debe tener el nombre y el precio." );
        assertEquals( "corral", lineas[ 0 ].trim( ), "La primera línea debe ser el nombre del producto base." );
        assertEquals( "14000", lineas[ 1 ].trim( ), "La segunda línea debe ser el precio." );
        assertTrue( texto.endsWith( "\n" ), "El texto debe terminar en salto de línea." );
    }

    @Test
    void testGenerarTextoFacturaConAgregados( )
    {
        ajustado.agregarIngrediente( queso );
        ajustado.agregarIngrediente( huevo );

        String[] lineas = ajustado.generarTextoFactura( ).split( "\n" );

        assertEquals( 4, lineas.length, "Debe haber una línea por el producto, una por cada agregado y una por el precio." );
        assertEquals( "corral", lineas[ 0 ].trim( ) );
        assertTrue( lineas[ 1 ].trim( ).matches( "\\+queso mozzarella\\s+2500" ), "Línea del primer agregado incorrecta: " + lineas[ 1 ] );
        assertTrue( lineas[ 2 ].trim( ).matches( "\\+huevo\\s+2500" ), "Línea del segundo agregado incorrecta: " + lineas[ 2 ] );
        assertEquals( "19000", lineas[ 3 ].trim( ) );
    }

    @Test
    void testGenerarTextoFacturaConEliminados( )
    {
        ajustado.eliminarIngrediente( tomate );
        ajustado.eliminarIngrediente( cebolla );

        String[] lineas = ajustado.generarTextoFactura( ).split( "\n" );

        assertEquals( 4, lineas.length, "Debe haber una línea por el producto, una por cada eliminado y una por el precio." );
        assertEquals( "corral", lineas[ 0 ].trim( ) );
        assertEquals( "-tomate", lineas[ 1 ].trim( ) );
        assertEquals( "-cebolla", lineas[ 2 ].trim( ) );
        assertEquals( "14000", lineas[ 3 ].trim( ) );
    }

    @Test
    void testGenerarTextoFacturaConAgregadosYEliminados( )
    {
        ajustado.eliminarIngrediente( tomate );
        ajustado.agregarIngrediente( queso );

        String[] lineas = ajustado.generarTextoFactura( ).split( "\n" );

        assertEquals( 4, lineas.length );
        assertEquals( "corral", lineas[ 0 ].trim( ) );
        assertTrue( lineas[ 1 ].trim( ).matches( "\\+queso mozzarella\\s+2500" ), "Los agregados van antes que los eliminados: " + lineas[ 1 ] );
        assertEquals( "-tomate", lineas[ 2 ].trim( ) );
        assertEquals( "16500", lineas[ 3 ].trim( ) );
    }
}
