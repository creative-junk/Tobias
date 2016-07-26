package com.crysoft.me.tobias;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.crysoft.me.tobias.adapters.CategoryAdapter;
import com.crysoft.me.tobias.adapters.ProductsAdapter;
import com.crysoft.me.tobias.models.ProductsModel;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private ParseQueryAdapter<ParseObject> mainAdapter;
    private ProductsAdapter productsAdapter;
    private GridView gridView;
    private RelativeLayout rlLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Context context = this;
        final String categoryId = getIntent().getStringExtra("Category_id");
        String categoryName = getIntent().getStringExtra("CategoryName");

        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Parse Adapter
        mainAdapter = new ParseQueryAdapter<ParseObject>(this, "Product");
        mainAdapter.setTextKey("product_name");
        mainAdapter.setTextKey("price");
        mainAdapter.setImageKey("product_image");

        //Initialize our subclass of the Parse Query Adapter
        productsAdapter = new ProductsAdapter(this,categoryId);
        gridView = (GridView) findViewById(R.id.productsGrid);
        rlLoading = (RelativeLayout) findViewById(R.id.loadingPanel);

        mainAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            private ProgressDialog mProgressDialog;
            @Override
            public void onLoading() {
                gridView.setVisibility(View.GONE);
                rlLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaded(List<ParseObject> objects, Exception e) {
                gridView.setVisibility(View.VISIBLE);
                rlLoading.setVisibility(View.GONE);
                gridView.setAdapter(productsAdapter);
            }
        });

        gridView.setAdapter(productsAdapter);
        mainAdapter.loadObjects();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject parseItem = (ParseObject) parent.getItemAtPosition(position);
                String objectId = parseItem.getObjectId();
                String objectName = parseItem.getString("product_name");
                String objectPrice = parseItem.getString("price");
                String objectDescription = parseItem.getString("description");
                String objectStatus = String.valueOf(parseItem.getInt("status"));
                String imageURL = parseItem.getParseFile("product_image").getUrl();


                Intent i = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
                //Get the Products Product Model
                ProductsModel productDetails = new ProductsModel();
                productDetails.setObjectId(objectId);
                productDetails.setProductName(objectName);
                productDetails.setProductPrice(objectPrice);
                productDetails.setImageFile(imageURL);
                productDetails.setDescription(objectDescription);
                productDetails.setProductStatus(objectStatus);;

                //Push the Parceable Model through the intent
                i.putExtra("product_details", productDetails);
                startActivity(i);


            }
        });
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
