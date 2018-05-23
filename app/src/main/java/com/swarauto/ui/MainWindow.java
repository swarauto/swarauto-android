package com.swarauto.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akivamu.lib.android.floatingwindow.FloatingWindow;
import com.swarauto.R;
import com.swarauto.Settings;
import com.swarauto.SoldRunesActivity;
import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.GameState;
import com.swarauto.game.profile.CommonConfig;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.session.AutoSession;
import com.swarauto.ui.main.MainPresenter;
import com.swarauto.ui.main.MainView;
import com.swarauto.util.AndroidUtil;
import com.swarauto.util.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import wei.mark.standout.ui.Window;

public class MainWindow extends FloatingWindow implements MainView {
    protected static final String TAG = MainWindow.class.getSimpleName();
    public static final int ID = 0xcafe;
    @Setter
    private static String profileId = null;

    private Settings settings;
    private Handler handler;

    private int initialWindowWidth;
    private int initialWindowHeight;

    private MainPresenter presenter;
    private List<String> sessionTypeList;
    private int selectedSessionTypeIndex = 0;
    private int selectedMinRarityIndex = 0;
    private int selectedMinGradeIndex = 0;

    // Dock
    private ImageView iconDock;
    private TextView runCountView;

    // Views - title
    private TextView textTitle;
    private ImageView btnInfo;

    // Views - session
    private Button btnSelectSessionType;
    private EditText textMaxRefills;
    private EditText textMaxRuns;
    private CheckBox checkboxRecordRuneStoneSelling;
    private Button btnShowSoldRunes;
    private Button btnSelectMinRarity;
    private Button btnSelectMinGrade;
    private Button btnStart;
    private Button btnStop;

    @Override
    public void onCreate() {
        super.onCreate();

        settings = DependenciesRegistry.settings;
        handler = new Handler();

        Rectangle screenSize = AndroidUtil.getScreenRectangle();
        initialWindowWidth = (int) (Math.min(screenSize.width, screenSize.height) * 0.8f);
        initialWindowHeight = initialWindowWidth;

        presenter = new MainPresenter();
        presenter.bindView(this);

    }

