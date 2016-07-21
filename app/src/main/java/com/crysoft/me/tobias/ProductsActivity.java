package com.crysoft.me.tobias;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

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

        mainAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            private ProgressDialog mProgressDialog;
            @Override
            public void onLoading() {
                mProgressDialog = new ProgressDialog(ProductsActivity.this);
                mProgressDialog.setMessage("Loading Shop...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();

            }

            @Override
            public void onLoaded(List<ParseObject> objects, Exception e) {
                gridView.setAdapter(productsAdapter);
                mProgressDialog.dismiss();
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
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


}
