package com.crysoft.me.tobias;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crysoft.me.tobias.adapters.WishlistAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private WishlistAdapter wishlistAdapter;
    private DBAdapter databaseAdapter;
    private ListView listView;
    private List<ProductsModel> productList;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup the DB
        databaseAdapter = DBAdapter.getInstance(this);
        //Setup the layout
        listView = (ListView) findViewById(R.id.wishList);
        emptyView = (LinearLayout) findViewById(R.id.emptyWishList);


        productList = databaseAdapter.getWishlist();
        if (productList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Log.i("List is", "Empty");
        } else {

            wishlistAdapter = new WishlistAdapter(getLayoutInflater(), productList, this);
            listView.setAdapter(wishlistAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(WishlistActivity.this, ProductDetailsActivity.class);
                    ProductsModel productDetails = productList.get(position);
                    intent.putExtra("product_details", productDetails);
                    startActivity(intent);
                    ((Activity) WishlistActivity.this).overridePendingTransition(0, 0);
                }
            });
        }

    }

}
