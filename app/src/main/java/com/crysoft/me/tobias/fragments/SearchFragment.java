package com.crysoft.me.tobias.fragments;


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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.SearchResultActivity;
import com.crysoft.me.tobias.adapters.CategorySpinnerAdapter;
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
public class SearchFragment extends Fragment {

    private Spinner categorySpinner;
    private CategorySpinnerAdapter categorySpinnerAdapter;

    private Button find;
    private EditText searchQuery;

    ParseObject categoryObject;
    ArrayList<ProductsModel> productList = new ArrayList<ProductsModel>();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search, container, false);

        categorySpinner = (Spinner) v.findViewById(R.id.spinnerCategory);
        searchQuery = (EditText) v.findViewById(R.id.etFragmentSearch);
        find = (Button) v.findViewById(R.id.btnFragmentFind);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Product");
                query.whereEqualTo("category_id",categoryObject);
                if (searchQuery.getText().length()>0){
                    query.whereMatches("product_name","("+searchQuery.getText().toString()+")","i");
                }
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> products, ParseException e) {
                        if (e==null){
                            //We have Lift off
                            productList.clear();
                            for (int a=0;a<products.size();a++){
                                ParseObject productObject = products.get(a);
                                ProductsModel productDetails = new ProductsModel();
                                productDetails.setObjectId(productObject.getObjectId());
                                productDetails.setProductName(productObject.getString("product_name"));
                                productDetails.setProductPrice(productObject.getString("price"));
                                productDetails.setDescription(productObject.getString("description"));
                                productDetails.setImageFile(productObject.getParseFile("product_image").getUrl());
                                productDetails.setProductStatus(productObject.getString("product_status"));
                                productList.add(productDetails);
                            }
                            Intent i = new Intent(getActivity(), SearchResultActivity.class);
                            i.putExtra("productList",productList);
                            startActivity(i);
                        }else if (e.getCode()==ParseException.CONNECTION_FAILED){
                            Toast.makeText(getActivity(), "Error: Connection Failed,Please Try again ", Toast.LENGTH_LONG).show();
                            Log.e("Search Fragment Error ",e.getMessage());
                        }else{
                            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        categorySpinnerSetup();
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
    private void categorySpinnerSetup(){
        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>(){

            @Override
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("Category");
                query.whereNotEqualTo("category_type","main");
                return query;
            }
        };
        categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(),factory);
        categorySpinnerAdapter.setTextKey("category_name"+"-"+"category_tag");
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setSelection(1);
        categorySpinner.setOnItemSelectedListener(new CategorySpinnerListener());
    }


    private class CategorySpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ParseObject theSelectedObject = categorySpinnerAdapter.getItem(position);
            Log.e("Category Spinner", theSelectedObject.getString("category_name") + " objectId is :" + theSelectedObject.getObjectId());
            categoryObject = theSelectedObject;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
