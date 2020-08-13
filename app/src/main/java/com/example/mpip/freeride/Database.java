package com.example.mpip.freeride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

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
    private static final String card_number = "card_number";

    private static final String create_table_users = "CREATE TABLE "
            + table_users + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + email + " TEXT, " + telephone + " TEXT, " + password + " TEXT, " + name + " TEXT, "
            + surname + " TEXT, " + card_number + " TEXT);";

    //Creating table_clients columns
    private static final String client_id = "client_id";
    private static final String create_table_clients = "CREATE TABLE "
            + table_clients + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + client_id + " INTEGER, FOREIGN KEY ("+ client_id + ") REFERENCES " + table_users + "(" + id + "));";

    //Creating table_renters columns
    private static final String renter_id = "renter_id";
    private static final String store_name = "store_name";
    private static final String create_table_renters = "CREATE TABLE "
            + table_renters + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " + renter_id + " INTEGER, "
            + store_name + " TEXT, FOREIGN KEY (" + renter_id + ") REFERENCES " + table_users + " (" + id + "));";

    //Creating table_categories columns
    private static final String category = "Category";
    private static final String description = "Description";
    private static final String create_table_categories = "CREATE TABLE "
            + table_categories + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + category + " TEXT, " + description + " TEXT);";

    //Crating table_locations columns
    private static final String longitude = "Longitude";
    private static final String latitude = "Latitude";
    private static final String create_table_locations = "CREATE TABLE "
            + table_locations + "(" + id + " INTEGER, "
            + longitude + " TEXT, " + latitude + " TEXT, "
            + renter_id + " INTEGER, FOREIGN KEY (" + renter_id + ") REFERENCES " + table_rents + " (" + id + "));";

    //Crating table_bikes columns
    private static final String price = "Price";
    private static final String rented = "Rented";
    private static final String category_id = "category_id";
    private static final String location_id = "location_id";
    private static final String image_url = "image_url";

    private static final String create_table_bikes = "CREATE TABLE "
            + table_bikes + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + renter_id + " INTEGER, " + image_url + " TEXT, " + price + " INTEGER, " + category_id + " INTEGER, " + location_id + " INTEGER, "
            + rented + " INTEGER, "
            + " FOREIGN KEY (" + renter_id + ") REFERENCES " + table_renters + " (" + id  + "),"
            + " FOREIGN KEY (" + category_id + ") REFERENCES " + table_categories + " (" + id + "),"
            + " FOREIGN KEY (" + location_id + ") REFERENCES " + table_locations + " (" + id + "));";

    //Crating table_rents columns
    private static final String date_from = "date_from";
    private static final String date_to = "date_to";
    private static final String bike_id = "bike_id";
    private static final String full_price="full_price";
    private static final String create_table_rents = "CREATE TABLE "
            + table_rents + "(" + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + client_id + " INTEGER, " + full_price + " INTEGER, " + bike_id + " INTEGER, "
            + date_from + " DATE, " + date_to + " DATE, "
            + "FOREIGN KEY (" + client_id + ") REFERENCES " + table_clients + " (" + id + "),"
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
        db.execSQL(create_table_locations);
        db.execSQL(create_table_categories);
        db.execSQL(create_table_clients);
        db.execSQL(create_table_renters);
        db.execSQL(create_table_bikes);
        db.execSQL(create_table_rents);
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


    public void addCategories()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(table_categories, null, null);

        ContentValues values = new ContentValues();
        values.put(category, "Mountain Bike");
        db.insert(table_categories, null, values);

        values.put(category, "Everyday");
        db.insert(table_categories, null, values);

        values.put(category, "Sports");
        db.insert(table_categories, null, values);
    }

    public ArrayList getCategories()
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

    public boolean checkLogin(String email, String pass)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from User where Email='" + email + "'" +
                " and Password='" + pass + "'", null);

        return (cursor.getCount() > 0);
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

    public boolean insertUser(String pass, String Email, String n, String s, String tel, String cardNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(email, Email);
        values.put(password, pass);
        values.put(name, n);
        values.put(surname, s);
        values.put(telephone, tel);
        values.put(card_number, cardNumber);
        long result = db.insert(table_users, null, values);

        return result != -1;
    }

    public boolean insertClient(String pass, String Email, String n, String s, String tel, String cardNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(this.insertUser(pass, Email, n, s, tel, cardNumber)) {
            long result = db.insert(table_clients, null, values);
            return result != -1;
        }
        return false;
    }

    public boolean insertRenter(String pass, String Email, String n, String s, String tel, String cardNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(this.insertUser(pass, Email, n, s, tel, cardNumber)) {
            long result = db.insert(table_renters, null, values);
            return result != -1;
        }
        return false;
    }

    public boolean insertBike(int isRented, int cena, int renterId, int categoryId, int locationId, String imageUrl)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(renter_id, renterId);
        values.put(category_id, categoryId);
        values.put(price, cena);
        values.put(image_url, imageUrl);
        values.put(rented, isRented);
        values.put(location_id, locationId);

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

    public boolean insertRent(String from, String to, int bikeId, int clientId, int fullPrice)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(date_from, from);
        values.put(date_to, to);
        values.put(bike_id, bikeId);
        values.put(client_id, clientId);
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
