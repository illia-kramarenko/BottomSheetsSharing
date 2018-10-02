package com.example.bottomsheets;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Intent.ACTION_SEND;

public class ShareActivity extends AppCompatActivity {

    private View bottomSheet;
    private View root;
    private RecyclerView list;
    private ProgressBar progressBar;

    private BottomSheetBehavior bottomSheetBehavior;

    private ShareAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setStatusBarColor(Color.WHITE, 0xc4c4c4, true);

        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
        setContentView(R.layout.activity_share);
        list = (RecyclerView) findViewById(R.id.list);
        bottomSheet = findViewById(R.id.bottom_sheet);
        root = findViewById(R.id.root);
        progressBar = findViewById(R.id.progress);

        initBottomSheet();
        initList();
        loadList();

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheet.post(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        // play hide animation and finish activity in listener
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }

    private void initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);

        int minHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
        bottomSheetBehavior.setPeekHeight(minHeight);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        list.setMinimumHeight(minHeight);
    }

    private void initList() {
        adapter = new ShareAdapter();
        list.setAdapter(adapter);
        list.setLayoutManager(new GridLayoutManager(this, 4));
        list.setItemAnimator(new DefaultItemAnimator());
        list.setNestedScrollingEnabled(false);
    }

    private void loadList() {
        progressBar.setVisibility(View.VISIBLE);
        Disposable disposable = Single.fromCallable(new Callable<List<ShareModel>>() {
            @Override
            public List<ShareModel> call() {
                PackageManager packageManager = getPackageManager();

                // the basic intent used to query all the activities that can support
                // text sharing
                Intent basicIntent = new Intent(ACTION_SEND);
                basicIntent.setType("text/plain");
                List<ResolveInfo> resInfo = packageManager.queryIntentActivities(basicIntent, 0);

                List<Intent> intents = new ArrayList<>();
                List<ShareModel> shareModels = new ArrayList<>();
                for (int i = 0; i < resInfo.size(); i++) {
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;

                    Intent realIntent = getBasicRealIntent(packageName, ri);

                    //By default (for all unknown/unpredictable apps), use just email version of share,
                    //that contains subject and text;
                    realIntent.setType("text/plain");
                    realIntent.putExtra(Intent.EXTRA_SUBJECT, "My subject");
                    realIntent.putExtra(Intent.EXTRA_TEXT, "My text foo bar baz");

                    if (ri.activityInfo.exported) {
                        intents.add(realIntent);
                        ShareModel shareModel = new ShareModel();
                        shareModel.setName(ri.loadLabel(packageManager).toString());
                        shareModel.setIcon(ri.activityInfo.loadIcon(packageManager));
                        shareModel.setIcon(ri.loadIcon(packageManager));

                        shareModels.add(shareModel);
                    }
                }
                shareModels.addAll(new ArrayList<>(shareModels));
                return shareModels;
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ShareModel>>() {
                    @Override
                    public void accept(List<ShareModel> shareModels) {
                        progressBar.setVisibility(View.GONE);
                        adapter.setData(shareModels);

                    }
                });
    }

    private Intent getBasicRealIntent(String packageName, ResolveInfo resolveInfo) {
        Intent realIntent = new Intent();
        realIntent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
        realIntent.setAction(ACTION_SEND);
        realIntent.setPackage(packageName);
        return realIntent;
    }

//    public void setStatusBarColor(@ColorInt int statusBarColor) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            } else {
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            }
//            window.setStatusBarColor(statusBarColor);
//        }
//    }
//
//    /**
//     * This method sets the color of the status bar on Android 5.0+ and also allows you
//     * to make dark status bar icons on Android 6.0+.
//     * It also provides the ability to select different status bar colors for Android 5 and Android 6 mainly
//     * because of case with the white status bar color.
//     * In such case you can have solid white status bar with dark icons on Marshmallow, but you'll have
//     * to use a different status bar color for android 5.0 and 5.1 because on those versions
//     * status bar icons will always be white.
//     *
//     * @param colorMarshmallow       status bar color used on Marshmallow and higher
//     * @param colorPreMarshmallow    status bar color used on Lollipop
//     * @param darkIconsOnMarshmallow set this to 'true' to make status bar icons dark on Android Marshmallow
//     */
//    public void setStatusBarColor(@ColorInt int colorMarshmallow,
//                                  @ColorInt int colorPreMarshmallow,
//                                  boolean darkIconsOnMarshmallow) {
//        View view = getWindow().getDecorView();
//        boolean isMarshmallow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
//        if (isMarshmallow && view != null) {
//            if (darkIconsOnMarshmallow) {
//                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            } else {
//                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            }
//        }
//        setStatusBarColor(isMarshmallow ? colorMarshmallow : colorPreMarshmallow);
//    }
}
