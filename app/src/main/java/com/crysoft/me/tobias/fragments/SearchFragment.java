package com.crysoft.me.tobias.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.adapters.CategorySpinnerAdapter;
import com.crysoft.me.tobias.models.ProductsModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
    ArrayList<ProductsModel> data = new ArrayList<ProductsModel>();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search, container, false);
        categorySpinner = (Spinner) getActivity().findViewById(R.id.spinnerCategory);
        searchQuery = (EditText) getActivity().findViewById(R.id.etFragmentSearch);
        find = (Button) getActivity().findViewById(R.id.btnFragmentFind);
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
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            //We have Lift off
                            data.clear();
                        }
                    }
                });
            }
        });
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
