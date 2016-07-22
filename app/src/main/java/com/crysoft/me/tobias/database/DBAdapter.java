package com.crysoft.me.tobias.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.helpers.Utils;
import com.crysoft.me.tobias.models.ProductsModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maxx on 7/19/2016.
 */
public class DBAdapter extends SQLiteOpenHelper {
    //First let's make this class a singleton to avoid memory leaks and some other scary stuff like unnecessary relocations
    private static DBAdapter sInstance;
    private Context context;
    //Let's tag this b*
    private static final String TAG = "DBAdapter";

    //Database Information
    private static final String DATABASE_NAME = "Tobias";
    private static final int DATABASE_VERSION = 1;
    //Table Names
    private static final String TABLE_SHOPPING_CART = "shopping_cart";
    private static final String TABLE_FAVOURITES = "favourites";
    //CART table columns
    private static final String KEY_CART_ID = "_id";
    private static final String KEY_PRODUCT_ID = "product_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_DESCRIPTION = "product_description";
    private static final String KEY_PRODUCT_PRICE = "product_price";
    private static final String KEY_PRODUCT_IMAGE = "product_image";
    private static final String KEY_PRODUCT_STATUS = "product_status";
    private static final String KEY_PRODUCT_QUANTITY = "product_quantity";
    private static final String KEY_CART_STATUS = "cart_status";

    //FAVOURITES table columns
    private static final String KEY_FAVOURITES_ID = "_id";
    private static final String KEY_FAV_PRODUCT_ID = "fav_product_id";
    private static final String KEY_FAV_PRODUCT_NAME = "fav_product_name";
    private static final String KEY_FAV_PRODUCT_DESCRIPTION = "fav_product_description";
    private static final String KEY_FAV_PRODUCT_PRICE = "fav_product_price";
    private static final String KEY_FAV_PRODUCT_IMAGE = "fav_product_image";
    private static final String KEY_FAV_PRODUCT_QUANTITY = "fav_product_quantity";
    private static final String KEY_FAV_PRODUCT_STATUS = "fav_product_status";
    private static final String KEY_FAV_STATUS = "fav_status";

    // lets create a method for giving back an instance of this class so there will only ever be one instance at a time. If we have one we just return it,otherwise we create a new one
    public static synchronized DBAdapter getInstance(Context context) {
        //We use the application context so we don't accidentally leak an activities Context. see http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    //Initialize the Database AND CREATE our tables if needed
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SHOPPING_CART_TABLE = "CREATE TABLE " + TABLE_SHOPPING_CART +
                "(" +
                KEY_CART_ID + " INTEGER PRIMARY KEY," +
                KEY_PRODUCT_ID + " INTEGER," +
                KEY_PRODUCT_NAME + " TEXT," +
                KEY_PRODUCT_PRICE + " TEXT," +
                KEY_PRODUCT_IMAGE + " TEXT," +
                KEY_PRODUCT_DESCRIPTION + " TEXT," +
                KEY_PRODUCT_STATUS + " INTEGER," +
                KEY_CART_STATUS + " INTEGER," +
                KEY_PRODUCT_QUANTITY + " INTEGER" +
                ")";
        String CREATE_FAVOURITES_TABLE = "CREATE TABLE " + TABLE_FAVOURITES +
                "(" +
                KEY_FAVOURITES_ID + " INTEGER PRIMARY KEY," +
                KEY_FAV_PRODUCT_ID + " INTEGER," +
                KEY_FAV_PRODUCT_NAME + " TEXT," +
                KEY_FAV_PRODUCT_PRICE + " TEXT," +
                KEY_FAV_PRODUCT_IMAGE + " TEXT," +
                KEY_FAV_PRODUCT_DESCRIPTION + " TEXT," +
                KEY_FAV_PRODUCT_STATUS + " INTEGER," +
                KEY_FAV_STATUS + " INTEGER," +
                KEY_FAV_PRODUCT_QUANTITY + " INTEGER" +
                ")";

        db.execSQL(CREATE_SHOPPING_CART_TABLE);
        db.execSQL(CREATE_FAVOURITES_TABLE);


    }

