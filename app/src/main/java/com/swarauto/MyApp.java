package com.swarauto;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.util.AndroidCommandUtil;
import com.swarauto.util.AndroidOcrUtil;

public class MyApp extends MultiDexApplication {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        initDependencies();
    }

    private void initDependencies() {
        DependenciesRegistry.commandUtil = new AndroidCommandUtil();
        DependenciesRegistry.settings = new AndroidSettings();
        DependenciesRegistry.profileManager = new ProfileManager();
        DependenciesRegistry.profileManager.setLocation(DependenciesRegistry.settings.getProfilesFolderPath());

        AndroidOcrUtil ocrUtil = new AndroidOcrUtil();
        ocrUtil.initialize();
        DependenciesRegistry.ocrUtil = ocrUtil;
    }
}
