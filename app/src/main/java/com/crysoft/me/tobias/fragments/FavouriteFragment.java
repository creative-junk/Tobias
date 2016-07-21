package com.crysoft.me.tobias.fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crysoft.me.tobias.ProductDetailsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.WishlistAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    private WishlistAdapter wishlistAdapter;
    private DBAdapter databaseAdapter;
    private ListView listView;
    private List<ProductsModel> productList;
    private LinearLayout emptyView;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setup the DB
        databaseAdapter = DBAdapter.getInstance(getActivity());
        //Setup the layout
        listView = (ListView) getActivity().findViewById(R.id.wishList);
        emptyView = (LinearLayout) getActivity().findViewById(R.id.emptyWishList);


        productList = databaseAdapter.getWishlist();
        if (productList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            Log.i("List is", "Empty");
        } else {

            wishlistAdapter = new WishlistAdapter(getActivity().getLayoutInflater(), productList, getActivity());
            listView.setAdapter(wishlistAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                    ProductsModel productDetails = productList.get(position);
                    intent.putExtra("product_details", productDetails);
                    startActivity(intent);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
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
        public void onFragmentInteraction(Uri uri);
    }


}
