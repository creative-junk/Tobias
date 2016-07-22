package com.crysoft.me.tobias.listeners;

import android.content.Context;
import android.view.View;

import com.crysoft.me.tobias.CartActivity;
import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Maxx on 7/22/2016.
 */
public class CartAddQtyButtonListener implements View.OnClickListener {
    private Context mContext;
    private ProductsModel myProduct;
    private DBAdapter databaseAdapter;
    private CartAdapter mCartAdapter;
    private List<ProductsModel> mProductList;

    //Weak Reference to Prevent Memory Leaks
    //Refer to http://stackoverflow.com/questions/9723106/get-activity-instance
    private static WeakReference<CartActivity> mCartActivityRef;

    public CartAddQtyButtonListener(Context context, ProductsModel product, CartAdapter cartAdapter, List<ProductsModel> productList) {
        mContext = context;
        myProduct = product;
        mCartAdapter = cartAdapter;
        mProductList = productList;
        databaseAdapter = DBAdapter.getInstance(context);
    }


    @Override
    public void onClick(View v) {

    }
}
