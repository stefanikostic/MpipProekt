package com.example.mpip.freeride;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;

public class RentedBikeAdapter extends BaseAdapter {
    private Context mContext;
    private final Bike[] bikes;
    private final String[] rent_ids;
    String clientId;
    LayoutInflater inflter;

    public RentedBikeAdapter(Context mContext, Bike[] bikes, String[] rent_ids, String clientId){
        this.mContext = mContext;
        this.bikes = bikes;
        this.clientId = clientId;
        this.rent_ids = rent_ids;
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
        final ImageView icon = (ImageView) listing.findViewById(R.id.icon); // get the reference of ImageView
        icon.setImageBitmap(bitmap); // set logo images
        TextView textView = (TextView) listing.findViewById(R.id.textView);
        textView.setText(bikes[position].getName());
        FloatingActionButton fab = (FloatingActionButton) listing.findViewById(R.id.cancelBike);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Bike");
                query.whereEqualTo("objectId", bikes[position].getId());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        parseObject.put("rented", false);
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Rents");
                                query1.whereEqualTo("objectId", rent_ids[position]);
                                query1.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject obj, ParseException e) {
                                        obj.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                Intent intent = new Intent(mContext, RentedBikesActivity.class);
                                                intent.putExtra("client_id", clientId);
                                                mContext.startActivity(intent);
                                            }
                                        });

                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        return listing;
    }
}
