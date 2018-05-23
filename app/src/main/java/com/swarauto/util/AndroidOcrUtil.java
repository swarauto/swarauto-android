package com.swarauto.util;

import android.content.res.AssetManager;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.swarauto.MyApp.appContext;
import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

public class AndroidOcrUtil implements OcrUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AndroidOcrUtil.class);
    private final tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
    private static final String TESSDATA_FOLDER_NAME = "tessdata";
    public static String DATA_PATH;
    private boolean initialized;

    public AndroidOcrUtil() {
        DATA_PATH = AndroidUtil.getHomeDir().getAbsolutePath() + "/" + TESSDATA_FOLDER_NAME;
    }

    @Override
    public void initialize() {
        if (isInitialized()) return;

        try {
            copyData(false);
        } catch (IOException e) {
            LOG.error("Failed to copy tessdata", e);
            return;
        }

        if (api.Init(DATA_PATH, "eng") != 0) {
            LOG.error("Failed to init OCR lib. Make sure to copy trained data to " + DATA_PATH);
            initialized = false;
        } else {
            initialized = true;
        }
    }

    public void reinitialize() {
        initialized = false;

        try {
            copyData(true);
        } catch (IOException e) {
            LOG.error("Failed to copy tessdata", e);
            return;
        }

        if (api.Init(DATA_PATH, "eng") != 0) {
            LOG.error("Failed to init OCR lib. Make sure to copy trained data to " + DATA_PATH);
            initialized = false;
        } else {
            initialized = true;
        }
    }

    private void copyData(boolean force) throws IOException {
        AssetManager assetManager = appContext.getAssets();
        new File(DATA_PATH).mkdirs();
        for (String fileName : assetManager.list(TESSDATA_FOLDER_NAME)) {
            InputStream is = assetManager.open(TESSDATA_FOLDER_NAME + "/" + fileName);

            File outputFile = new File(DATA_PATH + "/" + fileName);
            if (force || !outputFile.exists()) {
                FileUtil.fileCopy(is, outputFile.getAbsolutePath());
            }
            if (is != null) {
                is.close();
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public String text(File file) {
        return text(file, null);
    }

    @Override
    public String text(File file, Rectangle box) {
        lept.PIX image = pixRead(file.getAbsolutePath());
        api.SetImage(image);
        if (box != null) {
            api.SetRectangle(box.x, box.y, box.width, box.height);
        }
        BytePointer outText = api.GetUTF8Text();
        String text = outText.getString().trim();

        api.Clear();
        outText.deallocate();
        pixDestroy(image);

        return text;
    }
}
