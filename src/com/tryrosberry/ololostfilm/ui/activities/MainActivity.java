package com.tryrosberry.ololostfilm.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.tryrosberry.ololostfilm.LostFilmApp;
import com.tryrosberry.ololostfilm.R;
import com.tryrosberry.ololostfilm.debug.DebugHandler;
import com.tryrosberry.ololostfilm.imagefatcher.ImageCache;
import com.tryrosberry.ololostfilm.imagefatcher.ImageFetcher;
import com.tryrosberry.ololostfilm.logic.storage.ConstantStorage;
import com.tryrosberry.ololostfilm.logic.storage.Settings;
import com.tryrosberry.ololostfilm.ui.fragments.NewsFragment;
import com.tryrosberry.ololostfilm.ui.fragments.RssFragment;
import com.tryrosberry.ololostfilm.ui.fragments.SerialFragment;
import com.tryrosberry.ololostfilm.ui.fragments.SuperAwesomeCardFragment;

public class MainActivity extends SherlockFragmentActivity {

    private String[] TITLES = new String[]{};
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private ImageFetcher mImageFetcher;

    private final Handler handler = new Handler();

    private PagerSlidingTabStrip mTabs;
    private ViewPager mPager;
    private StripePagerAdapter mAdapter;

    private Drawable oldBackground = null;
    private int currentColor = 0xFF666666;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Settings mSettings = LostFilmApp.getInstance().getSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        TITLES = getResources().getStringArray(R.array.tab_titles);

        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();

        //Tabs code
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new StripePagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, getResources().getDisplayMetrics());
        mPager.setPageMargin(pageMargin);

        mTabs.setViewPager(mPager);
        mTabs.setOnPageChangeListener(pageChangeListener());

        changeColor(currentColor);

        //Drawer code

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(getBaseContext(),R.layout.drawer_list_item, TITLES));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {
                //super.onDrawerClosed(drawerView);
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        final String bug = mSettings.loadCrash();
        if(LostFilmApp.USER_DEBUGGING){
            if(!bug.equals("") /*&& !isBugDialogShown*/){
                /*isBugDialogShown = true;*/
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.bug_report_title)
                        .setMessage(R.string.bug_report_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DebugHandler.sendReport(MainActivity.this, bug, ConstantStorage.DEBUG_EMAIL_TO, false);
                                LostFilmApp.getInstance().getSettings().deleteLastCrash();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LostFilmApp.getInstance().getSettings().deleteLastCrash();
                                dialog.cancel();
                            }
                        }).show();
            }
        } else{
            if(!bug.equals("")) {
                showMessage("Bug",bug);
                LostFilmApp.getInstance().getSettings().deleteLastCrash();
            }
        }

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);


    }

    public void setProgressVisibility(boolean progress){
        if(progress)setProgressBarIndeterminateVisibility(Boolean.TRUE);
        else setProgressBarIndeterminateVisibility(Boolean.FALSE);
    }

    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }


    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        /*Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(android.R.id.content, fragment)
                .addToBackStack(null).commit();*/


        mPager.setCurrentItem(position);


        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(TITLES[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public ViewPager.OnPageChangeListener pageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                mDrawerList.setItemChecked(position, true);
                setTitle(TITLES[position]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    public class StripePagerAdapter extends FragmentPagerAdapter {

        public StripePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return NewsFragment.newInstance(position);
                case 1: return SerialFragment.newInstance(position);
                case 2: return RssFragment.newInstance(position);
                default: return SuperAwesomeCardFragment.newInstance(position);
            }
        }

    }

    private void changeColor(int newColor) {

        mTabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

}
