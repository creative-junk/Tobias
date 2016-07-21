package com.crysoft.me.tobias;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

 public class CartActivity extends AppCompatActivity {
    public CartAdapter cartAdapter;
    private DBAdapter databaseAdapter;

    private ListView listView;
    public List<ProductsModel> productList;
    private LinearLayout emptyView;

    private TextView cartTitle;
    private TextView subTotal;
    private TextView total;
    private LinearLayout llTotals;
    private Button checkoutBTn;

    String totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);

        //Setup the DB
        databaseAdapter = DBAdapter.getInstance(this);
        //Setup the layout
        listView = (ListView) findViewById(R.id.cartList);
        emptyView = (LinearLayout) findViewById(R.id.emptyCartList);
        llTotals = (LinearLayout) findViewById(R.id.lltotals);

        checkoutBTn = (Button) findViewById(R.id.btnCheckout);
        subTotal = (TextView) findViewById(R.id.subTotal);
        total = (TextView) findViewById(R.id.tvTotal);
        cartTitle = (TextView) findViewById(R.id.cartTitle);
        populateList();
    }
    public void populateList(){
        productList = databaseAdapter.getCartItems();
        totalAmount = databaseAdapter.getCartAmount();

        subTotal.setText(totalAmount);
        total.setText(totalAmount);

        if (productList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            cartTitle.setVisibility(View.GONE);
            llTotals.setVisibility(View.GONE);
            checkoutBTn.setVisibility(View.GONE);

            Log.i("List is", "Empty");
        } else {

            cartAdapter = new CartAdapter(getLayoutInflater(), productList, this);

            if (cartAdapter.getCount() == 1) {
                cartTitle.setText("You have 1 item ready in your Shopping Cart");
            } else {
                cartTitle.setText("You have " + cartAdapter.getCount() + " items ready in your Shopping Cart");
            }

            listView.setAdapter(cartAdapter);
            /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                    ProductsModel productDetails = productList.get(position);
                    intent.putExtra("product_details", productDetails);
                    startActivity(intent);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);
                }
            });*/
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
        productList.clear();
        productList.addAll(databaseAdapter.getCartItems());

        // cartAdapter.swapItems(databaseAdapter.getCartItems());

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
