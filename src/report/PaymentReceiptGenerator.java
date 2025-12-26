package report;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

public class PaymentReceiptGenerator {

    public static void generate(
            int userId,
            String username,
            String psName,
            int duration,
            int pricePerHour,
            int total
    ) {
        try {
            String fileName = "STRUK_RENTAL_" + userId + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font title = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 12);

            document.add(new Paragraph("STRUK RENTAL PLAYSTATION", title));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("User        : " + username, normal));
            document.add(new Paragraph("PS          : " + psName, normal));
            document.add(new Paragraph("Durasi      : " + duration + " jam", normal));
            document.add(new Paragraph("Harga / jam : Rp " + pricePerHour, normal));
            document.add(new Paragraph("TOTAL       : Rp " + total, normal));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Waktu       : " + LocalDateTime.now(), normal));

            document.close();

            // AUTO OPEN PDF
            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
