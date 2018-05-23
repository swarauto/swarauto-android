package com.swarauto.util;

import android.graphics.Bitmap;

import com.swarauto.game.GameStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AndroidCommandUtil implements CommandUtil {
    private static final Logger LOG = LoggerFactory.getLogger(AndroidCommandUtil.class);

    public String screenShotDirPath;
    private Point centerScreen = new Point();

    @Override
    public String capturePhoneScreen() {
        if (screenShotDirPath == null) {
            screenShotDirPath = AndroidUtil.getHomeDir().getAbsolutePath();
        }

        String screenShotRawPath = screenShotDirPath + "/phoneScreenshot";

        long start = System.currentTimeMillis();
        if (AndroidUtil.execRootCmd("/system/bin/screencap " + screenShotRawPath)) {
            long afterCapture = System.currentTimeMillis();

            // Read raw (device default orientation)
            byte[] rawData = AndroidUtil.readFileToBytes(screenShotRawPath);
            Rectangle defaultScreenSize = AndroidUtil.getDeviceDefaultSize();
            Bitmap origin = Bitmap.createBitmap(defaultScreenSize.width, defaultScreenSize.height, Bitmap.Config.ARGB_8888);
            origin.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
            long loadOrigin = System.currentTimeMillis();

            // Convert to RGB565
            Bitmap opaqueBitmap = AndroidUtil.compressBitmap(origin, Bitmap.Config.RGB_565, false);

            // Rotate
            int orientation = AndroidUtil.getDeviceCurrentOrientation();
            Bitmap rotated = AndroidUtil.horizontalizeBitmap(opaqueBitmap, orientation);
            long rotateOrigin = System.currentTimeMillis();

            centerScreen.x = rotated.getWidth() / 2;
            centerScreen.y = rotated.getHeight() / 2;

            // Save
            AndroidUtil.saveBitmapToFile(rotated, screenShotRawPath + ".png");
            long saveRotate = System.currentTimeMillis();

            LOG.info("captureScreen: " + (afterCapture - start)
                    + " + " + (loadOrigin - afterCapture)
                    + " + " + (rotateOrigin - loadOrigin)
                    + " + " + (saveRotate - rotateOrigin)
                    + " = " + (saveRotate - start));
            return screenShotRawPath + ".png";
        } else {
            return null;
        }
    }

    @Override
    public boolean runCmd(String... strings) {
        return false;
    }

    @Override
    public void tapScreen(final String x, final String y) {
        LOG.info("tapScreen " + x + "," + y);
        AndroidUtil.execRootCmd("/system/bin/input tap " + x + " " + y);
    }

    @Override
    public void tapScreenCenter() {
        int tapX = centerScreen.x + (int) (10 * (Math.random() - Math.random()));
        int tapY = centerScreen.y + (int) (10 * (Math.random() - Math.random()));

        if (tapX < 0) tapX = 0;
        if (tapY < 0) tapY = 0;

        tapScreen(String.valueOf(tapX), String.valueOf(tapY));
    }

    @Override
    public void screenLog(GameStatus status, File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (status.getScreenFile() == null || status.getScreenFile().length() == 0) {
            return;
        }

        try {
            FileUtil.fileCopy(status.getScreenFile(), folder.getAbsolutePath() + "/" + String.format("%s.png", System.currentTimeMillis()));
        } catch (final IOException ex) {
            LOG.error("Could not log screenshot", ex);
        }
    }
}
