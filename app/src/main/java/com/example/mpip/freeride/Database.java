package com.example.mpip.freeride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.mpip.freeride.domain.Location;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper
{

    public static String db_Name = "database";

    //Creating table names
    private static final String table_users = "User";
    private static final String table_clients = "Client";
    private static final String table_renters = "Renter";
    private static final String table_bikes = "Bike";
    private static final String table_rents = "Rent";
    private static final String table_locations = "Location";
    private static final String table_categories = "Category";

    //Creating table_users columns
    private static final String id = "id";
    private static final String email = "Email";
    private static final String password = "Password";
    private static final String name = "Name";
    private static final String surname = "Surname";
    private static final String telephone = "Telephone";

    private static final String create_table_users = "CREATE TABLE "
            + table_users + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + email + " TEXT, " + telephone + " TEXT, " + password + " TEXT, " + name + " TEXT, "
            + surname + " TEXT);";

    //Creating table_renters columns
    private static final String renter_id = "renter_id";
    private static final String store_name = "store_name";
    private static final String latitude = "latitude";
    private static final String longitude = "longitude";
    private static final String create_table_renters = "CREATE TABLE "
            + table_renters + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + email + " TEXT, " + telephone + " TEXT, " + password + " TEXT, " + name + " TEXT, "
            + surname + " TEXT, "
            + store_name + " TEXT, "
            + latitude + " REAL, "
            + longitude + " REAL);";

    //Creating table_categories columns
    private static final String category = "Category";
    private static final String description = "Description";
    private static final String create_table_categories = "CREATE TABLE "
            + table_categories + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + category + " TEXT, " + description + " TEXT);";

    /*//Crating table_locations columns
    private static final String longitude = "Longitude";
    private static final String latitude = "Latitude";
    private static final String create_table_locations = "CREATE TABLE "
            + table_locations + "(" + id + " INTEGER, "
            + longitude + " TEXT, " + latitude + " TEXT, "
            + renter_id + " INTEGER, FOREIGN KEY (" + renter_id + ") REFERENCES " + table_rents + " (" + id + "));";*/

    //Crating table_bikes columns
    private static final String price = "Price";
    private static final String rented = "Rented";
    private static final String category_id = "category_id";
    private static final String location_id = "location_id";
    private static final String image_url = "image_url";
    private static final String model_name = "model_name";

    private static final String create_table_bikes = "CREATE TABLE "
            + table_bikes + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + renter_id + " INTEGER, " + price + " INTEGER, " + category_id + " INTEGER, "
            + model_name + " TEXT, "
            + latitude + " REAL, "
            + longitude + " REAL, "
            + rented + " INTEGER, "
            + image_url + " TEXT, "
            + " FOREIGN KEY (" + renter_id + ") REFERENCES " + table_renters + " (" + id  + "),"
            + " FOREIGN KEY (" + category_id + ") REFERENCES " + table_categories + " (" + id + "));";

    //Crating table_rents columns
    private static final String date_from = "date_from";
    private static final String date_to = "date_to";
    private static final String bike_id = "bike_id";
    private static final String user_id = "user_id";
    private static final String full_price="full_price";
    private static final String create_table_rents = "CREATE TABLE "
            + table_rents + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + user_id + " INTEGER, " + full_price + " INTEGER, " + bike_id + " INTEGER, "
            + date_from + " DATE, " + date_to + " DATE, "
            + "FOREIGN KEY (" + user_id + ") REFERENCES " + table_users + " (" + id + "),"
            + "FOREIGN KEY (" + bike_id + ") REFERENCES " + table_bikes + " (" + id + "))";


    public Database(Context context)
    {
        super(context, db_Name + ".db", null, 1);
        Log.d("table", create_table_users);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(create_table_users);
        db.execSQL(create_table_categories);
        db.execSQL(create_table_renters);
        db.execSQL(create_table_bikes);
        db.execSQL(create_table_rents);

        ContentValues values = new ContentValues();

        values.put(category, "Mountain Bikes");
        values.put(description, "Mountain Bikes offer riding off-road, often over rough terrain");
        db.insert(table_categories, null, values);

        values = new ContentValues();
        values.put(category, "Hybrid/Comfort Bike");
        values.put(description, "Hybrids and Sport Comfort Bikes share the same comfort features but are distinguished by wheel size");
        db.insert(table_categories, null, values);

        values = new ContentValues();
        values.put(category, "Road Bike");
        values.put(description, "Road bikes rule the road due to their extreme efficiency and speed");
        db.insert(table_categories, null, values);

        values = new ContentValues();
        values.put(category, "Commuting Bike");
        values.put(description, "Simply put, a commuting bike is any bicycle used as general transportation, regardless of the style");
        db.insert(table_categories, null, values);

        values = new ContentValues();
        values.put(category, "Beach Cruiser");
        values.put(description, "A Beach Cruiser is a bicycle designed for riding short distances on flat terrain, like a boardwalk");
        db.insert(table_categories, null, values);

        values = new ContentValues();
        values.put(category, "BMX/Trick Bike");
        values.put(description, "BMXs are often very robust and durable and would be the best selection for anyone intending to do jumps or tricks");
        db.insert(table_categories, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS '" + table_users + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_clients + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_renters + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_locations + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_categories + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_bikes + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + table_rents + "'");
        onCreate(db);
    }



    public ArrayList<String> getCategories()
    {
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + table_categories, null);
        cursor.moveToFirst();

        if(cursor != null)
        {
            while(cursor.isAfterLast() == false)
            {
                list.add(cursor.getString(cursor.getColumnIndex(category)));
                cursor.moveToNext();
            }
        }
        return list;
    }

    public int checkLogin(String email, String pass)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from User where email='" + email + "'" +
                " and password='" + pass + "'", null);
        if(cursor.getCount()>0){
            return 1;
        }
        Cursor cursor1 = db.rawQuery("Select * from Renter where Email='" + email + "'" +
                " and Password='" + pass + "'", null);
        if(cursor1.getCount()>0){
            return 2;
        }
        return 0;
    }

    public int getLoginID(String email, String pass)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from User where Email='" + email + "'" +
                " and Password='" + pass + "'", null);

        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(id));
    }

    public int getCategoryID(String cat)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + table_categories + " where "
                + category + "='" + cat + "'", null);

        cursor.moveToFirst();
        String key = cursor.getString(cursor.getColumnIndex(id));

        return Integer.parseInt(key);
    }

    public String getCategoryByID(int categoryId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from Categories where id=" + categoryId + "'", null);

        return cursor.getString(cursor.getColumnIndex(category));
    }

    public boolean checkMail(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from User where Email='" + email + "'", null);

        return (cursor.getCount() > 0);
    }

    public boolean insertUser(String pass, String Email, String n, String s, String tel)
        {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(email, Email);
        values.put(password, pass);
        values.put(name, n);
        values.put(surname, s);
        values.put(telephone, tel);
        long result = db.insert(table_users, null, values);

        return result != -1;
    }


    public boolean insertRenter(String pass, String Email, String n, String s, String tel, String storeName, Double lat, Double longi)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(email, Email);
        values.put(password, pass);
        values.put(name, n);
        values.put(surname, s);
        values.put(telephone, tel);
        values.put(store_name, storeName);
        values.put(latitude, lat);
        values.put(longitude, longi);
        long result = db.insert(table_renters, null, values);

        return result != -1;
    }

    public boolean insertBike(int isRented, int cena, String mname, int renterId, int categoryId, double lat, double longi, String data)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(renter_id, renterId);
        values.put(category_id, categoryId);
        values.put(model_name, mname);
        values.put(price, cena);
        values.put(image_url, data);
        values.put(rented, isRented);
        values.put(latitude, lat);
        values.put(longitude, longi);

        long result = db.insert(table_bikes, null, values);

        return result != -1;
    }

    public boolean updateBike(int bike_id, int isRented, int cena, int renterId, int categoryId, int locationId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(renter_id, renterId);
        values.put(category_id, categoryId);
        values.put(price, cena);
        values.put(rented, isRented);
        values.put(location_id, locationId);

        int result = db.update(table_bikes, values, id + "=" + bike_id, null);

        return result > 0;
    }

    /*public boolean createEmptyOffer()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //ContentValues values = new ContentValues();

        long result = db.insert(table_offer, id_O, null);

        if(result == -1)
            return false;
        else
            return true;
    }

    public int getEmptyOfferID()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from Offer where Price is null", null);

        String key = "";

        if(cursor != null)
        {
            if (cursor.moveToFirst())
                key = cursor.getString(cursor.getColumnIndex(id_O));
            cursor.close();
        }

        int id = Integer.parseInt(key);
        return id;
    }*/

    public Cursor getAllBikes()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("Select * from Bike", null);
    }

    public int getRenterId(String renterEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from Renter WHERE Email='" + renterEmail+"'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String key = cursor.getString(cursor.getColumnIndex(id));

        return Integer.parseInt(key);
    }

    public Location getRenterLocation(String renterEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from Renter WHERE email ='" + renterEmail + "'";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(latitude)));
        double longi = Double.parseDouble(cursor.getString(cursor.getColumnIndex(longitude)));
        Location loc = new Location(lat, longi);

        return loc;
    }

    public boolean insertLocation(String longi, String lat, int renterId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(longitude, longi);
        values.put(latitude, lat);
        values.put(renter_id, renterId);
        long result = db.insert(table_locations, null, values);

        return result != -1;
    }

    public boolean insertRent(String from, String to, int bikeId, int userId, int fullPrice)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(date_from, from);
        values.put(date_to, to);
        values.put(bike_id, bikeId);
        values.put(user_id, userId);
        values.put(full_price, fullPrice);

        long result = db.insert(table_rents, null, values);

        return result != -1;
    }
    public Cursor getRents()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("Select * from Rents", null);
    }

    public Cursor getLocations()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("Select * from Locations", null);
    }


}
