package report;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ReportGenerator {

    // =======================
    // REPORT RENTAL (SUDAH ADA)
    // =======================
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

    // =======================
    // REPORT PEMBAYARAN (BARU)
    // =======================
    public static void generatePaymentReceipt(
            int userId,
            String psName,
            int duration,
            double pricePerHour,
            double total,
            LocalDateTime time
    ) {

        String fileName = "struk_pembayaran_user_" + userId + ".txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("STRUK PEMBAYARAN RENTAL PS\n");
            writer.write("===============================\n");
            writer.write("Tanggal    : " + time + "\n");
            writer.write("User ID    : " + userId + "\n");
            writer.write("PS         : " + psName + "\n");
            writer.write("Durasi     : " + duration + " jam\n");
            writer.write("Harga/jam  : " + pricePerHour + "\n");
            writer.write("-------------------------------\n");
            writer.write("TOTAL BAYAR: " + total + "\n");
            writer.write("===============================\n");

            System.out.println("Struk pembayaran dibuat: " + fileName);

        } catch (IOException e) {
            System.out.println("Gagal membuat struk pembayaran");
            e.printStackTrace();
        }
    }
}
