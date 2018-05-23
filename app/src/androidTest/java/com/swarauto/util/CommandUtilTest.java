package com.swarauto.util;

import android.support.test.runner.AndroidJUnit4;

import com.swarauto.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class CommandUtilTest extends BaseTest {
    @Test
    public void testCapture() {
        AndroidCommandUtil commandUtil = new AndroidCommandUtil();
        commandUtil.screenShotDirPath = "/sdcard/sw-bot-test";

        String screenFilePath = commandUtil.capturePhoneScreen();
        Assert.assertNotNull(screenFilePath);
    }

    @Test
    public void testSpeedCapture() {
        AndroidCommandUtil commandUtil = new AndroidCommandUtil();
        commandUtil.screenShotDirPath = "/sdcard/sw-bot-test";

        long start = System.currentTimeMillis();
        String screenFilePath = commandUtil.capturePhoneScreen();
        long end = System.currentTimeMillis();
        Assert.assertNotNull(screenFilePath);
        System.out.println("Time: " + (end - start));
        Assert.assertTrue(end - start < 3000); // Speed capture must be faster than 3s
    }

}
