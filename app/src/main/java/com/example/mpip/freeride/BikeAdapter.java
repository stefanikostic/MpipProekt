package com.example.mpip.freeride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mpip.freeride.domain.Bike;

public class BikeAdapter extends BaseAdapter {
    private Context mContext;
    private final Bike[] bikes;
    LayoutInflater inflter;
    Database db;

    public BikeAdapter(Context mContext, Bike[] bikes){
        this.mContext = mContext;
        this.bikes = bikes;
        inflter = (LayoutInflater.from(mContext));
        db=new Database(mContext);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View listing = layoutInflater.inflate(R.layout.gridview_item, parent, false);
        Bitmap bitmap = bikes[position].getImage();
     /*   try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        ImageView icon = (ImageView) listing.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(bitmap); // set logo images
        TextView textView = (TextView) listing.findViewById(R.id.textView);
        textView.setText(bikes[position].getName());
        listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=((Activity)mContext).getIntent();
                String email=intent.getStringExtra("email");
                if(db.checkMail(email)){
                    Intent i = new Intent(mContext, ClientBikeActivity.class);
                    i.putExtra("bikeId", bikes[position].getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
                else{
                    Intent i = new Intent(mContext, AddBicycleActivity.class);
                    i.putExtra("id", bikes[position].getId());
                    i.putExtra("categoryId", bikes[position].getCategory());
                    i.putExtra("name", bikes[position].getName());
                    i.putExtra("image", bikes[position].getImage());
                    i.putExtra("price", bikes[position].getPrice());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
            }
        });

        return listing;
    }
}