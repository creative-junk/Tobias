package com.crysoft.me.tobias;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.fragments.CartFragment;
import com.crysoft.me.tobias.fragments.RecentlyViewedFragment;
import com.crysoft.me.tobias.fragments.DiscoverFragment;
import com.crysoft.me.tobias.fragments.FavouriteFragment;
import com.crysoft.me.tobias.fragments.SearchFragment;
import com.crysoft.me.tobias.models.CategoryModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,CartFragment.OnFragmentInteractionListener, RecentlyViewedFragment.OnFragmentInteractionListener, DiscoverFragment.OnFragmentInteractionListener, FavouriteFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {
    private Toolbar mToolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.home,
            R.drawable.ic_fav,
            R.drawable.ic_cart
    };
    private DBAdapter databaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseAdapter = DBAdapter.getInstance(this);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabs();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        updateCategories();

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void setupTabs() {
      //  tabLayout.getTabAt(0).setIcon(tabIcons[0]);
       //tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DiscoverFragment(),"Browse");
        adapter.addFragment(new RecentlyViewedFragment(),"Recently Viewed");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);

        //Associte the Searchable Configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
             case R.id.action_cart:
                showCart();
                return true;
            case R.id.nav_signin:
                showLogin();
                return true;
              default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void showCart(){
        Intent i = new Intent(this,CartActivity.class);
        this.startActivity(i);
    }
    public void showLogin(){
        Intent i = new Intent(this,FullscreenLoginActivity.class);
        this.startActivity(i);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFragment(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
