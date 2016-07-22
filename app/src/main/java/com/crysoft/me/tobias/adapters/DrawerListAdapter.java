package com.crysoft.me.tobias.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crysoft.me.tobias.R;
import com.crysoft.me.tobias.models.NavDrawerItem;

import java.util.ArrayList;

/**
 * Created by Maxx on 7/22/2016.
 */
public class DrawerListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public DrawerListAdapter(Context context,ArrayList<NavDrawerItem> navDrawerItems){
        this.mContext=context;
        this.navDrawerItems =navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item,null);

        }
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvCount = (TextView) convertView.findViewById(R.id.counter);

        ivIcon.setImageResource(navDrawerItems.get(position).getIcon());
        tvTitle.setText(navDrawerItems.get(position).getTitle());
        //display count
        if (navDrawerItems.get(position).getCounterVisibility()){
            tvCount.setText(navDrawerItems.get(position).getCount());
        }else{
            tvCount.setVisibility(View.GONE);
        }
        return convertView;
    }
}
