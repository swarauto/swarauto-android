package com.akivamu.lib.android.floatingwindow;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public abstract class FloatingWindow extends StandOutWindow {
    protected static final String TAG = FloatingWindow.class.getSimpleName();
    private static final int DOCKING_OFFSET_DP = 10;
    private static final int DOCKING_UNDOCKTHRESHOLD_DP = 20;

    protected int curOrientation = Configuration.ORIENTATION_PORTRAIT;

    private DockConfig dockConfig = new DockConfig();

    // Config
    public abstract View inflateWindow(FrameLayout frame);

    public abstract int getWindowId();

    public abstract int getWindowInitialWidth();

    public abstract int getWindowInitialHeight();


    public abstract int getWindowRootViewId();

    // Dockable
    public abstract int getIconDockViewId();

    public abstract int getIconDockWidthDp();

    public abstract int getIconDockHeightDp();


    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        dockConfig.isDocked = false;
        dockConfig.beforeDockWindowWidth = 0;
        dockConfig.beforeDockWindowHeight = 0;

        // Get current orientation
        curOrientation = getResources().getConfiguration().orientation;

        dockConfig.icDockWidth = (int) convertDpToPixel(getIconDockWidthDp());
        dockConfig.icDockHeight = (int) convertDpToPixel(getIconDockHeightDp());

        View rootView = inflateWindow(frame);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");

        super.onConfigurationChanged(newConfig);

        getUniqueId();
        final Window window = getWindow(getWindowId());
        if (window == null) {
            return;
        }

        // orientation changed
        if (newConfig.orientation != curOrientation) {

            curOrientation = newConfig.orientation;

            // get new screen w + h
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int mMaxW = metrics.widthPixels;
            int mMaxH = (int) (metrics.heightPixels - 25 * metrics.density);

            window.displayWidth = mMaxW;
            window.displayHeight = mMaxH;

            final StandOutLayoutParams params = (StandOutLayoutParams) window
                    .getLayoutParams();

            // Reposition dock icon
            if (dockConfig.isDocked) {
                if (params.y + params.height > mMaxH) {
                    params.y = mMaxH - params.height;
                    updateViewLayout(getWindowId(), params);
                }
            }

            // Resize main window
            else {
                if (params.width > mMaxW) {
                    params.width = mMaxW - 50;
                }
                if (params.height > mMaxH) {
                    params.height = mMaxH - 50;
                }

                // Center main window
                params.x = (mMaxW - params.width) / 2;
                params.y = (mMaxH - params.height) / 2;

                updateViewLayout(getWindowId(), params);
            }

        }

    }

    protected void dock() {
        Window window = getWindow(getWindowId());

        // Determine position
        final StandOutLayoutParams params = window.getLayoutParams();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();

        if (params.x < screenWidth / 2) {
            dockConfig.dockPosition = DockConfig.LEFT;
        } else {
            dockConfig.dockPosition = DockConfig.RIGHT;
        }

        if (!dockConfig.isDocked) {
            dockConfig.beforeDockWindowWidth = params.width;
            dockConfig.beforeDockWindowHeight = params.height;
        }

        final View mainWindow = window.findViewById(getWindowRootViewId());
        final View dockIcon = window.findViewById(getIconDockViewId());

        ViewGroup.LayoutParams layoutParams = dockIcon.getLayoutParams();
        layoutParams.width = dockConfig.icDockWidth;
        layoutParams.height = dockConfig.icDockHeight;
        dockIcon.setLayoutParams(layoutParams);

        mainWindow.setVisibility(View.GONE);
        dockIcon.setVisibility(View.VISIBLE);

        if (dockConfig.dockPosition == DockConfig.LEFT) {
            params.x = -(int) convertDpToPixel(DOCKING_OFFSET_DP);
        } else if (dockConfig.dockPosition == DockConfig.RIGHT) {
            params.x = (int) (screenWidth - dockConfig.icDockWidth + convertDpToPixel(DOCKING_OFFSET_DP));
        }

        if (!dockConfig.isDocked) {
            params.width = dockConfig.icDockWidth;
            params.height = dockConfig.icDockHeight;
        }

        updateViewLayout(getWindowId(), params);
        dockConfig.isDocked = true;
    }

    private void unDock() {
        Window window = getWindow(getWindowId());
        final StandOutLayoutParams params = window.getLayoutParams();
        final View mainWindow = window.findViewById(getWindowRootViewId());
        final View dockIcon = window.findViewById(getIconDockViewId());

        mainWindow.setVisibility(View.VISIBLE);
        dockIcon.setVisibility(View.GONE);

        // Resize exceed window size when restore
        if (dockConfig.beforeDockWindowWidth > window.displayWidth) {
            dockConfig.beforeDockWindowWidth = window.displayWidth - 25;
        }
        if (dockConfig.beforeDockWindowHeight > window.displayHeight) {
            dockConfig.beforeDockWindowHeight = window.displayHeight - 25;
        }


        params.width = dockConfig.beforeDockWindowWidth;
        params.height = dockConfig.beforeDockWindowHeight;

        // Move to show full window
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();

        if (params.x < 0) {
            params.x = 0;
        }
        if (params.x + params.width > screenWidth) {
            params.x = screenWidth - params.width;
        }

        updateViewLayout(getWindowId(), params);
        dockConfig.isDocked = false;
    }

    @Override
    public boolean onFocusChange(int id, Window window, boolean focus) {
        if (!focus && !dockConfig.isDocked) {
            dock();
        }

        return super.onFocusChange(id, window, focus);
    }

    @Override
    public boolean onTouchBody(final int id, final Window window,
                               final View view, MotionEvent event) {

        // Only dock/undock when up
        if (event.getAction() != MotionEvent.ACTION_UP)
            return false;


        // Prepare variable
        final StandOutLayoutParams params = window.getLayoutParams();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();


        // Docking if not docked
        if (!dockConfig.isDocked) {
            if (params.x <= 0 || params.x + params.width >= screenWidth)
                dock();
        }
        // Undock
        else {
            if (params.x > convertDpToPixel(DOCKING_UNDOCKTHRESHOLD_DP) && params.x + params.width < screenWidth - convertDpToPixel(DOCKING_UNDOCKTHRESHOLD_DP)) {
                unDock();
            } else dock();
        }

        return false;
    }

    // the window will be centered
    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, getWindowInitialWidth(), getWindowInitialHeight(),
                StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER, getWindowInitialWidth() / 2,
                getWindowInitialHeight() / 2);
    }

    // move the window by dragging the view
    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE
//                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE
//                | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE
//                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
//                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH
                ;
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "Tap to close";
    }


}
