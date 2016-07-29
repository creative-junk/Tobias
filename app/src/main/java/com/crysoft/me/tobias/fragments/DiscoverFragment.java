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
import com.crysoft.me.tobias.ProductDetailsActivity;
import com.crysoft.me.tobias.ProductsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CategoryAdapter;
import com.crysoft.me.tobias.adapters.LocalCategoryAdapter;
import com.crysoft.me.tobias.adapters.MainCategoryAdapter;
import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.models.CategoryModel;
import com.crysoft.me.tobias.models.ProductsModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private DBAdapter databaseAdapter;

    private ParseQueryAdapter<ParseObject> mainAdapter;
    private ParseQueryAdapter<ParseObject> mainParseAdapter;

    private CategoryAdapter categoryAdapter;
    private MainCategoryAdapter mainCategoryAdapter;
    private LocalCategoryAdapter localCategoryAdapter;

    private List<CategoryModel> categoryList;

    private GridView gridView;
    private GridView mainGridView;

    private RelativeLayout rlLoading;
    private ViewFlipper mViewFlipper;

    int mFlipping = 0; // Initially flipping is off

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
        databaseAdapter = DBAdapter.getInstance(getActivity());

        gridView = (GridView) getActivity().findViewById(R.id.categoryGrid);
        mainGridView = (GridView) getActivity().findViewById(R.id.mainCategoryGrid);
        rlLoading = (RelativeLayout) getActivity().findViewById(R.id.loadingPanel);
        mViewFlipper = (ViewFlipper) getActivity().findViewById(R.id.viewFlipper);

        mViewFlipper.setAutoStart(true);
        mViewFlipper.startFlipping();

        //Main Query Adapter
        mainParseAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), "category");
        mainParseAdapter.setTextKey("category_name");
        mainParseAdapter.setImageKey("category_image");
        mainCategoryAdapter = new MainCategoryAdapter(getActivity());
        //Set up the grid
        mainGridView.setAdapter(mainCategoryAdapter);
        //Load Stuff
        mainParseAdapter.loadObjects();


        categoryList=databaseAdapter.getAllCategories();
        if (categoryList.size() > 0) {
            localCategoryAdapter = new LocalCategoryAdapter(getActivity().getLayoutInflater(),categoryList,getActivity());
            gridView.setAdapter(localCategoryAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProductsActivity.class);
                    CategoryModel categoryDetails = categoryList.get(position);
                    intent.putExtra("categoryDetails", categoryDetails);
                    startActivity(intent);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);
                }
            });

        } else {

            //Initialize Parse Adapter
            mainAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), "Category");
            mainAdapter.setTextKey("category_name");
            mainAdapter.setTextKey("category_tag");
            mainAdapter.setImageKey("category_image");

            //Initialize our subclass of the Parse Query Adapter
            categoryAdapter = new CategoryAdapter(getActivity());


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

            //Use our Parse Adapter to Load data into the Grid

            mainAdapter.loadObjects();

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ParseObject parseItem = (ParseObject) parent.getItemAtPosition(position);
                    String objectId = parseItem.getObjectId();
                    String objectTitle = parseItem.getString("category_name");

                    CategoryModel categoryDetails = new CategoryModel();
                    categoryDetails.setObjectId(objectId);
                    categoryDetails.setCategoryName(objectTitle);
                    categoryDetails.setCategoryImage(parseItem.getParseFile("category_image").getUrl());
                    categoryDetails.setCategoryTag(parseItem.getString("category_tag"));

                    Intent i = new Intent(getActivity(), ProductsActivity.class);
                    i.putExtra("categoryDetails", categoryDetails);
                    startActivity(i);
                    ((Activity) getActivity()).overridePendingTransition(0, 0);

                }
            });


            updateCategories();
        }
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
    private void updateCategories(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.whereNotEqualTo("category_type","main");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> categoryObjectList, ParseException e) {
                if (e==null){
                    ArrayList<CategoryModel> categoryList= new ArrayList<CategoryModel>();
                    for (int i=0;i<categoryObjectList.size();i++){
                        ParseObject category = categoryObjectList.get(i);
                        CategoryModel categoryDetails = new CategoryModel();
                        categoryDetails.setObjectId(category.getObjectId());
                        categoryDetails.setCategoryName(category.getString("category_name"));
                        categoryDetails.setCategoryTag(category.getString("category_tag"));
                        categoryDetails.setCategoryImage(category.getParseFile("category_image").getUrl());
                        categoryList.add(categoryDetails);
                    }
                    databaseAdapter.updateAllCategories(categoryList);
                }
            }
        });
    }

}
