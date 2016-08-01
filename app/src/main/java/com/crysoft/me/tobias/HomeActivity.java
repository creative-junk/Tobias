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
import android.view.View;

import android.widget.TextView;

import com.crysoft.me.tobias.database.DBAdapter;
import com.crysoft.me.tobias.fragments.CartFragment;
import com.crysoft.me.tobias.fragments.RecentlyViewedFragment;
import com.crysoft.me.tobias.fragments.DiscoverFragment;
import com.crysoft.me.tobias.fragments.FavouriteFragment;
import com.crysoft.me.tobias.fragments.SearchFragment;
import com.crysoft.me.tobias.helpers.CircleImageView;
import com.crysoft.me.tobias.models.CategoryModel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.crysoft.me.tobias.helpers.DrawerMenuIntents.populateDrawerMenu;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CartFragment.OnFragmentInteractionListener, RecentlyViewedFragment.OnFragmentInteractionListener, DiscoverFragment.OnFragmentInteractionListener, FavouriteFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {
    private Toolbar mToolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView tvWelcomeMsg;
    private CircleImageView ivUserIcon;
    private TextView tvUserName;

    ParseUser currentUser;
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

        currentUser = ParseUser.getCurrentUser();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabs();

        tvWelcomeMsg = (TextView) findViewById(R.id.signInLink);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_drawer);
        Menu drawerItemsMenu = navigationView.getMenu();
        MenuItem signInMenu = drawerItemsMenu.findItem(R.id.signIn);



        //Setup Drawer
        ivUserIcon = (CircleImageView) headerLayout.findViewById(R.id.ivUserIcon);
        tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);

        if (currentUser==null){
            tvWelcomeMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogin();
                }
            });
        }else{
            tvWelcomeMsg.setText(currentUser.getString("first_name"));
            tvUserName.setText(currentUser.getString("first_name") + " " + currentUser.getString("last_name"));
            ParseFile imageFile = currentUser.getParseFile("profile_pic");
            if (imageFile != null){
                Picasso.with(this).load(imageFile.getUrl()).into(ivUserIcon);
            }
            signInMenu.setTitle("Hello "+currentUser.getString("first_name")+", Log Out");
            signInMenu.setIcon(R.drawable.ic_lock);

        }

       navigationView.setNavigationItemSelectedListener(this);
       navigationView.setItemIconTintList(null);


        updateMainCategories();
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
        adapter.addFragment(new DiscoverFragment(), "Browse");
        adapter.addFragment(new RecentlyViewedFragment(), "Recently Viewed");
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
                if (ParseUser.getCurrentUser() != null) {
                    Intent i = new Intent(this, HomeActivity.class);
                    this.startActivity(i);
                } else {
                    showLogin();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentUser!=null) {
            menu.findItem(R.id.nav_signin).setTitle("Home");
        }
        return super.onPrepareOptionsMenu(menu);

    }

    public void showCart() {
        Intent i = new Intent(this, CartActivity.class);
        this.startActivity(i);
    }

    public void showLogin() {
        Intent i = new Intent(this, FullscreenLoginActivity.class);
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
        populateDrawerMenu(id,this);

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

    private void updateCategories() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.whereNotEqualTo("category_type", "main");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> categoryObjectList, ParseException e) {
                if (e == null) {
                    ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();
                    for (int i = 0; i < categoryObjectList.size(); i++) {
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

    private void updateMainCategories() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.whereEqualTo("category_type", "main");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> categoryObjectList, ParseException e) {
                if (e == null) {
                    ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();
                    for (int i = 0; i < categoryObjectList.size(); i++) {
                        ParseObject category = categoryObjectList.get(i);
                        CategoryModel categoryDetails = new CategoryModel();
                        categoryDetails.setObjectId(category.getObjectId());
                        categoryDetails.setCategoryName(category.getString("category_name"));
                        categoryDetails.setCategoryImage(category.getParseFile("category_image").getUrl());
                        categoryList.add(categoryDetails);
                    }
                    databaseAdapter.updateMainCategories(categoryList);
                }
            }
        });
    }

}
