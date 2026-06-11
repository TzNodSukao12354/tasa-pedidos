package pe.tasa.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtil {

    private static final Logger LOG = Logger.getLogger(EmailUtil.class.getName());
    private static EmailUtil instancia;
    private Session session;
    private String usuarioCorreo;
    private String remitente;

    private EmailUtil() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String host     = dotenv.get("MAIL_HOST",      "smtp.gmail.com");
        String puerto   = dotenv.get("MAIL_PORT",      "587");
        usuarioCorreo   = dotenv.get("MAIL_USUARIO",   "");
        String password = dotenv.get("MAIL_PASSWORD",  "");
        remitente       = dotenv.get("MAIL_REMITENTE", "Sistema TASA");

        Properties props = new Properties();
        props.put("mail.smtp.host",            host);
        props.put("mail.smtp.port",            puerto);
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust",       host);

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuarioCorreo, password);
            }
        });
        LOG.info("✔ Sesión de correo configurada.");
    }

    public static synchronized EmailUtil getInstancia() {
        if (instancia == null) instancia = new EmailUtil();
        return instancia;
    }

    public void enviar(String destinatario, String asunto, String cuerpo) {
        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuarioCorreo, remitente));
            mensaje.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setContent(cuerpo, "text/html; charset=UTF-8");
            Transport.send(mensaje);
            LOG.info("✔ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "✘ Error: " + e.getMessage(), e);
        }
    }

    public void notificarCambioEstado(String correoEmpresa, String razonSocial,
                                      int idPedido, String nuevoEstado) {
        String asunto = "Pedido #" + idPedido + " — " + nuevoEstado;
        String color  = switch (nuevoEstado) {
            case "CONFIRMADO"  -> "blue";
            case "EN_DESPACHO" -> "orange";
            case "ENTREGADO"   -> "green";
            case "ANULADO"     -> "red";
            default            -> "gray";
        };
        String cuerpo = """
                <html>
                <body style="font-family:Arial,sans-serif;padding:20px;">
                    <h2 style="color:#1a237e;">Sistema TASA</h2>
                    <hr/>
                    <p>Estimado <strong>%s</strong>,</p>
                    <p>Su pedido <strong>#%d</strong> cambió de estado:</p>
                    <p style="font-size:22px;color:%s;"><strong>%s</strong></p>
                    <hr/>
                    <small style="color:gray;">Mensaje automático — Sistema TASA</small>
                </body>
                </html>
                """.formatted(razonSocial, idPedido, color, nuevoEstado);
        enviar(correoEmpresa, asunto, cuerpo);
    }

    public void notificarPedidoConDetalle(
            String correoEmpresa,
            String razonSocial,
            int idPedido,
            List<pe.tasa.modelo.DetallePedido> detalles,
            List<pe.tasa.modelo.Producto> productos,
            String total,
            String fechaEntrega) {

        String asunto = "Pedido #" + idPedido + " registrado — Sistema TASA";

        StringBuilder filas = new StringBuilder();
        for (int i = 0; i < detalles.size(); i++) {
            pe.tasa.modelo.DetallePedido d = detalles.get(i);
            pe.tasa.modelo.Producto p = productos.get(i);
            filas.append("""
                <tr>
                    <td style="padding:8px;border:1px solid #ddd;">%s</td>
                    <td style="padding:8px;border:1px solid #ddd;text-align:center;">%d %s</td>
                    <td style="padding:8px;border:1px solid #ddd;text-align:right;">S/ %s</td>
                    <td style="padding:8px;border:1px solid #ddd;text-align:right;">S/ %s</td>
                </tr>
                """.formatted(
                            p.getNombre(),
                            d.getCantidad(),
                            p.getUnidadMedida(),
                            d.getPrecioUnitario(),
                            d.getSubtotal()
                    )
            );
        }

        String cuerpo = """
                <html>
                <body style="font-family:Arial,sans-serif;padding:20px;color:#333;">
                    <h2 style="color:#1a237e;">Sistema TASA</h2>
                    <hr/>
                    <p>Estimado cliente <strong>%s</strong>,</p>
                    <p>Su pedido ha sido registrado exitosamente.</p>
                    <table style="border-collapse:collapse;width:100%%;">
                        <tr style="background:#1a237e;color:white;">
                            <td style="padding:8px;"><strong>N° Pedido</strong></td>
                            <td style="padding:8px;">#%d</td>
                            <td style="padding:8px;"><strong>Fecha entrega</strong></td>
                            <td style="padding:8px;">%s</td>
                        </tr>
                    </table>
                    <br/>
                    <h3 style="color:#1a237e;">Detalle del Pedido</h3>
                    <table style="border-collapse:collapse;width:100%%;">
                        <tr style="background:#f2f2f2;">
                            <th style="padding:8px;border:1px solid #ddd;text-align:left;">Producto</th>
                            <th style="padding:8px;border:1px solid #ddd;">Cantidad</th>
                            <th style="padding:8px;border:1px solid #ddd;">Precio Unit.</th>
                            <th style="padding:8px;border:1px solid #ddd;">Subtotal</th>
                        </tr>
                        %s
                        <tr style="background:#e8f5e9;">
                            <td colspan="3" style="padding:8px;border:1px solid #ddd;
                                text-align:right;"><strong>TOTAL</strong></td>
                            <td style="padding:8px;border:1px solid #ddd;
                                text-align:right;"><strong>S/ %s</strong></td>
                        </tr>
                    </table>
                    <br/>
                    <p style="color:orange;"><strong>Estado: PENDIENTE</strong></p>
                    <p>Le notificaremos cuando su pedido sea despachado.</p>
                    <hr/>
                    <small style="color:gray;">Mensaje automático — Sistema TASA</small>
                </body>
                </html>
                """.formatted(razonSocial, idPedido, fechaEntrega,
                filas.toString(), total);

        enviar(correoEmpresa, asunto, cuerpo);
    }
}