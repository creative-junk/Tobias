package com.crysoft.me.tobias.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.fragments.CartFragment;
import com.crysoft.me.tobias.helpers.Constants;
import com.crysoft.me.tobias.helpers.Utils;
import com.crysoft.me.tobias.listeners.CartOverflowSelectedListener;
import com.crysoft.me.tobias.models.ProductsModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Maxx on 7/20/2016.
 */
public class CartAdapter extends BaseAdapter{
    private List<ProductsModel> productList;
    private LayoutInflater layoutInflater;
    private Context myContext;

    public CartAdapter(LayoutInflater layoutInflater, List<ProductsModel> productList, Context context){
        this.layoutInflater = layoutInflater;
        this.productList = productList;
        this.myContext = context;
    }
    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;


        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.cart_items,null);
            viewHolder.productImage = (ImageView) convertView.findViewById(R.id.ivCartItemImage);
            viewHolder.productName = (TextView) convertView.findViewById(R.id.tvCartItemName);
            viewHolder.productPrice = (TextView) convertView.findViewById(R.id.tvCartItemPrice);
            viewHolder.overflowMenu = (ImageView) convertView.findViewById(R.id.ivCartOverflow);
            //viewHolder.productDescription = (TextView) convertView.findViewById(R.id.tvItemDescription);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ProductsModel productDetails = productList.get(position);
        viewHolder.productPrice.setOnClickListener(new CartOverflowSelectedListener(myContext, productDetails));


        viewHolder.productName.setText(productDetails.getProductName().substring(0,20)+"...");
        viewHolder.productPrice.setText(Utils.formatPrice(Integer.valueOf(productDetails.getProductPrice())));

     //   viewHolder.productDescription.setText(productDetails.getDescription());

        if (productDetails.getImageFile()!=null){
            Picasso.with(myContext).load(productDetails.getImageFile()).into(viewHolder.productImage);
        }else{

        }
        return convertView;
    }
    private class ViewHolder{
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productDescription;
        EditText cartQty;
        ImageView overflowMenu;


    }
}
