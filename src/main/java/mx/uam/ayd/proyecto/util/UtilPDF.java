package mx.uam.ayd.proyecto.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uam.ayd.proyecto.negocio.modelo.DetalleVenta;
import mx.uam.ayd.proyecto.negocio.modelo.ReporteVentaDTO;
import mx.uam.ayd.proyecto.negocio.modelo.Venta;
import mx.uam.ayd.proyecto.presentacion.generarReceta.Medicacion;

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

    /**
     * Genera un archivo PDF que contiene una receta médica basada en una lista de objetos DatosReceta.
     * @param datosReceta Lista de objetos DatosReceta que contienen la información a mostrar en la receta.
     */
    public String crearReceta(List<Medicacion> datosReceta) {

        // Configuración inicial del FileChooser para guardar el PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.setInitialFileName("Receta.pdf");

        // Seleccionar la carpeta Descargas como directorio inicial
        String userHome = System.getProperty("user.home");
        File carpetaDescargas = new File(userHome, "Downloads");
        if (carpetaDescargas.exists()) {
            fileChooser.setInitialDirectory(carpetaDescargas);
        }

        // Mostrar ventana para elegir ubicación del archivo
        File archivo = fileChooser.showSaveDialog(new Stage());
        if (archivo != null) {
            String ruta = archivo.getAbsolutePath();

            // Asegurar extensión .pdf
            if (!ruta.endsWith(".pdf")) {
                ruta += ".pdf";
            }

            try {
                // Inicialización del PDF
                PdfWriter writer = new PdfWriter(ruta);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Cargar imagen del logo
                String imgPath = getClass().getResource("/imagenes/logo.jpg").getPath();
                ImageData imageData = ImageDataFactory.create(imgPath);
                Image logo = new Image(imageData);
                logo.scaleToFit(50, 50);

                // Tabla para colocar logo y título en una misma fila
                float[] colWidths = {1, 5};
                Table encabezado = new Table(colWidths);
                encabezado.setWidth(UnitValue.createPercentValue(100));

                // Celda con el logo
                encabezado.addCell(
                        new Cell()
                                .add(logo)
                                .setBorder(Border.NO_BORDER)
                );

                // Celda con el nombre del negocio
                encabezado.addCell(
                        new Cell()
                                .add(new Paragraph("Kroketa").setBold().setFontSize(24))
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                document.add(encabezado);

                // Título de la receta
                document.add(new Paragraph("Receta médica").setBold().setFontSize(16));

                float[] columnWidths = {100f, 100f, 100f, 100f, 100f};
                Table table = new Table(columnWidths);

                // Encabezados de la tabla
                table.addCell("Medicamento");
                table.addCell("Dosis");
                table.addCell("Cada");
                table.addCell("Hasta");
                table.addCell("Nota");

                // Llenar filas de la tabla con datos
                for (Medicacion datos : datosReceta) {
                    table.addCell(String.valueOf(datos.getProducto()));
                    table.addCell(datos.getDosis());
                    table.addCell(datos.getCada());
                    table.addCell(datos.getHasta());
                    table.addCell(datos.getNota());
                }

                document.add(table);

                // Imagen ilustrativa
                imgPath = getClass().getResource("/imagenes/decoracion.jpeg").getPath();
                imageData = ImageDataFactory.create(imgPath);
                Image image = new Image(imageData);
                image.scaleToFit(80, 80);

                // Imagen firma
                imgPath = getClass().getResource("/imagenes/firma.png").getPath();
                imageData = ImageDataFactory.create(imgPath);
                Image firma = new Image(imageData);
                firma.scaleToFit(80, 80);

                float[] colWidthsFirma = {2, 4, 3};
                Table pie = new Table(colWidthsFirma);
                pie.setWidth(UnitValue.createPercentValue(100));

                // Imagen lateral del pie
                pie.addCell(
                        new Cell()
                                .add(image)
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                // Texto del pie
                pie.addCell(
                        new Cell()
                                .add(new Paragraph("Firma del veterinario:").setBold().setFontSize(12))
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                // Imagen de firma
                pie.addCell(
                        new Cell()
                                .add(firma)
                                .setBorder(Border.NO_BORDER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                );

                document.add(pie);

                // Cerrar documento
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
