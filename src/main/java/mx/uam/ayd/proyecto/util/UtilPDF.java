package mx.uam.ayd.proyecto.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.Cita;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.generarReceta.DatosReceta;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase de utilidad para la generación de documentos PDF.
 * @author InovaSoftwareGroup
 */
public class UtilPDF {

    /**
     * Genera un comprobante PDF para una Cita agendada.
     * Corresponde a la HU-03.
     *
     * @param cita La cita de la cual se generará el comprobante.
     */
    public void generarComprobanteCita(Cita cita) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Comprobante de Cita");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        String nombreArchivo = "Cita_" + cita.getNombreSolicitante().replace(" ", "_") + "_" + cita.getIdCita() + ".pdf";
        fileChooser.setInitialFileName(nombreArchivo);

        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }

        File archivo = fileChooser.showSaveDialog(new Stage());

        if (archivo != null) {
            try {
                String ruta = archivo.getAbsolutePath();
                if (!ruta.endsWith(".pdf")) {
                    ruta += ".pdf";
                }

                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Encabezado
                document.add(new Paragraph("CLÍNICA VETERINARIA UAM")
                        .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Comprobante de Cita")
                        .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));

                // Tabla de Detalles
                Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
                table.setWidth(UnitValue.createPercentValue(100));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy, HH:mm 'hrs'");

                agregarFila(table, "ID de Cita:", String.valueOf(cita.getIdCita()));
                agregarFila(table, "Fecha y Hora:", cita.getFechaHora().format(formatter));
                agregarFila(table, "Tipo de Servicio:", cita.getTipo().toString());
                agregarFila(table, "Solicitante:", cita.getNombreSolicitante());
                agregarFila(table, "Contacto:", cita.getContacto());

                String nombreVet = (cita.getVeterinario() != null) ? cita.getVeterinario().getNombreCompleto() : "No asignado";
                agregarFila(table, "Veterinario:", nombreVet);

                String motivo = (cita.getMotivo() != null && !cita.getMotivo().isEmpty()) ? cita.getMotivo() : "---";
                agregarFila(table, "Motivo:", motivo);

                String notas = (cita.getNotas() != null && !cita.getNotas().isEmpty()) ? cita.getNotas() : "---";
                agregarFila(table, "Notas/Observaciones:", notas);

                document.add(table);

                // Pie de pagina
                document.add(new Paragraph("\n\n"));
                document.add(new Paragraph("Favor de presentarse 10 minutos antes de su cita.")
                        .setItalic().setFontSize(10).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("¡Gracias por confiar en nosotros!")
                        .setBold().setTextAlignment(TextAlignment.CENTER));

                document.close();
                System.out.println("PDF generado exitosamente en: " + archivo.getAbsolutePath());

            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Error al generar el PDF: " + ex.getMessage());
            }
        }
    }

    /**
     * Método auxiliar para agregar filas a la tabla del comprobante.
     *
     * @param table La tabla a la que se añadirán las celdas.
     * @param etiqueta El texto de la etiqueta (negrita).
     * @param valor El valor del campo.
     */
    private void agregarFila(Table table, String etiqueta, String valor) {
        table.addCell(new Cell().add(new Paragraph(etiqueta).setBold()));
        table.addCell(new Cell().add(new Paragraph(valor)));
    }

    /**
     * Genera un documento PDF con la información de la venta y sus detalles.
     * * @param detallesVenta Lista de detalles de la venta.
     * @param venta La venta correspondiente.
     */
    public void crearDocumentoVenta(List<DetalleVenta> detallesVenta, Venta venta) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("venta_" + venta.getIdVenta() + ".pdf");

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

                document.add(new Paragraph("Venta_" + venta.getIdVenta()).setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

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
     * Permite descargar un reporte de ventas en formato PDF.
     * * @param ventas Lista con los datos del reporte a descargar.
     */
    public void descargarReporte(List<ReporteVentaDTO> ventas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_de_venta.pdf");

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

                document.add(new Paragraph("Reporte de venta").setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

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

    /**
     * Genera un archivo PDF que contiene una receta médica.
     * * @param datosReceta Lista de objetos con la información de la receta.
     */
    public String crearReceta(List<DatosReceta> datosReceta) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Receta.pdf");

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

                try {
                    String imgPath = getClass().getResource("/imagenes/logo.jpg").getPath();
                    ImageData imageData = ImageDataFactory.create(imgPath);
                    Image logo = new Image(imageData);
                    logo.scaleToFit(50, 50);

                    float[] colWidths = {1, 5};
                    Table encabezado = new Table(colWidths);
                    encabezado.setWidth(UnitValue.createPercentValue(100));

                    encabezado.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));
                    encabezado.addCell(new Cell().add(new Paragraph("Kroketa").setBold().setFontSize(24))
                            .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                    document.add(encabezado);

                } catch (NullPointerException e) {
                    document.add(new Paragraph("Kroketa - Receta Médica").setBold().setFontSize(24));
                }

                document.add(new Paragraph("Receta médica").setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                table.addCell("Medicamento");
                table.addCell("Dosis");
                table.addCell("Cada");
                table.addCell("Hasta");
                table.addCell("Nota");

                for (DatosReceta datos : datosReceta) {
                    table.addCell(String.valueOf(datos.getProducto()));
                    table.addCell(datos.getDosis());
                    table.addCell(datos.getCada());
                    table.addCell(datos.getHasta());
                    table.addCell(datos.getNota());
                }

                document.add(table);

                // Intentar cargar imágenes de pie de página
                try {
                    String imgPath = getClass().getResource("/imagenes/decoracion.jpeg").getPath();
                    ImageData imageData = ImageDataFactory.create(imgPath);
                    Image image = new Image(imageData);
                    image.scaleToFit(80, 80);

                    imgPath = getClass().getResource("/imagenes/firma.png").getPath();
                    imageData = ImageDataFactory.create(imgPath);
                    Image firma = new Image(imageData);
                    firma.scaleToFit(80, 80);

                    float[] colWidthsFirma = {2, 4, 3};
                    Table pie = new Table(colWidthsFirma);
                    pie.setWidth(UnitValue.createPercentValue(100));

                    pie.addCell(new Cell().add(image).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    pie.addCell(new Cell().add(new Paragraph("Firma del veterinario:").setBold().setFontSize(12))
                            .setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                    pie.addCell(new Cell().add(firma).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));

                    document.add(pie);
                } catch (Exception ignored) {}

                document.close();

                return ruta;

            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return null;
    }
}
