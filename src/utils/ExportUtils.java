package utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportUtils {
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // Export JTable to CSV
    public static boolean exportTableToCSV(JTable table, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export " + title + " to CSV");

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String suggestedName = title.toLowerCase().replace(" ", "_") + "_" + timestamp + ".csv";
        fileChooser.setSelectedFile(new java.io.File(suggestedName));

        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }

            if (file.exists()) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "File already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

            return writeTableToCSV(table, file, title);
        }

        return false;
    }

    private static boolean writeTableToCSV(JTable table, java.io.File file, String title) {

        final java.io.File finalFile = file; // ✔ make final for lambdas

        try (FileWriter writer = new FileWriter(finalFile)) {
            TableModel model = table.getModel();

            writer.write("# " + title + " Export\n");
            writer.write("# Generated on: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("# Total Records: " + model.getRowCount() + "\n");
            writer.write("sep=,\n");

            // Write column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(escapeCSV(model.getColumnName(i)));
                if (i < model.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");

            // Use AtomicInteger instead of int
            AtomicInteger exportedRows = new AtomicInteger(0);

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.write(escapeCSV(value != null ? value.toString() : ""));
                    if (col < model.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
                exportedRows.incrementAndGet(); // ✔ safe increment

                if (model.getRowCount() > 100 && row % 50 == 0) {
                    SwingUtilities.invokeLater(() -> {});
                }
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "Successfully exported " + exportedRows.get() +
                                " records to:\n" + finalFile.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });

            System.out.println("Exported " + exportedRows.get() +
                    " records to: " + finalFile.getAbsolutePath());
            return true;

        } catch (IOException e) {

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        "Error exporting data:\n" + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            });

            System.err.println("Export error: " + e.getMessage());
            return false;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            String escaped = value.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return value;
    }

    // Export Appointments with details
    public static boolean exportAppointmentsWithDetails(JTable table, String title) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export " + title + " Report");

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String suggestedName = "appointment_report_" + timestamp + ".csv";
        fileChooser.setSelectedFile(new java.io.File(suggestedName));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            java.io.File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }

            final java.io.File finalFile = file; // ✔ final for lambdas

            try (FileWriter writer = new FileWriter(finalFile)) {
                TableModel model = table.getModel();

                writer.write("# Appointment Report - Hospital Management System\n");
                writer.write("# Generated: " + LocalDateTime.now() + "\n");
                writer.write("# Report Type: Detailed Appointment Summary\n");
                writer.write("sep=,\n");

                String[] enhancedHeaders = {
                        "Appointment ID", "Patient Name", "Doctor Name",
                        "Specialization", "Date", "Time", "Status",
                        "Reason", "Diagnosis", "Fee", "Created Date"
                };

                for (int i = 0; i < enhancedHeaders.length; i++) {
                    writer.write(escapeCSV(enhancedHeaders[i]));
                    if (i < enhancedHeaders.length - 1) writer.write(",");
                }
                writer.write("\n");

                AtomicInteger count = new AtomicInteger(0); // ✔ instead of int

                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < Math.min(model.getColumnCount(), enhancedHeaders.length); col++) {
                        Object value = model.getValueAt(row, col);
                        writer.write(escapeCSV(value != null ? value.toString() : "N/A"));
                        if (col < enhancedHeaders.length - 1) writer.write(",");
                    }
                    writer.write("\n");
                    count.incrementAndGet(); // ✔ safe
                }

                writer.write("\n# Summary\n");
                writer.write("Total Appointments," + count.get() + "\n");
                writer.write("Export Timestamp," + LocalDateTime.now() + "\n");
                writer.write("Generated By,Hospital Management System\n");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null,
                            "Report generated successfully!\n" +
                                    "Total appointments exported: " + count.get() + "\n" +
                                    "File: " + finalFile.getName(),
                            "Report Generated",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                });

                return true;

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error generating report: " + e.getMessage(),
                        "Report Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return false;
    }

    // Quick export
    public static void quickExport(JTable table, String type) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String filename = "export_" + type.toLowerCase() + "_" + timestamp + ".csv";

        java.io.File file = new java.io.File(filename);

        if (writeTableToCSV(table, file, type + " Data")) {
            System.out.println("Quick export completed: " + file.getAbsolutePath());
        }
    }

    // Batch export
    public static void exportMultipleTables(JTable[] tables, String[] titles) {
        if (tables.length != titles.length) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tables and titles count mismatch",
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setDialogTitle("Select Export Folder");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (dirChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            java.io.File folder = dirChooser.getSelectedFile();

            int successCount = 0;
            for (int i = 0; i < tables.length; i++) {
                String filename = titles[i].toLowerCase().replace(" ", "_") + "_export.csv";
                java.io.File file = new java.io.File(folder, filename);

                if (writeTableToCSV(tables[i], file, titles[i])) {
                    successCount++;
                }
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Batch export completed!\n" +
                            "Successfully exported: " + successCount + "/" + tables.length + " files\n" +
                            "Location: " + folder.getAbsolutePath(),
                    "Batch Export Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
