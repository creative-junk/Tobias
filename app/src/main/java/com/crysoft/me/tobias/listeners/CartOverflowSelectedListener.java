package com.crysoft.me.tobias.listeners;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

/**
 * Created by Maxx on 7/21/2016.
 */
 public class CartOverflowSelectedListener implements View.OnClickListener{
        private Context mContext;
        private ProductsModel myProduct;
        private DBAdapter databaseAdapter;
        private ListAdapter mCartAdapter;


        public  CartOverflowSelectedListener(Context context, ProductsModel product){

            mContext = context;
            myProduct = product;
            databaseAdapter = DBAdapter.getInstance(context);

        }
        @Override
        public void onClick(View v) {
            final ListView listView = (ListView) v.findViewById(R.id.cartList);
            //Use an Android v7 PopUp Menu Widget
            PopupMenu popupMenu = new PopupMenu(mContext,v){

                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.cart_overflow_remove:
                            databaseAdapter.removeFromCart(myProduct.getObjectId());

                            return true;
                        default:
                            return super.onMenuItemSelected(menu, item);
                    }

                }
            };
            popupMenu.inflate(R.menu.cart_menu);
            //popupMenu.getMenu().removeItem(R.id.cart_overflow_remove);
            popupMenu.show();
        }
    }


