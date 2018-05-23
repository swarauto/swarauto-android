package com.swarauto.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akivamu.lib.android.floatingwindow.FloatingWindow;
import com.swarauto.MainActivity;
import com.swarauto.R;
import com.swarauto.Settings;
import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.ui.MainWindow;
import com.swarauto.util.AndroidOcrUtil;
import com.swarauto.util.OcrUtil;

import java.util.ArrayList;
import java.util.List;

import static com.swarauto.MyApp.appContext;

public class FragMain extends Fragment {
    protected MainActivity mainActivity;

    private Settings settings;
    private ProfileManager profileManager;
    private OcrUtil ocrUtil;

    private final List<String> profileIds = new ArrayList<String>();
    private final List<String> profileNames = new ArrayList<String>();

    // View - Account info
    private TextView textVersion;

    // View - System info
    private TextView textOcrStatus;
    private TextView textProfileStatus;
    private TextView textFloatingWindowStatus;
    private Button btnRecheck;
    private boolean error = false;

    // View - Open window
    private View openWindowContainer;
    private Spinner comboBoxProfiles;
    private Button btnBegin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        settings = DependenciesRegistry.settings;
        profileManager = DependenciesRegistry.profileManager;
        ocrUtil = DependenciesRegistry.ocrUtil;

        return inflater.inflate(R.layout.frag_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View - Account info
        textVersion = view.findViewById(R.id.textVersion);

        // View - System info
        textOcrStatus = view.findViewById(R.id.textOcrStatus);
        textProfileStatus = view.findViewById(R.id.textProfileStatus);
        textFloatingWindowStatus = view.findViewById(R.id.textFloatingWindowStatus);
        btnRecheck = view.findViewById(R.id.btnRecheck);
        btnRecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recheck();
            }
        });

        // View - Open window
        openWindowContainer = view.findViewById(R.id.openWindowContainer);
        comboBoxProfiles = view.findViewById(R.id.comboBoxProfiles);
        btnBegin = view.findViewById(R.id.btnBegin);
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWindow();
            }
        });
    }

    private void renderVersion() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            textVersion.setText("v" + pInfo.versionName + " (Build: " + pInfo.versionCode + ")");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        renderVersion();

        refreshSystemRequirements();
    }

    private void recheck() {
        AndroidOcrUtil ocrUtil = (AndroidOcrUtil) this.ocrUtil;
        ocrUtil.reinitialize();

        mainActivity.checkPermissions();

        refreshSystemRequirements();
    }

    private void refreshSystemRequirements() {
        error = false;

        // OCR
        String ocr = "";
        AndroidOcrUtil ocrUtil = (AndroidOcrUtil) this.ocrUtil;
        if (ocrUtil.isInitialized()) {
            ocr += "OK";
        } else {
            ocr += "ERROR: Please contact developer.";
            ocr += "\nThis feature is optional. But SWarAuto can only either PICK ALL RUNES or SELL ALL RUNES.";
        }
        textOcrStatus.setText(ocr);

        // Profile
        String profiles = "";
        loadProfiles();
        if (profileIds.size() > 0) {
            profiles += "OK: Loaded " + profileIds.size() + " device configs.";

            // Apply data to combobox
            List<String> options = new ArrayList<String>();
            for (int i = 0; i < profileIds.size(); i++) {
                options.add(profileIds.get(i) + " - " + profileNames.get(i));
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(appContext, android.R.layout.simple_spinner_item, options);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            comboBoxProfiles.setAdapter(dataAdapter);
        } else {
            profiles += "ERROR: No device config found"
                    + "\nMake sure to copy from PC to /sdcard/swarauto/profiles"
                    + "\nAnd permission Access Storage is granted for SWarAuto.";
            error = true;
        }

        textProfileStatus.setText(profiles);

        // Floating window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.canDrawOverlays(getActivity())) {
            textFloatingWindowStatus.setText("ERROR: Permission not granted."
                    + "\nSWarAuto display floating window. Please allow permission in App Settings.");
            error = true;
        } else {
            textFloatingWindowStatus.setText("OK.");
        }

        openWindowContainer.setVisibility(error ? View.GONE : View.VISIBLE);
        btnRecheck.setVisibility(error ? View.VISIBLE : View.GONE);

        if (error)
            Toast.makeText(getActivity(), "There are errors. Please fix.", Toast.LENGTH_SHORT).show();
    }

    private void loadProfiles() {
        profileIds.clear();
        profileNames.clear();
        profileIds.addAll(profileManager.getProfileIds());
        for (String id : profileIds) {
            Profile profile = profileManager.loadProfile(id);
            if (profile != null) {
                profileNames.add(profile.getName());
            }
        }
    }

    private void openWindow() {
        FloatingWindow.closeAll(mainActivity, MainWindow.class);
        FloatingWindow.show(mainActivity, MainWindow.class, MainWindow.ID);
        MainWindow.setProfileId(profileIds.get(comboBoxProfiles.getSelectedItemPosition()));
        mainActivity.finish();
    }
}