    @Override
    public View inflateWindow(FrameLayout frame) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_window, frame, true);

        iconDock = view.findViewById(R.id.iconDock);
        runCountView = view.findViewById(R.id.runCount);

        // Title bar
        textTitle = view.findViewById(R.id.textTitle);
        btnInfo = view.findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnInfoClicked();
            }
        });

        // Session
        btnSelectSessionType = view.findViewById(R.id.btnSelectSessionType);
        btnSelectSessionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChooseSessionType();
            }
        });

        textMaxRefills = view.findViewById(R.id.refillTimes);
        textMaxRuns = view.findViewById(R.id.maxRuns);

        checkboxRecordRuneStoneSelling = view.findViewById(R.id.checkboxRecordRuneStoneSelling);
        btnShowSoldRunes = view.findViewById(R.id.btnShowSoldRunes);
        btnShowSoldRunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnShowSoldRunesClicked();
            }
        });

        btnSelectMinRarity = view.findViewById(R.id.btnSelectMinRarity);
        btnSelectMinRarity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChooseMinRarity();
            }
        });
        btnSelectMinGrade = view.findViewById(R.id.btnSelectMinGrade);
        btnSelectMinGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChooseMinGrade();
            }
        });

        btnStart = view.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnStartClicked();
            }
        });
        btnStop = view.findViewById(R.id.btnStop);
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBtnStopClicked();
            }
        });

        presenter.refreshCommonConfig();
        presenter.refreshProfileList();
        presenter.refreshSessionTypeList();

        if (profileId == null) {
            Toast.makeText(this, "Invalid profile", Toast.LENGTH_SHORT).show();
            stopSelf();
        } else {
            presenter.onProfileSelected(profileId);
        }

        return view;
    }

    private void showDialogChooseSessionType() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = sessionTypeList.toArray(new CharSequence[0]);

        builder.setTitle("Select type")
                .setSingleChoiceItems(array, selectedSessionTypeIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSessionTypeIndex = position;
                presenter.onSessionTypeSelected(selectedSessionTypeIndex);
                btnSelectSessionType.setText(sessionTypeList.get(selectedSessionTypeIndex));
                dialog.dismiss();
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

    }

    private void showDialogChooseMinRarity() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = CommonConfig.RunePickingRarity.getTexts().toArray(new CharSequence[0]);

        builder.setTitle("Select min RARITY")
                .setSingleChoiceItems(array, selectedMinRarityIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMinRarityIndex = position;
                btnSelectMinRarity.setText(CommonConfig.RunePickingRarity.getTexts().get(selectedMinRarityIndex));
                dialog.dismiss();
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    private void showDialogChooseMinGrade() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] array = CommonConfig.RunePickingGrade.getTexts().toArray(new CharSequence[0]);

        builder.setTitle("Select min GRADE")
                .setSingleChoiceItems(array, selectedMinGradeIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMinGradeIndex = position;
                btnSelectMinGrade.setText(CommonConfig.RunePickingGrade.getTexts().get(selectedMinGradeIndex));
                dialog.dismiss();
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @Override
    public int getWindowId() {
        return ID;
    }

    @Override
    public int getWindowInitialWidth() {
        return initialWindowWidth;
    }

    @Override
    public int getWindowInitialHeight() {
        return initialWindowHeight;
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
//        return StandOutWindow.getCloseIntent(this, MainWindow.class, id);
        return null;
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "";
    }

    @Override
    public int getIconDockViewId() {
        return R.id.dockView;
    }

    @Override
    public int getIconDockWidthDp() {
        return 60;
    }

    @Override
    public int getIconDockHeightDp() {
        return 60;
    }

    @Override
    public int getWindowRootViewId() {
        return R.id.mainWindow;
    }

    @Override
    public void onResize(int id, Window window, View view, MotionEvent event) {
        super.onResize(id, window, view, event);
    }

    @Override
    public List<DropDownListItem> getDropDownItems(final int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();


//        items.add(new DropDownListItem(R.drawable.ic_running,
//                "Exit", new Runnable() {
//
//            @Override
//            public void run() {
//            }
//        }));

        return items;
    }

    @Override
    public String getAppName() {
        return getResources().getString(R.string.app_name);
    }

    @Override
    public int getAppIcon() {
        return R.mipmap.ic_launcher;
    }

    @Override
    public boolean onClose(int id, Window window) {
        presenter.onBtnStopClicked();
        return super.onClose(id, window);
    }

    private void setDockIcon(final int rid) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                iconDock.setImageResource(rid);
            }
        });
    }

    @Override
    public void create() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void renderProfileList(List<String> profileList) {
        // DO NOTHING
    }

    @Override
    public void renderSessionTypeList(List<String> sessionTypeList) {
        this.sessionTypeList = sessionTypeList;
        btnSelectSessionType.setText(sessionTypeList.get(selectedSessionTypeIndex));
        presenter.onSessionTypeSelected(selectedSessionTypeIndex);
    }

    @Override
    public void renderCommonConfig(CommonConfig commonConfig) {
        textMaxRefills.setText(String.valueOf(commonConfig.getMaxRefills()));
        textMaxRuns.setText(String.valueOf(commonConfig.getMaxRuns()));

        checkboxRecordRuneStoneSelling.setChecked(commonConfig.isRecordSoldRunes());

        {
            CommonConfig.RunePickingRarity rarity = commonConfig.getRunePickingMinRarity();
            if (rarity == null) {
                rarity = CommonConfig.RunePickingRarity.RARITY_ALL;
            }
            selectedMinRarityIndex = CommonConfig.RunePickingRarity.indexOf(rarity);
            commonConfig.setRunePickingMinRarity(rarity);
            btnSelectMinRarity.setText(rarity.getText());
        }

        {
            CommonConfig.RunePickingGrade grade = commonConfig.getRunePickingMinGrade();
            if (grade == null) {
                grade = CommonConfig.RunePickingGrade.GRADE_ALL;
            }
            selectedMinGradeIndex = CommonConfig.RunePickingGrade.indexOf(grade);
            commonConfig.setRunePickingMinGrade(grade);
            btnSelectMinGrade.setText(grade.getText());
        }
    }

    @Override
    public void renderUIForSessionState(final AutoSession.State sessionState) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (sessionState) {
                    case INIT:
                        btnStart.setText("Start");
                        btnStop.setEnabled(false);
                        setDockIcon(R.drawable.ic_init);
                        break;
                    case RUNNING:
                        dock();
                        btnStart.setText("Pause");
                        btnStop.setEnabled(true);
                        setDockIcon(R.drawable.ic_running);
                        break;
                    case PAUSED:
                        btnStart.setText("Resume");
                        btnStop.setEnabled(true);
                        setDockIcon(R.drawable.ic_paused);
                        break;
                    case STOPPED:
                        btnStart.setText("Start");
                        btnStop.setEnabled(false);
                        setDockIcon(R.drawable.ic_init);
                        break;
                }
            }
        });
    }

    @Override
    public void renderSessionReport(final AutoSession.Report report) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                runCountView.setText(report.getSuccessRuns() + "/" + report.getCompletedRuns());
            }
        });
    }

    @Override
    public void renderGameState(final GameState gameState) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (gameState) {
                    case IN_BATTLE:
                        setDockIcon(R.drawable.ic_battle);
                        break;
                    case UNKNOWN:
                        setDockIcon(R.drawable.ic_unknown);
                        break;
                    default:
                        setDockIcon(R.drawable.ic_running);
                        break;
                }
            }
        });
    }

    @Override
    public void renderError(final String error) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainWindow.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void renderMessage(final String message) {
        Log.d(TAG, message);
    }

    @Override
    public CommonConfig getCommonConfig() {
        final CommonConfig commonConfig = new CommonConfig();

        int maxRefills = 0;
        try {
            maxRefills = Integer.parseInt(textMaxRefills.getText().toString());
        } catch (NumberFormatException ignored) {
        }
        commonConfig.setMaxRefills(maxRefills);

        int maxRuns = Integer.MAX_VALUE;
        try {
            maxRuns = Integer.parseInt(textMaxRuns.getText().toString());
        } catch (NumberFormatException ignored) {
        }
        commonConfig.setMaxRuns(maxRuns);

        commonConfig.setRunePickingMinRarity(CommonConfig.RunePickingRarity.byIndex(selectedMinRarityIndex));
        commonConfig.setRunePickingMinGrade(CommonConfig.RunePickingGrade.byIndex(selectedMinGradeIndex));
        commonConfig.setRecordSoldRunes(checkboxRecordRuneStoneSelling.isChecked());

        handler.post(new Runnable() {
            @Override
            public void run() {
                textMaxRefills.setText(String.valueOf(commonConfig.getMaxRefills()));
                textMaxRuns.setText(String.valueOf(commonConfig.getMaxRuns()));
            }
        });

        return commonConfig;
    }

    @Override
    public void renderProfileCheckerError(final String error) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainWindow.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showInfoDialog(final AutoSession.Report sessionReport) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainWindow.this);
                builder.setTitle("Info");

                StringBuilder sb = new StringBuilder();

                if (sessionReport != null) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append("Current session:");
                    sb.append("\n - Refills done: ").append(sessionReport.getRefillTimes());
                    sb.append("\n - Completed runs: ").append(sessionReport.getCompletedRuns());
                    sb.append("\n - Victory runs: ").append(sessionReport.getSuccessRuns())
                            .append("/").append(sessionReport.getCompletedRuns());
                }

                if (sb.length() == 0) sb.append("No info yet");

                builder.setMessage(sb.toString());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        });
    }

    @Override
    public void renderSelectedProfile(Profile profile) {
        textTitle.setText(profile.getName());
    }

    @Override
    public void showSoldRunes() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                File soldRunesDir = new File(settings.getSoldRunesFolderPath());
                soldRunesDir.mkdirs();

                Intent intent = new Intent(MainWindow.this, SoldRunesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
