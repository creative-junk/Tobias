package com.crysoft.me.tobias.listeners;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crysoft.me.tobias.CartActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

/**
 * Created by Maxx on 7/21/2016.
 */
 public class CartOverflowSelectedListener implements View.OnClickListener{
        private Context mContext;
        private ProductsModel myProduct;
        private DBAdapter databaseAdapter;
        private CartAdapter mCartAdapter;
        private List<ProductsModel> mProductList;


        public  CartOverflowSelectedListener(Context context, ProductsModel product,CartAdapter cartAdapter,List<ProductsModel> productList){

            mContext = context;
            myProduct = product;
            mCartAdapter = cartAdapter;
            mProductList = productList;
            databaseAdapter = DBAdapter.getInstance(context);

        }
        @Override
        public void onClick(View v) {
            final ListView listView = (ListView) v.findViewById(R.id.cartList);
            final View view =v;
            //Use an Android v7 PopUp Menu Widget
            PopupMenu popupMenu = new PopupMenu(mContext,v){

                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.cart_overflow_remove:
                            //databaseAdapter.removeFromCart(myProduct.getObjectId());
                            deleteFromCart(myProduct.getObjectId(),view);
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
        public void deleteFromCart(String objectId,View v){

            databaseAdapter.removeFromCart(objectId);
            List<ProductsModel> cartItems=databaseAdapter.getCartItems();
            mProductList.clear();
            mProductList.addAll(cartItems);

            mCartAdapter.notifyDataSetChanged();
            TextView cartTitle = (TextView)v.findViewById(R.id.cartTitle);
            if (cartItems.size() == 0){
                v.findViewById(R.id.emptyCartList).setVisibility(View.VISIBLE);
                v.findViewById(R.id.cartList).setVisibility(View.GONE);
                v.findViewById(R.id.cartTitle).setVisibility(View.GONE);
                v.findViewById(R.id.lltotals).setVisibility(View.GONE);
                v.findViewById(R.id.btnCheckout).setVisibility(View.GONE);
            }else if(cartItems.size()==1){
               cartTitle.setText("You have 1 item ready in your Shopping Cart");
            }else {
                cartTitle.setText("You have " + mCartAdapter.getCount() + " items ready in your Shopping Cart");
            }

        }
    }


