package com.swarauto.util;

import android.support.test.runner.AndroidJUnit4;

import com.swarauto.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public final class ImageUtilTest extends BaseTest {

    @Test
    public void testContainsMethodNegative_byInvalidTemplate() {
        String sourceFile = getTempFilePath("sampleScreen.png");
        String templateFile = getTempFilePath("invalidTemplate.png");
        Assert.assertNull(ImageUtil.contains(sourceFile, templateFile, 99));
    }

    @Test
    public void testContainsMethodNegative_byValidTemplate1_butThresholdMoreThan100() {
        String sourceFile = getTempFilePath("sampleScreen.png");
        String templateFile = getTempFilePath("validTemplate1.png");
        Assert.assertNull(ImageUtil.contains(sourceFile, templateFile, 101));
    }

    @Test
    public void testContainsMethodNegative_byValidTemplate2_butThresholdMoreThan100() {
        String sourceFile = getTempFilePath("sampleScreen.png");
        String templateFile = getTempFilePath("validTemplate2.png");
        Assert.assertNull(ImageUtil.contains(sourceFile, templateFile, 101));
    }

    @Test
    public void testContainsMethodPositive_byValidTemplate1() {
        String sourceFile = getTempFilePath("sampleScreen.png");
        String templateFile = getTempFilePath("validTemplate1.png");
        Assert.assertNotNull(ImageUtil.contains(sourceFile, templateFile, 99));
    }

    @Test
    public void testContainsMethodPositive_byValidTemplate2() {
        String sourceFile = getTempFilePath("sampleScreen.png");
        String templateFile = getTempFilePath("validTemplate2.png");
        Assert.assertNotNull(ImageUtil.contains(sourceFile, templateFile, 99));
    }
}
