package com.example.dmitry.palette;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

public class GridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private GridView mGridView;
    private GridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        mGridView = (GridView) findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);
        if(requestPerm())
            mAdapter.setPhotos(getImages());

    }
    public boolean requestPerm(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Приехали")
                        .setMessage("Разреши доступ к файликами твоим. Дабы свершить это, следуй в настройки. Ступай")
                        .setCancelable(false)
                        .setPositiveButton("Окай",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        return true;
    }
    public Bitmap getThumbnail(long imageId){
        return MediaStore.Images.Thumbnails.getThumbnail(
                getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null);
    }

    public ArrayList<Photo> getImages(){
        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA
        };
        // content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.i("ListingImages"," query count="+cur.getCount());
        ArrayList<Photo> photos = new ArrayList<>();
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);

                // Do something with the values.
                Log.i("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + date);
                Photo photo = new Photo(cur.getInt(cur.getColumnIndex(MediaStore.Images.Media._ID)),
                        cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA)));
                photos.add(photo);
            } while (cur.moveToNext());

        }
        cur.close();
        return photos;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mAdapter.setPhotos(getImages());

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("НУ епта!")
                            .setMessage("хуль не дал?!")
                            .setCancelable(false)
                            .setPositiveButton("Так получилось…",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            onNegative();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    private void onNegative(){
        finish();
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<Photo> photos;

        public void setPhotos(ArrayList<Photo> photos) {
            this.photos = photos;
            notifyDataSetChanged();
        }

        public ArrayList<Photo> getPhotos() {
            return photos;
        }

        @Override
        public int getCount() {
            if(photos == null)
                return 0;
            return photos.size();
        }

        @Override
        public Photo getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            final Photo item = photos.get(position);

            // Load the thumbnail image
            ImageView image = (ImageView) view.findViewById(R.id.imageview_item);

            image.setImageBitmap(getThumbnail(item.getId()));

            return view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Photo item = (Photo) adapterView.getItemAtPosition(position);

        // Construct an Intent as normal
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(DetailActivity.SELECTED_NUM, item.getId());

        // BEGIN_INCLUDE(start_activity)
        //ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
        //        this,
//
        //        // Now we provide a list of Pair items which contain the view we can transitioning
        //        // from, and the name of the view it is transitioning to, in the launched activity
        //        new Pair<View, String>(view.findViewById(R.id.imageview_item),
        //                DetailActivity.VIEW_NAME_HEADER_IMAGE),
        //        new Pair<View, String>(view.findViewById(R.id.textview_name),
        //                DetailActivity.VIEW_NAME_HEADER_TITLE));
//
        //ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        bundle.putParcelable(DetailActivity.PHOTO_LIST, mAdapter.getPhotos().get(position));
        intent.putParcelableArrayListExtra(DetailActivity.PHOTO_LIST, mAdapter.getPhotos());
        intent.putExtra(DetailActivity.SELECTED_NUM, position);
        startActivity(intent);
    }
}
