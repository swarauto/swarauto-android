package com.swarauto;

import com.swarauto.util.AndroidUtil;

public class AndroidSettings extends Settings {
    @Override
    public String getHomeFolderPath() {
        return AndroidUtil.getHomeDir().getAbsolutePath();
    }
}
