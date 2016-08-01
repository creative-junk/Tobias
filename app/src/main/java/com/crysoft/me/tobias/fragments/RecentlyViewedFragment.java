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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.crysoft.me.tobias.ProductDetailsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.RecentlyViewedAdapter;
import com.crysoft.me.tobias.adapters.WishlistAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.ProductsModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentlyViewedFragment extends Fragment {
    private DBAdapter databaseAdapter;

    private GridView gridView;
    private LinearLayout emptyView;

    private Button btnRecentlyViewed;

    private List<ProductsModel> productList;
    private RecentlyViewedAdapter recentlyViewedAdapter;

    public RecentlyViewedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Setup the DB
        databaseAdapter = DBAdapter.getInstance(getActivity());
        //Setup the layout
        gridView = (GridView) getActivity().findViewById(R.id.recentlyViewedProductsGrid);
        emptyView = (LinearLayout) getActivity().findViewById(R.id.emptyView);
        btnRecentlyViewed = (Button) getActivity().findViewById(R.id.btnClearRecent);

        btnRecentlyViewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecentlyViewed();
               // Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        productList = databaseAdapter.getRecentlyViewed();
        if (productList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            btnRecentlyViewed.setVisibility(View.GONE);
            Log.i("List is", "Empty");
        } else {

            recentlyViewedAdapter = new RecentlyViewedAdapter(getActivity().getLayoutInflater(), productList, getActivity());
            gridView.setAdapter(recentlyViewedAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    private void clearRecentlyViewed(){
        databaseAdapter.emptyRecentlyViewed();
        recentlyViewedAdapter.notifyDataSetChanged();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_recently_viewed, container, false);

        return v;
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
