package com.crysoft.me.tobias.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crysoft.me.tobias.HomeActivity;
import com.crysoft.me.tobias.ProductsActivity;
import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CategoryAdapter;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends Fragment {

    private ParseQueryAdapter<ParseObject> mainAdapter;
    private CategoryAdapter categoryAdapter;
    private GridView gridView;
    private RelativeLayout rlLoading;


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

        //Initialize Parse Adapter
        mainAdapter = new ParseQueryAdapter<ParseObject>(getActivity(), "Category");
        mainAdapter.setTextKey("category_name");
        mainAdapter.setTextKey("category_tag");
        mainAdapter.setImageKey("category_image");

        //Initialize our subclass of the Parse Query Adapter
        categoryAdapter = new CategoryAdapter(getActivity());
        gridView = (GridView) getActivity().findViewById(R.id.categoryGrid);
        rlLoading = (RelativeLayout) getActivity().findViewById(R.id.loadingPanel);

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
        //gridView.setAdapter(categoryAdapter);
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
