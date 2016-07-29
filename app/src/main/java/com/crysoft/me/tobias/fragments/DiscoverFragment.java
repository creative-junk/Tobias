package com.crysoft.me.tobias.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crysoft.me.tobias.HomeActivity;
import com.crysoft.me.tobias.ProductsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CategoryAdapter;
import com.crysoft.me.tobias.adapters.MainCategoryAdapter;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private ParseQueryAdapter<ParseObject> mainAdapter;
    private ParseQueryAdapter<ParseObject> mainParseAdapter;
    private CategoryAdapter categoryAdapter;
    private MainCategoryAdapter mainCategoryAdapter;
    private GridView gridView;
    private GridView mainGridView;
    private RelativeLayout rlLoading;
    private ViewFlipper mViewFlipper;

    int mFlipping = 0 ; // Initially flipping is off

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Main Query Adapter
        mainParseAdapter = new ParseQueryAdapter<ParseObject>(getActivity(),"category");
        mainParseAdapter.setTextKey("category_name");
        mainParseAdapter.setImageKey("category_image");

        //Initialize Parse Adapter
        mainAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), "Category");
        mainAdapter.setTextKey("category_name");
        mainAdapter.setTextKey("category_tag");
        mainAdapter.setImageKey("category_image");


        //Initialize our subclass of the Parse Query Adapter
        categoryAdapter = new CategoryAdapter(getActivity());
        mainCategoryAdapter = new MainCategoryAdapter(getActivity());

        gridView = (GridView) getActivity().findViewById(R.id.categoryGrid);
        mainGridView = (GridView) getActivity().findViewById(R.id.mainCategoryGrid);
        rlLoading = (RelativeLayout) getActivity().findViewById(R.id.loadingPanel);
        mViewFlipper = (ViewFlipper) getActivity().findViewById(R.id.viewFlipper);


        mViewFlipper.setAutoStart(true);
        mViewFlipper.startFlipping();


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
                gridView.setAdapter(categoryAdapter);
            }
        });
        // Setup our Grid Views' Adapters
       // gridView.setAdapter(categoryAdapter);
        mainGridView.setAdapter(mainCategoryAdapter);

        //Use our Parse Adapter to Load data into the Grid
        mainParseAdapter.loadObjects();
        mainAdapter.loadObjects();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject parseItem = (ParseObject) parent.getItemAtPosition(position);
                String objectId = parseItem.getObjectId();
                String objectTitle = parseItem.getString("category_name");

                Intent i = new Intent(getActivity(), ProductsActivity.class);
                i.putExtra("Category_id", objectId);
                i.putExtra("CategoryName",objectTitle);

                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_discover, container, false);

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
