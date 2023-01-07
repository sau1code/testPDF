package com.fansysoft.testpdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fansysoft.testpdf.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName() + "-.-";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.inclBar.ivBack.setVisibility(View.INVISIBLE);
        binding.inclBar.tvPageName.setText("首頁");

        binding.btNext.setOnClickListener(v -> {
            startActivity( new Intent(this, SecActivity.class));
            overridePendingTransition(R.anim.trans_in_from_right,R.anim.trans_out_to_left);
        });


        // 用 XML 製作 PDF
        binding.btPdf.setOnClickListener(v -> {

            // 生成 PDF 文件
            PdfDocument pdfDocument = new PdfDocument();

            // 設定 頁面生成條件 (長、寬、頁數)
            PageInfo pageInfo = new PageInfo
                    .Builder(binding.inclPdf.llPdf.getWidth(), binding.inclPdf.llPdf.getHeight(), 1)
                    .create();

            // 打開頁面 (照上面條件)
            Page page = pdfDocument.startPage(pageInfo);

//            // 獲取 page 畫布，並在畫布上作畫 (這裡將 linearlayout 轉 bitmap 畫在畫布上)
//            Canvas canvas = page.getCanvas();
//            canvas.drawBitmap(getBitmapById(R.id.ll_pdf), 0, 0, null);

            // 另一種網路上的畫法
            binding.inclPdf.llPdf.draw(page.getCanvas());

            // 畫完將頁面關閉
            pdfDocument.finishPage(page);

            // 在手機上 新增名叫 pictures 的資料夾
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            // 在手機裡新增空白 PDF
            File newPdfFile = new File(dir, "newPdf.pdf");

            // 取得輸出流 指向空白 PDF
            // 將 pdfDocument 流入 outputStream
            try {
                FileOutputStream outputStream = new FileOutputStream(newPdfFile);
                pdfDocument.writeTo(outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 關閉 writeTo() 寫入狀態
            pdfDocument.close();

            // newPdfFile.exists(): true
            Log.d(TAG, "newPdfFile.exists(): " + newPdfFile.exists());

            // newPdfFile: /storage/emulated/0/Android/data/com.fansysoft.testpdf/files/Pictures/newPdf.pdf
            // 打開模擬器 可跟尋 Android/data/com.fansysoft.testpdf/files/Pictures/newPdf.pdf 路徑 找到檔案
            Log.d(TAG, "newPdfFile: " + newPdfFile);

            Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
            openPdf(newPdfFile);
        });
    }

//    private Bitmap getBitmapById(int id) {
//        return Bitmap.createBitmap(findViewById(id).getWidth(),
//                findViewById(id).getHeight(),
//                Bitmap.Config.ARGB_8888);
//    }

    // 模擬器開啟 PDF
    private void openPdf(File file) {
        if (file.exists()) {

            // Android 7.0及以上版本文件暴露异常 https://www.codeleading.com/article/24892804593/
            // Intent 啟動模式 https://wangkuiwu.github.io/2014/06/26/IntentFlag/
            // Build.VERSION.SDK_INT(當前版本) >= Build.VERSION_CODES.N (N好像是24)

            Uri url;
            int flag;

            // SDK24?以上版本 防止Url外漏 要處理過才能用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                url = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            } else {
                url = Uri.fromFile(file);
                flag = Intent.FLAG_ACTIVITY_NEW_TASK;
            }

            try {
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(url, "application/pdf")
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | flag));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "請先安裝PDF閱讀器", Toast.LENGTH_LONG).show();
            }
        }
    }

}