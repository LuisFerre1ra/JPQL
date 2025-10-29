
package org.example;

import funciones.FuncionApp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
            EntityManager em = emf.createEntityManager();

            generarEntidades(em);

            // Ejercicio 1: Listar todos los clientes
            System.out.println("\n=== Ejercicio 1: Listar todos los clientes ===");

            List<Cliente> clientes = em.createQuery(
                "SELECT c FROM Cliente c",
                Cliente.class
            ).getResultList();

            imprimirLista(clientes);

            // Ejercicio 2: Listar todas las facturas generadas en el último mes
            System.out.println("\n=== Ejercicio 2: Listar todas las facturas generadas en el último mes ===");

            List<Factura> facturas = em.createQuery(
                "SELECT f FROM Factura f WHERE f.fechaComprobante >= :fechaLimite",
                Factura.class
            ).setParameter("fechaLimite", LocalDate.now().minusMonths(1)).getResultList();

            imprimirLista(facturas);

            // Ejercicio 3: Obtener el cliente que ha generado más facturas
            System.out.println("\n=== Ejercicio 3: Obtener el cliente que ha generado más facturas ===");

            Cliente clienteMasFacturas = em.createQuery(
                "SELECT f.cliente FROM Factura f GROUP BY f.cliente ORDER BY COUNT(f) DESC",
                Cliente.class
            ).getSingleResult();

            System.out.println(clienteMasFacturas);

            // Ejercicio 4: Listar los artículos más vendidos
            System.out.println("\n=== Ejercicio 4: Listar los artículos más vendidos ===");

            List<Articulo> articulos = em.createQuery(
                    "SELECT fd.articulo FROM FacturaDetalle fd GROUP BY fd.articulo ORDER BY SUM(fd.cantidad) DESC",
                    Articulo.class
            ).getResultList();

            imprimirLista(articulos);

            // Ejercicio 5: Consultar las facturas emitidas en los 3 últimos meses de un cliente específico
            System.out.println("\n=== Ejercicio 5: Consultar las facturas emitidas en los 3 últimos meses de un cliente específico ===");

            List<Factura> facturasCliente = em.createQuery(
                "SELECT f FROM Factura f WHERE f.cliente.id = :idCliente AND f.fechaComprobante >= :fechaLimite",
                    Factura.class
            ).setParameter("idCliente", clienteMasFacturas.getId())
             .setParameter("fechaLimite", LocalDate.now().minusMonths(3)).getResultList();

            imprimirLista(facturasCliente);

            // Ejercicio 6: Calcular el monto total facturado por un cliente
            System.out.println("\n=== Ejercicio 6: Calcular el monto total facturado por un cliente ===");

            Double totalFacturado = em.createQuery(
                "SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :idCliente",
                Double.class
            ).setParameter("idCliente", clienteMasFacturas.getId()).getSingleResult();

            System.out.println(totalFacturado);

            // Ejercicio 7: Listar los Artículos vendidos en una factura
            System.out.println("\n=== Ejercicio 7: Listar los Artículos vendidos en una factura ===");

            List<Articulo> articulosFactura = em.createQuery(
                "SELECT a FROM FacturaDetalle fd JOIN fd.articulo a WHERE fd.factura.id = :idFactura",
                Articulo.class
            ).setParameter("idFactura", facturasCliente.getFirst().getId()).getResultList();

            imprimirLista(articulosFactura);

            // Ejercicio 8: Obtener el Artículo más caro vendido en una factura
            System.out.println("\n=== Ejercicio 8: Obtener el Artículo más caro vendido en una factura ===");

            Articulo articuloMasVendido = em.createQuery(
                "SELECT fd.articulo FROM FacturaDetalle fd WHERE fd.factura.id = :idFactura ORDER BY fd.articulo.precioVenta DESC",
                Articulo.class
            ).setParameter("idFactura", facturasCliente.getFirst().getId()).setMaxResults(1).getSingleResult();

            System.out.println(articuloMasVendido);

            // Ejercicio 9: Contar la cantidad total de facturas generadas en el sistema
            System.out.println("\n=== Ejercicio 9: Contar la cantidad total de facturas generadas en el sistema ===");

            Long cantidadFacturas = em.createQuery(
                "SELECT COUNT(f) FROM Factura f",
                Long.class
            ).getSingleResult();

            System.out.println(cantidadFacturas);

            // Ejercicio 10: Listar las facturas cuyo total es mayor a un valor determinado
            System.out.println("\n=== Ejercicio 10: Listar las facturas cuyo total es mayor a un valor determinado ===");

            List<Factura> facturasConTotal = em.createQuery(
                "SELECT f FROM Factura f WHERE f.total > :valorMinimo",
                Factura.class
            ).setParameter("valorMinimo", 400d).getResultList();

            imprimirLista(facturasConTotal);

            // Ejercicio 11: Consultar las facturas que contienen un Artículo específico, filtrando por el nombre del artículo
            System.out.println("\n=== Ejercicio 11: Consultar las facturas que contienen un Artículo específico, filtrando por el nombre del artículo ===");

            List<Factura> facturasConArticulo = em.createQuery(
                "SELECT fd.factura FROM FacturaDetalle fd WHERE fd.articulo.denominacion = :nomArticulo",
                Factura.class
            ).setParameter("nomArticulo", "Pera").getResultList();

            imprimirLista(facturasConArticulo);

            // Ejercicio 12: Listar los Artículos filtrando por código parcial
            System.out.println("\n=== Ejercicio 12: Listar los Artículos filtrando por código parcial ===");

            List<Articulo> articulosConCodigo = em.createQuery(
                "SELECT a FROM Articulo a WHERE a.codigo LIKE :cod",
                Articulo.class
            ).setParameter("cod", "176079").getResultList();

            imprimirLista(articulosConCodigo);

            // Ejercicio 13: Listar todos los Artículos cuyo precio sea mayor que el promedio de los precios de todos los Artículos
            System.out.println("\n=== Ejercicio 13: Listar todos los Artículos cuyo precio sea mayor que el promedio de los precios de todos los Artículos ===");

            List<Articulo> articulosMasPromedio = em.createQuery(
                "SELECT a FROM Articulo a WHERE a.precioVenta > (SELECT AVG(a2.precioVenta) FROM Articulo a2)",
                Articulo.class
            ).getResultList();

            imprimirLista(articulosMasPromedio);

            // Ejercicio 14: Explique y ejemplifique la cláusula EXISTS
            /* La cláusula EXISTS se utiliza para comprobar si una subconsulta devuelve al menos una fila,
               devolviendo TRUE si existen resultados y FALSE en caso contrario. */
            System.out.println("\n=== Ejercicio 14: Buscar los artículos que hayan sido vendidos al menos una vez ===");

            List<Articulo> articulosVendidos = em.createQuery(
                "SELECT a FROM Articulo a WHERE EXISTS (SELECT 1 FROM FacturaDetalle fd WHERE fd.articulo = a)",
                Articulo.class
            ).getResultList();

            imprimirLista(articulosVendidos);

            em.close();
            emf.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static <T> void imprimirLista(List<T> lista) {
        if (lista.isEmpty()) {
            System.out.println("No se encontraron coincidencias");
        } else {
            for (int i = 0; i < lista.size(); i++) {
                System.out.println(i+1 + ": " + lista.get(i));
            }
        }
    }

    private static void generarEntidades(EntityManager em) {
        // Persistir la entidad UnidadMedida en estado "gestionada"
        em.getTransaction().begin();
        // Crear una nueva entidad UnidadMedida en estado "nueva"
        UnidadMedida unidadMedida = UnidadMedida.builder()
                .denominacion("Kilogramo")
                .build();
        UnidadMedida unidadMedidapote = UnidadMedida.builder()
                .denominacion("pote")
                .build();

        em.persist(unidadMedida);
        em.persist(unidadMedidapote);


        // Crear una nueva entidad Categoria en estado "nueva"
        Categoria categoria = Categoria.builder()
                .denominacion("Frutas")
                .esInsumo(true)
                .build();

        // Crear una nueva entidad Categoria en estado "nueva"
        Categoria categoriaPostre = Categoria.builder()
                .denominacion("Postre")
                .esInsumo(false)
                .build();

        // Persistir la entidad Categoria en estado "gestionada"

        em.persist(categoria);
        em.persist(categoriaPostre);


        // Crear una nueva entidad ArticuloInsumo en estado "nueva"
        ArticuloInsumo articuloInsumo = ArticuloInsumo.builder()
                .denominacion("Manzana").codigo(Long.toString(new Date().getTime()))
                .precioCompra(1.5)
                .precioVenta(5d)
                .stockActual(100)
                .stockMaximo(200)
                .esParaElaborar(true)
                .unidadMedida(unidadMedida)
                .build();


        ArticuloInsumo articuloInsumoPera = ArticuloInsumo.builder()
                .denominacion("Pera").codigo(Long.toString(new Date().getTime() + 1))
                .precioCompra(2.5)
                .precioVenta(10d)
                .stockActual(130)
                .stockMaximo(200)
                .esParaElaborar(true)
                .unidadMedida(unidadMedida)
                .build();

        // Persistir la entidad ArticuloInsumo en estado "gestionada"

        em.persist(articuloInsumo);
        em.persist(articuloInsumoPera);

        Imagen manza1 = Imagen.builder().denominacion("Manzana Verde").
                build();
        Imagen manza2 = Imagen.builder().denominacion("Manzana Roja").
                build();

        Imagen pera1 = Imagen.builder().denominacion("Pera Verde").
                build();
        Imagen pera2 = Imagen.builder().denominacion("Pera Roja").
                build();




        // Agregar el ArticuloInsumo a la Categoria
        categoria.getArticulos().add(articuloInsumo);
        categoria.getArticulos().add(articuloInsumoPera);
        // Actualizar la entidad Categoria en la base de datos

        // em.merge(categoria);

        // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"
        ArticuloManufacturadoDetalle detalleManzana = ArticuloManufacturadoDetalle.builder()
                .cantidad(2)
                .articuloInsumo(articuloInsumo)
                .build();


        ArticuloManufacturadoDetalle detallePera = ArticuloManufacturadoDetalle.builder()
                .cantidad(2)
                .articuloInsumo(articuloInsumoPera)
                .build();

        // Crear una nueva entidad ArticuloManufacturado en estado "nueva"
        ArticuloManufacturado articuloManufacturado = ArticuloManufacturado.builder()
                .denominacion("Ensalada de frutas")
                .descripcion("Ensalada de manzanas y peras ")
                .precioVenta(150d)
                .tiempoEstimadoMinutos(10)
                .preparacion("Cortar las frutas en trozos pequeños y mezclar")
                .unidadMedida(unidadMedidapote)
                .build();

        articuloManufacturado.getImagenes().add(manza1);
        articuloManufacturado.getImagenes().add(pera1);

        categoriaPostre.getArticulos().add(articuloManufacturado);
        // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"

        // Agregar el ArticuloManufacturadoDetalle al ArticuloManufacturado
        articuloManufacturado.getDetalles().add(detalleManzana);
        articuloManufacturado.getDetalles().add(detallePera);
        // Persistir la entidad ArticuloManufacturado en estado "gestionada"
        categoriaPostre.getArticulos().add(articuloManufacturado);
        em.persist(articuloManufacturado);
        em.getTransaction().commit();

        // modificar la foto de manzana roja
        em.getTransaction().begin();
        articuloManufacturado.getImagenes().add(manza2);
        em.merge(articuloManufacturado);
        em.getTransaction().commit();

        //creo y guardo un cliente
        em.getTransaction().begin();
        Cliente cliente = Cliente.builder()
                .cuit(FuncionApp.generateRandomCUIT())
                .razonSocial("Juan Perez")
                .build();
        em.persist(cliente);
        em.getTransaction().commit();

        //creo y guardo una factura
        em.getTransaction().begin();

        FacturaDetalle detalle1 = new FacturaDetalle(3, articuloInsumo);
        detalle1.calcularSubTotal();
        FacturaDetalle detalle2 = new FacturaDetalle(3, articuloInsumoPera);
        detalle2.calcularSubTotal();
        FacturaDetalle detalle3 = new FacturaDetalle(3, articuloManufacturado);
        detalle3.calcularSubTotal();

        Factura factura = Factura.builder()
                .puntoVenta(2024)
                .fechaAlta(new Date())
                .fechaComprobante(FuncionApp.generateRandomDate())
                .cliente(cliente)
                .nroComprobante(FuncionApp.generateRandomNumber())
                .build();
        factura.addDetalleFactura(detalle1);
        factura.addDetalleFactura(detalle2);
        factura.addDetalleFactura(detalle3);
        factura.calcularTotal();

        em.persist(factura);
        em.getTransaction().commit();
    }
}

/*

Manejo del Ciclo de Estados en JPA
El ciclo de estados en JPA (Java Persistence API) define los diferentes estados que puede tener una entidad en relación con el contexto de persistencia (EntityManager). Comprender y manejar correctamente estos estados es crucial para trabajar eficazmente con JPA. Los estados del ciclo de vida de una entidad en JPA son:

New (Nuevo):

Una entidad está en estado "New" cuando ha sido creada pero aún no ha sido persistida en la base de datos.
Managed (Gestionado):

Una entidad está en estado "Managed" cuando está asociada con un contexto de persistencia (EntityManager) y cualquier cambio en la entidad se reflejará automáticamente en la base de datos.
Detached (Desconectado):

Una entidad está en estado "Detached" cuando ya no está asociada con un contexto de persistencia. Los cambios en la entidad no se reflejarán automáticamente en la base de datos.
Removed (Eliminado):

Una entidad está en estado "Removed" cuando ha sido marcada para su eliminación en la base de datos.
*/


