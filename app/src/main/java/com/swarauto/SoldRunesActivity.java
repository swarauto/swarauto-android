package com.swarauto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.swarauto.dependencies.DependenciesRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SoldRunesActivity extends AppCompatActivity {
    private Settings settings;
    private List<File> fileList = new ArrayList<>();
    private int currentIndex;

    private TextView textTitle;
    private ImageView imageScreen;
    private Button btnPrev;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_runes);

        settings = DependenciesRegistry.settings;

        // Init views
        textTitle = findViewById(R.id.textTitle);
        imageScreen = findViewById(R.id.imageScreen);
        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(-1);
            }
        });
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate(1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        refreshFiles();
    }

    private void refreshFiles() {
        File dir = new File(settings.getSoldRunesFolderPath());
        dir.mkdirs();

        fileList.clear();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }

        showImageAtIndex(0);
    }

    private void navigate(int direction) {
        int index = currentIndex + direction;
        if (index < 0) index = 0;
        if (index > fileList.size() - 1) index = fileList.size() - 1;

        showImageAtIndex(index);
    }

    private void showImageAtIndex(int index) {
        if (fileList.size() == 0) {
            imageScreen.setVisibility(View.GONE);
            textTitle.setText("No record");
        } else {
            imageScreen.setVisibility(View.VISIBLE);

            currentIndex = index;
            if (index < 0 || index > fileList.size() - 1) {
                currentIndex = 0;
            }

            File imgFile = fileList.get(currentIndex);
            Date lastModifiedDate = new Date(imgFile.lastModified());
            textTitle.setText((currentIndex + 1) + "/" + fileList.size()
                    + "\n" + imgFile.getName()
                    + "\n" + lastModifiedDate.toString());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageScreen.setImageBitmap(myBitmap);
        }
    }
}
