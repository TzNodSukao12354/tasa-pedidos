package pe.tasa.principal;

import pe.tasa.dao.*;
import pe.tasa.modelo.*;
import pe.tasa.servicio.PedidoServicio;
import pe.tasa.util.ConexionDB;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  SISTEMA DE GESTIÓN DE PEDIDOS TASA      ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        try {
            System.out.println("[ 1 ] Conectando a PostgreSQL...");
            ConexionDB.getInstancia();
            System.out.println("      ✔ Conexión exitosa\n");

            System.out.println("[ 2 ] Insertando datos de prueba...");
            insertarDatosPrueba();
            System.out.println();

            System.out.println("[ 3 ] Registrando pedido...");
            PedidoServicio servicio = new PedidoServicio();
            List<int[]> items = Arrays.asList(
                    new int[]{1, 10},
                    new int[]{2, 5}
            );
            Pedido pedido = servicio.registrarPedido(
                    1, 1,
                    LocalDate.now().plusDays(7),
                    items,
                    "Entrega urgente"
            );
            System.out.println();

            System.out.println("[ 4 ] Confirmando pedido...");
            servicio.cambiarEstado(pedido.getIdPedido(), "CONFIRMADO", 1);
            System.out.println();

            System.out.println("[ 5 ] Pedidos registrados:");
            servicio.listarTodos().forEach(p ->
                    System.out.println("      " + p));
            System.out.println();

            System.out.println("[ 6 ] Auditoría:");
            new AuditoriaDAO().listarTodos().forEach(a ->
                    System.out.println("      " + a));

            System.out.println("\n✔ Demo completada exitosamente.");

        } catch (Exception e) {
            System.err.println("✘ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConexionDB.getInstancia().cerrar();
        }
    }

    private static void insertarDatosPrueba() throws Exception {
        RolDAO rolDAO         = new RolDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();
        ProductoDAO productoDAO = new ProductoDAO();

        if (rolDAO.listarTodos().isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre("ADMIN");
            rol.setDescripcion("Administrador");
            rolDAO.insertar(rol);
            System.out.println("      + Rol creado");
        }

        if (usuarioDAO.listarTodos().isEmpty()) {
            Usuario u = new Usuario();
            u.setNombre("Administrador TASA");
            u.setCorreo("admin@tasa.pe");
            u.setPassword("hash_password");
            u.setTelefono("999888777");
            u.setEstado("ACTIVO");
            u.setIdRol(1);
            usuarioDAO.insertar(u);
            System.out.println("      + Usuario creado");
        }

        if (empresaDAO.listarTodos().isEmpty()) {
            Empresa e = new Empresa();
            e.setRuc("20100000001");
            e.setRazonSocial("Distribuciones Lima S.A.C.");
            e.setTelefono("01-4445566");
            e.setDireccion("Av. Industrial 123, Lima");
            e.setCorreo("contacto@distrilima.pe");
            e.setEstado("ACTIVO");
            empresaDAO.insertar(e);
            System.out.println("      + Empresa creada");
        }

        if (productoDAO.listarTodos().isEmpty()) {
            Producto p1 = new Producto();
            p1.setCodigo("PROD-001");
            p1.setNombre("Cemento Portland 42.5 kg");
            p1.setDescripcion("Bolsa de cemento");
            p1.setUnidadMedida("BOLSA");
            p1.setPrecio(new BigDecimal("28.50"));
            p1.setEstado("ACTIVO");
            productoDAO.insertar(p1);

            Producto p2 = new Producto();
            p2.setCodigo("PROD-002");
            p2.setNombre("Varilla de acero 3/8\"");
            p2.setDescripcion("Varilla corrugada 9m");
            p2.setUnidadMedida("UNIDAD");
            p2.setPrecio(new BigDecimal("15.90"));
            p2.setEstado("ACTIVO");
            productoDAO.insertar(p2);
            System.out.println("      + Productos creados");
        }
    }
}