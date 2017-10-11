package com.rR.apksapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rR.apksapp.adapters.DashboardAdapter;
import com.rR.apksapp.models.AppsApkDetails;
import com.rR.apksapp.utils.AppConstants;
import com.rR.apksapp.utils.ItemOffsetDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    private final String TAG = Dashboard.class.getName();

    File mInsideFolder;
    ArrayList<AppsApkDetails> mArrayList = new ArrayList<>();
    ArrayList<Uri> uris = new ArrayList<>();

    LinearLayout mBottomBtnLayout;
    Button mSaveAndShareBtn;
    Button mShareBtn;
    Button mSaveBtn;
    ProgressBar progressBar;
    RecyclerView mRecyclerView;
    DashboardAdapter mDashboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mBottomBtnLayout = (LinearLayout) findViewById(R.id.bottomBtnLayout);
        mSaveAndShareBtn = (Button) findViewById(R.id.saveAndShareBtn);
        mShareBtn = (Button) findViewById(R.id.shareBtn);
        mSaveBtn = (Button) findViewById(R.id.saveBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRecyclerView = (RecyclerView) findViewById(R.id.dashboardRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(Dashboard.this, AppConstants.GRID_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(Dashboard.this, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkStoragePermission()) {
                getAppsApkDetails();
            } else {
                requestForStoragePermission();
            }
        } else {
            getAppsApkDetails();
        }

        mSaveAndShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "selected save and share");
                saveAPKs();
                shareAPKs();
            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "selected share");
                shareAPKs();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "selected save");
                saveAPKs();
            }
        });

        /*private RippleDrawable rippleDrawable;
        private Button buttonWithRipple;

        buttonWithRipple = (Button) findViewById(R.id.buttonWithRipple);
        rippleDrawable = (RippleDrawable) buttonWithRipple.getBackground();
        buttonWithRipple.setOnTouchListener(this);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.buttonWithRipple :
                    rippleDrawable.setHotspot(event.getX(), event.getY());
                    rippleDrawable.setColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    break; }
            return false;
        }*/
    }

    private void getAppsApkDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String appName;
                File apkFile;
                Drawable drawable;

                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                List<ResolveInfo> apps = getPackageManager().
                        queryIntentActivities(mainIntent, 0);

                for (ResolveInfo info : apps) {
                    try {
                        ApplicationInfo applicationInfo = Dashboard.this.
                                getPackageManager().getApplicationInfo(
                                info.activityInfo.applicationInfo.packageName, 0);
                        appName = (String) getPackageManager().
                                getApplicationLabel(applicationInfo);
                        drawable = getPackageManager().getApplicationIcon(applicationInfo);
                        apkFile = new File(applicationInfo.publicSourceDir);

                        mArrayList.add(new AppsApkDetails(appName,
                                apkFile.getAbsolutePath(), drawable, false));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        Comparator<AppsApkDetails> appsApkDetailsComparator = new
                                Comparator<AppsApkDetails>() {
                                    @Override
                                    public int compare(AppsApkDetails appsApkDetails1,
                                                       AppsApkDetails appsApkDetails2) {
                                        return appsApkDetails1.getAppName().
                                                compareToIgnoreCase(appsApkDetails2.getAppName());
                                    }
                                };

                        Collections.sort(mArrayList, appsApkDetailsComparator);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null == mDashboardAdapter) {
                                    mDashboardAdapter = new DashboardAdapter(
                                            Dashboard.this, mArrayList);
                                    mRecyclerView.setAdapter(mDashboardAdapter);
                                    mRecyclerView.setAdapter(mDashboardAdapter);
                                } else {
                                    mDashboardAdapter.updateAdapter(Dashboard.this, mArrayList);
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void showOrHideBottomBar(boolean showOrHide) {
        if (null != mBottomBtnLayout && showOrHide) {
            mBottomBtnLayout.setVisibility(View.VISIBLE);
        } else if (null != mBottomBtnLayout && !showOrHide) {
            mBottomBtnLayout.setVisibility(View.GONE);
        }
    }

    private void saveAPKs() {
        if (checkStoragePermission()) {
            createFolders();
        } else {
            requestForStoragePermission();
        }
    }

    private void shareAPKs() {
        Log.d(TAG, "shareAPKs()");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AppsApkDetails> arrayList = mDashboardAdapter.
                        getTheSelectedAppsList();
                Log.d(TAG, "shareAPKs(), arrayList.size(): " +
                        (null != arrayList ? arrayList.size() : -1));

                if (getSelectedItemsCount(arrayList) > 0) {
                    if (null != uris)
                        uris.clear();

                    for (int i = 0; i < arrayList.size(); i++) {
                        Log.d(TAG, "shareAPKs(), getApkFilePath(): " +
                                arrayList.get(i).getApkFilePath());
                        uris.add(Uri.fromFile(new File(arrayList.get(i).
                                getApkFilePath())));
                    }
                }

                Log.d(TAG, "shareAPKs(), uris.size(): " + uris.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uris.size() > 0) {
                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            intent.setType("application/vnd.android.package-archive");
                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                                    uris);
                            startActivity(Intent.createChooser(intent, "Share " +
                                    uris.size() + " Files Via"));
                        }
                    }
                });
            }
        }).start();
    }

    private void createFolders() {
        ArrayList<AppsApkDetails> arrayList = mDashboardAdapter.
                getTheSelectedAppsList();
        Log.d(TAG, "saveAPKs(), arrayList.size(): " +
                (null != arrayList ? arrayList.size() : -1));

        if (getSelectedItemsCount(arrayList) > 0) {
            File appFolder = new File(AppConstants.FOLDER_PATH + File.separator +
                    AppConstants.FOLDER_NAME);

            mInsideFolder = new File(AppConstants.FOLDER_PATH + File.separator +
                    AppConstants.FOLDER_NAME + File.separator +
                    AppConstants.CURRENT_INSIDE_FOLDER_NAME);

            if (!appFolder.exists()) {
                appFolder.mkdirs();
            }

            if (!mInsideFolder.exists()) {
                mInsideFolder.mkdirs();
            }

            copyAPKsToCurrentFolder(mInsideFolder.getAbsolutePath(), arrayList);
        }
    }

    private void copyAPKsToCurrentFolder(final String insideFolderPath,
                                         final ArrayList<AppsApkDetails> arrayList) {
        Log.d(TAG, "copyAPKsToCurrentFolder(), insideFolderPath: " + insideFolderPath);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileChannel sourceChannel = null;
                FileChannel destinationChannel = null;

                for (int i = 0; i < arrayList.size(); i++) {
                    try {
                        File sourceFile = new File(arrayList.get(i).getApkFilePath());
                        File destinationFile = new File(insideFolderPath + File.separator +
                                sourceFile.getName());

                        if (!checkWhetherAPKIsAlreadySaved(destinationFile.
                                getAbsolutePath())) {
                            sourceChannel = new FileInputStream(sourceFile).
                                    getChannel();
                            destinationChannel = new FileOutputStream(destinationFile).
                                    getChannel();

                            destinationChannel.transferFrom(sourceChannel, 0,
                                    sourceChannel.size());
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (sourceChannel != null) {
                            try {
                                sourceChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (destinationChannel != null) {
                            try {
                                destinationChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Dashboard.this, "APKs saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private boolean checkWhetherAPKIsAlreadySaved(String apkPath) {
        File[] files = mInsideFolder.listFiles();
        boolean alreadySaved = false;

        for (File file : files) {
            if (file.getAbsolutePath().equalsIgnoreCase(apkPath)) {
                alreadySaved = true;
                break;
            }
        }

        Log.d(TAG, "checkWhetherAPKIsAlreadySaved(), alreadySaved: " + alreadySaved);
        return alreadySaved;
    }

    private int getSelectedItemsCount(ArrayList<AppsApkDetails> appsApkDetails) {
        int selectedItemsCount = 0;
        for (int i = 0; i < appsApkDetails.size(); i++) {
            if (appsApkDetails.get(i).getIsSelected()) {
                selectedItemsCount++;
            }
        }

        Log.d(TAG, "getSelectedItemsCount(), selectedItemsCount: " + selectedItemsCount);
        return selectedItemsCount;
    }

    private boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(Dashboard.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Dashboard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Dashboard.this, getString(R.string.storagePermissionToast),
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case AppConstants.REQUEST_CODE_STORAGE_PERMISSION:
                Log.d(TAG, "grantResults.length: " + grantResults.length);
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mBottomBtnLayout.getVisibility() == View.VISIBLE) {
                        createFolders();
                    }
                } else {
                    Toast.makeText(Dashboard.this, getString(R.string.storagePermissionToast),
                            Toast.LENGTH_LONG).show();
                }

                if (mBottomBtnLayout.getVisibility() == View.GONE) {
                    getAppsApkDetails();
                }
                break;
        }
    }

}