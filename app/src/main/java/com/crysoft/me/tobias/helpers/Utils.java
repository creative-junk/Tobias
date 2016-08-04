package com.crysoft.me.tobias.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
/**
 * Created by Maxx on 7/20/2016.
 */
public class Utils {


    public static final void showNoInternetConnection(Context context) {
        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    public static final String getBasePath() {
        return Environment.getExternalStorageDirectory() + "/" + Constants.ROOT_FOLDER_NAME;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static final void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.managedQuery(contentURI, projection, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            return s;
        }
        return null;
    }


    public static String getCurrentTimeInFormat() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String newFormat = formatter.format(new Date());
        return newFormat;
    }

    public static final Bitmap getBimapFromFile(String photoPath) {
        return BitmapFactory.decodeFile(photoPath);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    public static final Bitmap resizeBitmap(int newWidth, int newHeight, Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        bm = null;
        return resizedBitmap;

    }

    public static final String encodeToBase64(Bitmap image) {
        Bitmap imagex = image;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imagex.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] b = bos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public static final Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static final int generateRandom(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static final String convertDateIntoMillis(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            Date expiry = formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiry);
            return calendar.getTimeInMillis() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() + "";
    }

    public static final String convertDateIntoLocalMillis(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(("yyyy-mm-dd hh:mm:ss"));
        try {
            Date expiry = formatter.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiry);
            return calendar.getTimeInMillis() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis() + "";
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
    public static String formatPrice(int price){
        String formattedPrice;

        DecimalFormat fmt = new DecimalFormat();
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();

        fmts.setGroupingSeparator(',');

        fmt.setGroupingSize(3);
        fmt.setGroupingUsed(true);
        fmt.setCurrency(Currency.getInstance("KES"));
        fmt.setDecimalFormatSymbols(fmts);
        formattedPrice = fmt.format(price);
        return Constants.CURRENCY + " " + formattedPrice;
    }

}
