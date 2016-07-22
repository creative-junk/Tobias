package com.crysoft.me.tobias.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crysoft.me.tobias.CartActivity;
import com.crysoft.me.tobias.ProductDetailsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CartAdapter;
import com.crysoft.me.tobias.adapters.WishlistAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
  */
public class CartFragment extends Fragment {
    private CartAdapter cartAdapter;
    private DBAdapter databaseAdapter;

    private ListView listView;
    private List<ProductsModel> productList;
    private LinearLayout emptyView;

    private TextView cartTitle;
    private TextView subTotal;
    private TextView total;
    private LinearLayout llTotals;
    private Button checkoutBTn;

    String totalAmount;

    private OnFragmentInteractionListener mListener;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setup the DB
        databaseAdapter = DBAdapter.getInstance(getActivity());
        //Setup the layout
        listView = (ListView) getActivity().findViewById(R.id.cartList);
        emptyView = (LinearLayout) getActivity().findViewById(R.id.emptyCartList);
        llTotals = (LinearLayout) getActivity().findViewById(R.id.lltotals);

        checkoutBTn = (Button) getActivity().findViewById(R.id.btnCheckout);
        subTotal = (TextView) getActivity().findViewById(R.id.subTotal);
        total = (TextView) getActivity().findViewById(R.id.tvTotal);
        cartTitle = (TextView) getActivity().findViewById(R.id.cartTitle);

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

            //cartAdapter = new CartAdapter(getActivity().getLayoutInflater(), productList, getActivity());

            cartAdapter = new CartAdapter(getActivity().getLayoutInflater(), productList, getActivity());

            if (cartAdapter.getCount()==1){
                cartTitle.setText("You have 1 item ready in your Shopping Cart");
            }else {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        productList.clear();
        productList.addAll(databaseAdapter.getCartItems());

       // cartAdapter.swapItems(databaseAdapter.getCartItems());

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public final void deleteFromCart(String objectId){
        databaseAdapter.removeFromCart(objectId);
        cartAdapter.notifyDataSetChanged();
        onResume();

    }

}
