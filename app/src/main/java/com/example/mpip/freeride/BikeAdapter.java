package com.example.mpip.freeride;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mpip.freeride.domain.Bike;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BikeAdapter extends BaseAdapter {
    private Context mContext;
    private final Bike[] bikes;
    LayoutInflater inflter;

    public BikeAdapter(Context mContext, Bike[] bikes){
        this.mContext = mContext;
        this.bikes = bikes;
        inflter = (LayoutInflater.from(mContext));
    }
    @Override
    public int getCount() {
        return bikes.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.gridview_item, null); // inflate the layout

        Bitmap bitmap = bikes[position].getImageUrl();
     /*   try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(bitmap); // set logo images
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(bikes[position].getName());
        return convertView;
    }
}
