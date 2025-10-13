package mx.uam.ayd.proyecto.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;

import java.io.File;
import java.util.List;

public class UtilPDF {

    /**
     * Genera un documento PDF con la información de la venta y sus detalles,
     * y permite al usuario seleccionar dónde guardarlo.
     *
     * @param detallesVenta lista de detalles de la venta
     * @param venta venta correspondiente a los detalles
     */
    public void crearDocumentoVenta(List<DetalleVenta> detallesVenta, Venta venta){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("venta_" + venta.getIdVenta() + ".pdf");

        // Establecer carpeta Descargas como inicial
        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }
        File archivo = fileChooser.showSaveDialog(new Stage());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }
            try {
                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Título
                document.add(new Paragraph("Venta_" + venta.getIdVenta()).setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                // Encabezados
                table.addCell("idProducto");
                table.addCell("Nombre_producto");
                table.addCell("Marca");
                table.addCell("Precio");
                table.addCell("Cantidad");
                table.addCell("Subtotal");

                for (DetalleVenta detalleVenta : detallesVenta) {
                    table.addCell(String.valueOf(detalleVenta.getProducto().getIdProducto()));
                    table.addCell(detalleVenta.getProducto().getNombre());
                    table.addCell(String.valueOf(detalleVenta.getProducto().getMarcaProducto()));
                    table.addCell(String.valueOf(detalleVenta.getProducto().getPrecio()));
                    table.addCell(String.valueOf(detalleVenta.getCantidadVendida()));
                    table.addCell(String.valueOf(detalleVenta.getSubtotal()));
                }
                document.add(table);
                document.add(new Paragraph("Total: " + venta.getMontoTotal()).setFontSize(14));
                document.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Permite descargar un reporte de ventas en formato PDF
     * y permite al usuario seleccionar dónde guardarlo
     *
     * @param ventas lista con los datos del reporte a descargar
     */
    public void descargarReporte(List<ReporteVentaDTO> ventas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_de_venta.pdf");

        // Establecer carpeta Descargas como inicial
        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }
        File archivo = fileChooser.showSaveDialog(new Stage());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }
            try {
                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Título
                document.add(new Paragraph("Reporte de venta").setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                // Encabezados
                table.addCell("Fecha");
                table.addCell("Nombre_producto");
                table.addCell("Tipo");
                table.addCell("Vendidos");
                table.addCell("Total");

                for (ReporteVentaDTO venta : ventas) {
                    table.addCell(String.valueOf(venta.getFecha()));
                    table.addCell(venta.getNombreProducto());
                    table.addCell(String.valueOf(venta.getTipoProducto()));
                    table.addCell(String.valueOf(venta.getCantidadVendida()));
                    table.addCell(String.valueOf(venta.getTotalVenta()));
                }
                document.add(table);
                document.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
