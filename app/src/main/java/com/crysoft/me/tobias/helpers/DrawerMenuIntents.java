package com.crysoft.me.tobias.helpers;

import android.content.Context;
import android.content.Intent;

import com.crysoft.me.tobias.CartActivity;
import com.crysoft.me.tobias.DealsActivity;
import com.crysoft.me.tobias.HomeActivity;
import com.crysoft.me.tobias.OrdersActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.WishlistActivity;

/**
 * Created by Maxx on 8/1/2016.
 */
public class DrawerMenuIntents {

    public static final void populateDrawerMenu(int id,Context context){
        if (id == R.id.home) {
            // Handle the camera action
            showHome(context);
        } else if (id == R.id.todaysDeals) {
            showDeals(context);
        } else if (id == R.id.yourCart) {
            DrawerMenuIntents.showCart(context);
        } else if (id == R.id.yourWishlist) {
            showWishlist(context);
        } else if (id == R.id.yourOrders) {
            showOrders(context);

        } else if (id == R.id.yourAccount) {
            showAccount(context);
        }else if (id == R.id.customerService) {
            showCustomerService(context);
        }else if (id == R.id.signIn) {
            showSignIn(context);
        }
    }


    public static final void showHome(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        context.startActivity(i);
    }

    public static final void showDeals(Context context) {
        Intent i = new Intent(context, DealsActivity.class);
        context.startActivity(i);
    }

    public static final void showCart(Context context) {
        Intent i = new Intent(context, CartActivity.class);
        context.startActivity(i);
    }

    public static final void showWishlist(Context context) {
        Intent i = new Intent(context, WishlistActivity.class);
        context.startActivity(i);
    }

    public static final void showOrders(Context context) {
        Intent i = new Intent(context, OrdersActivity.class);
        context.startActivity(i);
    }

    public static final void showAccount(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        context.startActivity(i);
    }

    public static final void showCustomerService(Context context) {

    }

    public static final void showSignIn(Context context) {
    }

}
