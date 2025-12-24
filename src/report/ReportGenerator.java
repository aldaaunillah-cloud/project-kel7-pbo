package report;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ReportGenerator {

    public static void generateRentalReport(List<String> data) {
        String fileName = "laporan_rental.txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("LAPORAN RENTAL\n");
            writer.write("====================\n");
            writer.write("Tanggal: " + LocalDateTime.now() + "\n\n");

            for (String row : data) {
                writer.write(row + "\n");
            }

            writer.write("\n=== END OF REPORT ===");
            System.out.println("Report berhasil dibuat: " + fileName);

        } catch (IOException e) {
            System.out.println("Gagal membuat report");
            e.printStackTrace();
        }
    }
}
