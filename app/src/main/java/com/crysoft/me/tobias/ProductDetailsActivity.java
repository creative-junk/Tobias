package com.crysoft.me.tobias;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {
    TextView productName, productPrice, productDescription;
    ImageView productImage, addToFav;
    Button addTocart;
    String objectId;
    String imageURL;
    DBAdapter databaseAdapter;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseAdapter = DBAdapter.getInstance(this);

        productName = (TextView) findViewById(R.id.tvProductTitle);
        productPrice = (TextView) findViewById(R.id.tvProductPrice);
        productDescription = (TextView) findViewById(R.id.tvProductDescription);

        productImage = (ImageView) findViewById(R.id.ivProductImage);
        addToFav = (ImageView) findViewById(R.id.ivAddToFav);

        addTocart = (Button) findViewById(R.id.btAddToCart);

        //Get our Product Model from the Intent
        Intent productIntent = getIntent();
        final ProductsModel productDetails = (ProductsModel) productIntent.getExtras().getParcelable("product_details");
        productDetails.setQuantity("2");
        objectId = productDetails.getObjectId();
        imageURL = productDetails.getImageFile();

        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToFavourites(productDetails);

            }
        });

        addTocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCart(productDetails);

            }
        });

        productName.setText(productDetails.getProductName());
        productPrice.setText(productDetails.getProductPrice());
        productDescription.setText(productDetails.getDescription());

        Picasso.with(this).load(imageURL).into(productImage);


    }

    private void addProductToCart(ProductsModel productDetails) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding to Cart...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        //databaseAdapter.removeFromCart(productDetails.getObjectId());
        //Add to cart
       Boolean added = databaseAdapter.insertOrUpdateCart(productDetails);
        //Dismiss Dialog
        mProgressDialog.dismiss();
        //Notify the user
        if (added) {
            Toast.makeText(ProductDetailsActivity.this, productDetails.getProductName() + " Added to cart", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(ProductDetailsActivity.this, "Failed to add" + productDetails.getProductName() + " to shopping cart", Toast.LENGTH_LONG).show();
        }

    }

    private void addProductToFavourites(ProductsModel productDetails) {
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding to Cart...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        //add to wishlist
        Boolean added = databaseAdapter.addOrUpdateWishlist(productDetails);
        //Dismiss Dialog
        mProgressDialog.dismiss();
        if (added) {
            Toast.makeText(ProductDetailsActivity.this, productDetails.getProductName() + " Added to Favourites", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(ProductDetailsActivity.this, "Failed to add" + productDetails.getProductName() + " to Favourites", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        //Associate the Searchable Configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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
