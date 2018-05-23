package com.swarauto.util;

import android.support.test.runner.AndroidJUnit4;

import com.swarauto.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class OcrUtilTest extends BaseTest {

    private final OcrUtil ocrUtil;

    public OcrUtilTest() throws InstantiationException {
        ocrUtil = new AndroidOcrUtil();
        ocrUtil.initialize();
    }

    @Test
    public void testTextBufferedImage() throws IOException {
        Assert.assertEquals("Hero", ocrUtil.text(new File(getTempFilePath("hero.png")), null));
    }

    @Test
    public void testTextFile_withPercent() throws IOException {
        String text = ocrUtil.text(new File(getTempFilePath("percent.png")), null);
        Assert.assertTrue(text.contains("%") || text.contains("°/o"));
    }

    @Test
    public void testRareLevel() {
        Rectangle box = new Rectangle(1176, 350, 1325 - 1176, 405 - 350);
        Assert.assertEquals("Rare", ocrUtil.text(new File(getTempFilePath("sampleRuneReward.png")), box));
    }
}
