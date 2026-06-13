package pe.tasa.servicio;

import pe.tasa.dao.*;
import pe.tasa.modelo.*;
import pe.tasa.util.EmailUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoServicio {

    private final PedidoDAO pedidoDAO       = new PedidoDAO();
    private final ProductoDAO productoDAO   = new ProductoDAO();
    private final EmpresaDAO empresaDAO     = new EmpresaDAO();
    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public Pedido registrarPedido(int idEmpresa, int idUsuario,
                                  LocalDate fechaEntrega,
                                  List<int[]> items,
                                  String observaciones) throws Exception {

        Optional<Empresa> empresa = empresaDAO.buscarPorId(idEmpresa);
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empresa con ID " + idEmpresa + " no encontrada.");
        }

        List<DetallePedido> detalles       = new ArrayList<>();
        List<Producto>      productosFinal = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (int[] item : items) {
            int idProducto = item[0];
            int cantidad   = item[1];

            Optional<Producto> producto = productoDAO.buscarPorId(idProducto);
            if (producto.isEmpty()) {
                throw new IllegalArgumentException(
                        "Producto ID " + idProducto + " no encontrado.");
            }

            BigDecimal precioUnitario = producto.get().getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(
                    BigDecimal.valueOf(cantidad));

            DetallePedido detalle = new DetallePedido();
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
            productosFinal.add(producto.get());
            total = total.add(subtotal);
        }

        Pedido pedido = new Pedido();
        pedido.setIdEmpresa(idEmpresa);
        pedido.setIdUsuario(idUsuario);
        pedido.setFechaEntrega(fechaEntrega);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(total);
        pedido.setObservaciones(observaciones);
        pedido.setDetalles(detalles);

        pedidoDAO.insertar(pedido);

        auditoriaDAO.insertar(new Auditoria(
                idUsuario, "Pedido", "INSERT",
                "Pedido #" + pedido.getIdPedido() +
                        " registrado. Total: S/ " + total
        ));

        System.out.println("✔ Pedido #" + pedido.getIdPedido() + " registrado.");
        System.out.println("  Empresa : " + empresa.get().getRazonSocial());
        System.out.println("  Total   : S/ " + total);

        // ── Enviar correo con detalle completo + destino ──
        EmailUtil.getInstancia().notificarPedidoConDetalle(
                empresa.get().getCorreo(),
                empresa.get().getRazonSocial(),
                pedido.getIdPedido(),
                detalles,
                productosFinal,
                total.toString(),
                fechaEntrega != null ? fechaEntrega.toString() : "Por definir",
                observaciones != null ? observaciones : "Sin observaciones"
        );

        return pedido;
    }

    public void cambiarEstado(int idPedido, String nuevoEstado,
                              int idUsuario) throws Exception {
        pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
        auditoriaDAO.insertar(new Auditoria(
                idUsuario, "Pedido", "UPDATE",
                "Pedido #" + idPedido + " → " + nuevoEstado
        ));

        Optional<Empresa> empresa = empresaDAO.buscarPorId(
                pedidoDAO.buscarPorId(idPedido).get().getIdEmpresa()
        );
        empresa.ifPresent(e ->
                EmailUtil.getInstancia().notificarCambioEstado(
                        e.getCorreo(),
                        e.getRazonSocial(),
                        idPedido,
                        nuevoEstado
                )
        );

        System.out.println("✔ Pedido #" + idPedido + " → " + nuevoEstado);
    }

    public List<Pedido> listarTodos() throws Exception {
        return pedidoDAO.listarTodos();
    }
}