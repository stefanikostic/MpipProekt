package com.example.mpip.freeride;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mpip.freeride.domain.Bike;

public class RentedBikeAdapter extends BaseAdapter {
    private Context mContext;
    private final Bike[] bikes;
    String clientId;
    LayoutInflater inflter;

    public RentedBikeAdapter(Context mContext, Bike[] bikes, String clientId){
        this.mContext = mContext;
        this.bikes = bikes;
        this.clientId = clientId;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View listing = layoutInflater.inflate(R.layout.rented_gridview_item, parent, false);
        Bitmap bitmap = bikes[position].getImage();
        ImageView icon = (ImageView) listing.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(bitmap); // set logo images
        TextView textView = (TextView) listing.findViewById(R.id.textView);
        textView.setText(bikes[position].getName());
/*        listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AddBicycleActivity.class);
                i.putExtra("id", bikes[position].getId());
                i.putExtra("bikeExists", "true");
                    mContext.startActivity(i);
            }
        });*/
        return listing;
    }
}
