package com.swarauto;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseTest {
    protected Context appContext;

    protected String tempDir;

    @Before
    public void setup() throws IOException {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        tempDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sw-bot-test";
        new File(tempDir).mkdirs();

        prepareTestResources();
    }

    private void prepareTestResources() throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        for (String fileName : context.getAssets().list("")) {
            try {
                copyInputStreamToFile(context.getAssets().open(fileName), getTempFilePath(fileName));
            } catch (FileNotFoundException ignored) {

            }
        }
    }

    protected String getTempFilePath(String name) {
        return tempDir + "/" + name;
    }

    private void copyInputStreamToFile(InputStream in, String outputFilePath) {
        File file = new File(outputFilePath);
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
