package com.example.dmitry.palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
   
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class DetailActivity extends AppCompatActivity {
    public static final String SELECTED_NUM = "selected_num";
    public static final String PHOTO_LIST = "photos";
    private static ArrayList<Photo> photos;
    private static int selectedNum;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(getIntent() != null){
            photos = getIntent().getExtras().getParcelableArrayList(PHOTO_LIST);
            selectedNum = getIntent().getIntExtra(SELECTED_NUM, 0);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(selectedNum);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PHOTO_LIST, photos);
        outState.putInt(SELECTED_NUM, selectedNum);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getParcelableArrayList(PHOTO_LIST);
        savedInstanceState.getInt(SELECTED_NUM);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            selectedNum = position;
            return PlaceholderFragment.newInstance(photos.get(position));
        }

        @Override
        public int getCount() {
            if (photos == null)
                return 0;
            return photos.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(photos == null)
                return null;
            return String.valueOf(position) + "/" + String.valueOf(photos.size());
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }


    public static class PlaceholderFragment extends Fragment {
        private Photo photo;
        private TextView vibrant;
        private TextView vibrant_dark;
        private TextView vibrant_light;
        private TextView muted;
        private TextView muted_dark;
        private TextView muted_light;
        ImageView imageView;
        final String PHOTO = "photo";

        final Target target = new Target() {
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(bitmap == null)
                    return;
                imageView.setImageBitmap(bitmap);
                palette(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
        };

        public static PlaceholderFragment newInstance(Photo photo) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.photo = photo;
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if(savedInstanceState != null)
                photo = savedInstanceState.getParcelable(PHOTO);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.image);
            vibrant = (TextView)rootView.findViewById(R.id.vibrant);
            vibrant_dark = (TextView)rootView.findViewById(R.id.vibrant_dark);
            vibrant_light = (TextView)rootView.findViewById(R.id.vibrant_light);
            muted = (TextView)rootView.findViewById(R.id.muted);
            muted_dark = (TextView)rootView.findViewById(R.id.muted_dark);
            muted_light = (TextView)rootView.findViewById(R.id.muted_light);
            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(PHOTO, photo);
        }

        @Override
        public void onStart() {
            super.onStart();
            Picasso.with(getActivity()).load(new File(photo.getData())).into(target);
        }

        @Override
        public void onStop() {
            super.onStop();
            imageView.setImageBitmap(null);
        }


        public void palette(Bitmap bmp){
            if(bmp == null)
                return;
            int defaultColor = Color.TRANSPARENT;
            Palette palette = Palette.generate(bmp);
            Palette.Swatch vibrant = palette.getVibrantSwatch();
            Palette.Swatch vibrantLight = palette.getLightVibrantSwatch();
            Palette.Swatch vibrantDark = palette.getDarkVibrantSwatch();
            Palette.Swatch muted = palette.getMutedSwatch();
            Palette.Swatch mutedLight = palette.getLightMutedSwatch();
            Palette.Swatch mutedDark = palette.getDarkMutedSwatch();

            if(vibrant != null){
                this.vibrant.setBackgroundColor(vibrant.getRgb());
                this.vibrant.setTextColor(vibrant.getTitleTextColor());
            }
            else{
                this.vibrant.setText(this.vibrant.getText() + " ERROR");
            }
            if(vibrantLight != null){
                this.vibrant_light.setBackgroundColor(vibrantLight.getRgb());
                this.vibrant_light.setTextColor(vibrantLight.getTitleTextColor());
            }
            else{
                this.vibrant_light.setText(this.vibrant_light.getText() + " ERROR");
            }
            if(vibrantDark != null){
                this.vibrant_dark.setBackgroundColor(vibrantDark.getRgb());
                this.vibrant_dark.setTextColor(vibrantDark.getTitleTextColor());
            }
            else{
                this.vibrant_dark.setText(this.vibrant_dark.getText() + " ERROR");
            }
            if(muted != null){
                this.muted.setBackgroundColor(muted.getRgb());
                this.muted.setTextColor(muted.getTitleTextColor());
            }
            else{
                this.muted.setText(this.muted.getText() + " ERROR");
            }
            if(mutedLight != null){
                this.muted_light.setBackgroundColor(mutedLight.getRgb());
                this.muted_light.setTextColor(mutedLight.getTitleTextColor());
            }
            else{
                this.muted_light.setText(this.muted_light.getText() + " ERROR");
            }
            if(mutedDark != null){
                this.muted_dark.setBackgroundColor(mutedDark.getRgb());
                this.muted_dark.setTextColor(mutedDark.getTitleTextColor());
            }
            else{
                this.muted_dark.setText(this.muted_dark.getText() + " ERROR");
            }
        }
    }
}