    //If we upgrade, we drop the database for now. Not really sure is this is the best approach but for now let's stick to basic implementations
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOPPING_CART);
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_FAVOURITES);
            onCreate(db);

        }
    }

    //CRUD METHODS
    //Add Product to cart
    public boolean insertOrUpdateCart(ProductsModel productDetails) {
        long productId = -1;
        SQLiteDatabase db = getWritableDatabase();
        Boolean transactionSuccessful=false;

        //Let's wrap the operation in a transaction for Performance and Consistency
        db.beginTransaction();
        try {


            //Let's check if the user exists and get the quantity in the process
            if (getProductQty(productDetails.getObjectId()) > 0) {
                int quantity = getProductQty(productDetails.getObjectId());
                Log.i("Current QTY",String.valueOf(quantity));
                int newQuantity = ++quantity;

                ContentValues values = new ContentValues();
                values.put(KEY_PRODUCT_ID, productDetails.getObjectId());
                values.put(KEY_PRODUCT_NAME, productDetails.getProductName());
                values.put(KEY_PRODUCT_PRICE, productDetails.getProductPrice());
                values.put(KEY_PRODUCT_IMAGE, productDetails.getImageFile());
                values.put(KEY_PRODUCT_DESCRIPTION, productDetails.getDescription());
                values.put(KEY_PRODUCT_STATUS, productDetails.getProductStatus());
                values.put(KEY_CART_STATUS, productDetails.getCartStatus());
                //Product Exists, update its quantity
                values.put(KEY_PRODUCT_QUANTITY, newQuantity);
                Log.i("New Qty",String.valueOf(newQuantity));
                int rows=db.update(TABLE_SHOPPING_CART, values, KEY_PRODUCT_ID + "=?", new String[]{productDetails.getObjectId()});
                //int rows = db.update(TABLE_SHOPPING_CART,values,KEY_PRODUCT_ID+ "='"+productDetails.getObjectId()+"'",null);
                db.setTransactionSuccessful();
                transactionSuccessful=true;
            } else {
                //It's a new Product, Insert it
                ContentValues values = new ContentValues();
                values.put(KEY_PRODUCT_ID, productDetails.getObjectId());
                values.put(KEY_PRODUCT_NAME, productDetails.getProductName());
                values.put(KEY_PRODUCT_PRICE, productDetails.getProductPrice());
                values.put(KEY_PRODUCT_IMAGE, productDetails.getImageFile());
                values.put(KEY_PRODUCT_DESCRIPTION, productDetails.getDescription());
                values.put(KEY_PRODUCT_STATUS, productDetails.getProductStatus());
                values.put(KEY_CART_STATUS, productDetails.getCartStatus());
                values.put(KEY_PRODUCT_QUANTITY, "2");
                db.insertOrThrow(TABLE_SHOPPING_CART, null, values);
                db.setTransactionSuccessful();
                transactionSuccessful=true;
            }

        } catch (Exception e) {
            Log.d(TAG, "Error Updating Shopping Cart-InsertOrUpdate");
            Log.i("Error:",e.getMessage());
            e.printStackTrace();
            transactionSuccessful=false;
        } finally {
            db.endTransaction();
        }
        return transactionSuccessful;

    }

    //Get the Quantity given a Product Id
    public int getProductQty(String objectId) {
        int quantity = 0;
        SQLiteDatabase db = getWritableDatabase();
        /*String userSelectQuery = String.format("SELECT % FROM % WHERE %s=",new String[]{ KEY_PRODUCT_QUANTITY, TABLE_SHOPPING_CART, KEY_PRODUCT_ID});
        Cursor cursor = db.rawQuery(userSelectQuery, new String[]{objectId});
        */
        Cursor cursor = db.rawQuery(
                "SELECT " + KEY_PRODUCT_QUANTITY + " FROM "+TABLE_SHOPPING_CART +" WHERE "+KEY_PRODUCT_ID +"=?",new String[] {objectId}
        );

        if (cursor.moveToFirst()) {

            quantity = cursor.getInt(0);
            Log.i("Gotten Qty", String.valueOf(cursor.getInt(0)));
            cursor.close();


        }
        return quantity;

    }
    //Get Cart Items
    public List<ProductsModel> getCartItems(){
        List<ProductsModel> productList = new ArrayList<ProductsModel>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SHOPPING_CART,new String[]{
                KEY_CART_ID,KEY_PRODUCT_ID,KEY_PRODUCT_NAME,KEY_PRODUCT_PRICE,KEY_PRODUCT_IMAGE,KEY_PRODUCT_DESCRIPTION,KEY_PRODUCT_QUANTITY,KEY_PRODUCT_STATUS,KEY_CART_STATUS
        },null,null,null,null,null);
        try{
            if (cursor.moveToFirst()){
                do {
                    ProductsModel productDetails= new ProductsModel();
                    productDetails.setObjectId(cursor.getString(1));
                    productDetails.setProductName(cursor.getString(2));
                    productDetails.setProductPrice(cursor.getString(3));
                    productDetails.setImageFile(cursor.getString(4));
                    productDetails.setDescription(cursor.getString(5));
                    productDetails.setQuantity(String.valueOf(cursor.getInt(6)));

                    productDetails.setProductStatus(cursor.getString(7));
                    productDetails.setCartStatus(cursor.getString(8));
                    Log.i("Qty",String.valueOf(cursor.getInt(6)));
                    productList.add(productDetails);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Error Retrieving Cart Items");
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return productList;

    }
    //Get Cart Total
    public String getCartAmount(){
        int amount=0;


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SHOPPING_CART,new String[]{
                KEY_CART_ID,KEY_PRODUCT_ID,KEY_PRODUCT_NAME,KEY_PRODUCT_PRICE,KEY_PRODUCT_IMAGE,KEY_PRODUCT_DESCRIPTION,KEY_PRODUCT_QUANTITY,KEY_PRODUCT_STATUS,KEY_CART_STATUS
        },null,null,null,null,null);
        try{
            if (cursor.moveToFirst()){
                do {
                    amount = amount +Integer.valueOf(cursor.getString(3));
                    amount = amount * Integer.valueOf(cursor.getString(6));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Error Retrieving Cart Items");
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return Utils.formatPrice(amount);

    }
    //Get Number of Items in Cart
    public int getNumberOfItems(){
        int nrItems=0;


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SHOPPING_CART,new String[]{
                KEY_CART_ID,KEY_PRODUCT_ID,KEY_PRODUCT_NAME,KEY_PRODUCT_PRICE,KEY_PRODUCT_IMAGE,KEY_PRODUCT_DESCRIPTION,KEY_PRODUCT_QUANTITY,KEY_PRODUCT_STATUS,KEY_CART_STATUS
        },null,null,null,null,null);
        try{
            if (cursor.moveToFirst()){
                do {
                    nrItems = nrItems +Integer.valueOf(cursor.getString(6));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Error Retrieving Cart Items");
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return nrItems;

    }
    //Remove Item from Cart
    public void removeFromCart(String objectId){
        SQLiteDatabase db = getWritableDatabase();
        String delSQLString = "DELETE FROM " + TABLE_SHOPPING_CART + " WHERE " + KEY_PRODUCT_ID + "='" + objectId + "';";
        db.execSQL(delSQLString);
        db.close();
        //Log.i("Delete","Delete Called");
        //cartAdapter.notifyDataSetChanged();


    }
    //Add to Wishlist
    public boolean addOrUpdateWishlist(ProductsModel productDetails){
        //Use the Cached Connection
        SQLiteDatabase db = getWritableDatabase();
        Boolean transactionSuccessful=false;

        //As usual Wrap it in a transaction
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_FAV_PRODUCT_ID, productDetails.getObjectId());
            values.put(KEY_FAV_PRODUCT_NAME, productDetails.getProductName());
            values.put(KEY_FAV_PRODUCT_PRICE, productDetails.getProductPrice());
            values.put(KEY_FAV_PRODUCT_IMAGE, productDetails.getImageFile());
            values.put(KEY_FAV_PRODUCT_DESCRIPTION, productDetails.getDescription());
            values.put(KEY_FAV_PRODUCT_STATUS, productDetails.getProductStatus());
            values.put(KEY_FAV_STATUS, productDetails.getFavStatus());

            //Let's try to update the Saved Product if it exists.
            int rows = db.update(TABLE_FAVOURITES,values,KEY_FAV_PRODUCT_ID + "= ?",new String[]{productDetails.getObjectId()});

            //Let's check if the update worked
            if (rows==1){
                //Ok, we have updated a Saved Product, we could probably get the Product updated at this point if we needed to
                db.setTransactionSuccessful();
                transactionSuccessful=true;

            }else{
                //No Such Product Here, insert it
                db.insertOrThrow(TABLE_FAVOURITES,null,values);
                db.setTransactionSuccessful();
                transactionSuccessful=true;
            }
        }catch (Exception e){
            Log.d(TAG,"Error trying to Update Wishlist");
            transactionSuccessful=false;
        }finally {
            db.endTransaction();
        }
        return transactionSuccessful;

    }
    //Get Wishlist Items
    public List<ProductsModel> getWishlist(){
        List<ProductsModel> productList = new ArrayList<ProductsModel>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVOURITES,new String[]{
                KEY_FAVOURITES_ID,KEY_FAV_PRODUCT_ID,KEY_FAV_PRODUCT_NAME,KEY_FAV_PRODUCT_PRICE,KEY_FAV_PRODUCT_IMAGE,KEY_FAV_PRODUCT_DESCRIPTION,KEY_FAV_PRODUCT_QUANTITY,KEY_FAV_PRODUCT_STATUS,KEY_FAV_STATUS
        },null,null,null,null,null);
        try{
            if (cursor.moveToFirst()){
                do {
                    ProductsModel productDetails= new ProductsModel();
                    productDetails.setObjectId(cursor.getString(1));
                    productDetails.setProductName(cursor.getString(2));
                    productDetails.setProductPrice(cursor.getString(3));
                    productDetails.setImageFile(cursor.getString(4));
                    productDetails.setDescription(cursor.getString(5));
                    productDetails.setQuantity(cursor.getString(6));
                    productDetails.setProductStatus(cursor.getString(7));
                    productDetails.setFavStatus(cursor.getString(8));
                    productList.add(productDetails);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Error Retrieving Wishlist Items");
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return productList;

    }
    //remove Item from Wishlist
    private boolean removeFromWishlist(String objectId){
        SQLiteDatabase db = getWritableDatabase();
        String delSQLString = "DELETE FROM " + TABLE_FAVOURITES + " WHERE " + KEY_FAV_PRODUCT_ID + "=" + objectId + ";";
        db.execSQL(delSQLString);
        db.close();
        return true;
    }

}

