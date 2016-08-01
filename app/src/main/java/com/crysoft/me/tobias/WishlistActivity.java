package com.crysoft.me.tobias;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        //Associte the Searchable Configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        switch (item.getItemId()) {

            case R.id.action_cart:
                showCart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCart(){
        Intent i = new Intent(this,CartActivity.class);
        this.startActivity(i);
    }

}
