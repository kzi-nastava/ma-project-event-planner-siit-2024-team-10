package m3.eventplanner.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtils {
    private Context context;
    public PdfUtils(Context context){
        this.context = context;
    }

    public File savePdfFile(byte[] pdfBytes, String filename) {
        try {
            // Create file in app's private directory
            File reportFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    filename + System.currentTimeMillis() + ".pdf");

            try (FileOutputStream fos = new FileOutputStream(reportFile)) {
                fos.write(pdfBytes);
                fos.flush();
                return reportFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openPdfFile(File file) {
        // Create URI using FileProvider
        Uri uri = FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider",
                file);

        // Create intent to view PDF
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
