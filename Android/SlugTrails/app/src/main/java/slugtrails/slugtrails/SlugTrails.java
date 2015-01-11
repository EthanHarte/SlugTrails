package slugtrails.slugtrails;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapdemo.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class SlugTrails extends ActionBarActivity implements ActionBar.TabListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
    ExpandableListView expListView;

    private Socket client;

    private SearchView search;
    private MyListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<Continent> continentList = new ArrayList<Continent>();

    private ArrayList<LatLng> locations = new ArrayList<LatLng>(200);
    private String[] names = new String[200];

    private static double gps1 = -1, gps2 = -1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private GoogleMap map;
    private GoogleMap mMap;

    ArrayList<Country> countryList = new ArrayList<Country>();
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slug_trails);
        buildGoogleApiClient();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        SendGetAllRequest sendMessageTask = new SendGetAllRequest();
        sendMessageTask.execute();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);



    // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setIcon(mSectionsPagerAdapter.getPageIcon(i))
                            .setTabListener(this));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slug_trails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        if (tab.getText().equals("LIST")) {
            setContentView(R.layout.activity_slug_trails);
            setContentView(R.layout.fragment_list);

            displayList();

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            search = (SearchView) findViewById(R.id.search);
            search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            search.setIconifiedByDefault(false);
            search.setOnQueryTextListener(this);
            search.setOnCloseListener(this);



          //  expListView = (ExpandableListView) findViewById(R.id.laptop_list);
          //  final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
           //         this, groupList, laptopCollection);
           // expListView.setAdapter(expListAdapter);

            //setGroupIndicatorToRight();



            if (mMap != null) {
                mMap = null;
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = (fm.findFragmentById(R.id.map));
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }
        if (tab.getText().equals("MAP")) {
            SendGetAllRequest message = new SendGetAllRequest();
            message.execute();
            setUpMapIfNeeded();
        }

        if (tab.getText().equals("TAG")) {

            setContentView(R.layout.activity_slug_trails);
            setContentView(R.layout.fragment_slug_trails);
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TextView tv = (TextView)findViewById(R.id.editText);

                    if (gps1 == -1 || gps2 == -1){
                        Toast.makeText(SlugTrails.this,
                                "Try loading the map first to get gps!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (tv.getText().length() < 3) {
                        Toast.makeText(SlugTrails.this,
                                "Enter at least 3 characters for a name!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                    Toast.makeText(SlugTrails.this,
                                "Successfully tagged animal", Toast.LENGTH_SHORT).show();
                        continentList.clear();
                        SendMessage sendMessageTask = new SendMessage();
                        sendMessageTask.execute();
                        SendGetAllRequest message = new SendGetAllRequest();
                        message.execute();
                    }
                }
            });

            if (mMap != null) {
                mMap = null;
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = (fm.findFragmentById(R.id.map));
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }

        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }


    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            myList.expandGroup(i);
        }
    }

    //method to expand all groups
    private void displayList() {

        //display the list
        loadSomeData();

        //get reference to the ExpandableListView
        myList = (ExpandableListView) findViewById(R.id.expandableList);
        //create the adapter by passing your ArrayList data
        listAdapter = new MyListAdapter(SlugTrails.this, continentList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

    }

    private void loadSomeData() {


    }


    @Override
    public boolean onQueryTextChange(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
System.out.println("LATITIDUDUE: " + LocationServices.FusedLocationApi.getLastLocation(
        mGoogleApiClient).getLatitude());

      gps1 = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient).getLatitude();
        gps2 = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient).getLongitude();

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return MyMapFragment.newInstance(position + 1);
                case 2:
                    return ListFragment.newInstance(position + 1);
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        public int getPageIcon(int position) {
            switch (position) {
                case 0:
                    return R.drawable.tagicon;
                case 1:
                    return R.drawable.mapicon;
                case 2:
                    return R.drawable.listicon;
            }
            return -1;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_slug_trails, container, false);
            return rootView;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ListFragment newInstance(int sectionNumber) {
            ListFragment fragment = new ListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MyMapFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MyMapFragment newInstance(int sectionNumber) {
            MyMapFragment fragment = new MyMapFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MyMapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
      //  setContentView(R.positive_view.activity_maps);
        if (mMap != null) {


        }
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            setContentView(R.layout.activity_maps);

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37, -122.06), 12));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //    mMap.addMarker(new MarkerOptions().position(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()))).setTitle("me");
          mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                System.out.println("latitidudue: " + arg0.getLatitude());
                gps1 = arg0.getLatitude();
                gps2 = arg0.getLongitude();
            }
        });
        int i = 0;
        for (LatLng loc : locations) {
            mMap.addMarker(new MarkerOptions().position(loc).title(names[i++]));
        }
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }


    private class SendGetAllRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("attempting to connect");

                client = new Socket("hackucsc2015.no-ip.info", 25565); // connect to the server

               BufferedReader reader = new BufferedReader( new InputStreamReader(client.getInputStream()));
                if (client.isConnected()) {
                    System.out.println("connected.");
                }
                PrintWriter printwriter = new PrintWriter(client.getOutputStream());
                printwriter.println("getall_4.4_5.5_6.6");
                printwriter.flush();
               // continentList.clear();
                locations.clear();

                if (client.isConnected()) {
                    System.out.println("we still connected");
                } else {
                    System.out.println("definitely disconeccted");
                }
                String string;
                try {
                    groupList = new ArrayList<String>();
                    continentList.clear();
                    int i = 0;
                    while ((string = reader.readLine()) != null) {
                        String[] array = string.split("_");
                        System.out.println(array[1]);
                        ArrayList<Country> count = new ArrayList<Country>();
                        count.add(new Country("", array[5], Integer.parseInt(array[2])));
                        continentList.add(new Continent(array[1], count));
                        locations.add(new LatLng(Double.parseDouble(array[3]), Double.parseDouble(array[4])));
                        names[i++] = array[1];
                       // mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(array[3]), Double.parseDouble(array[4]))).title(array[1]));
                       // id_name_time_gps1_gps2_desc
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

                printwriter.close();
                reader.close();
                client.close(); // closing the connection

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("attempting to connect");
                client = new Socket("hackucsc2015.no-ip.info", 25565); // connect to the server
                System.out.println("connected!");
                PrintWriter printwriter;
                printwriter = new PrintWriter(client.getOutputStream(), true);

                TextView tv = (TextView)findViewById(R.id.editText);
                TextView tv2 = (TextView)findViewById(R.id.editText2);

                //post id name gps1 gps2 desc
                printwriter.write("post_" + new Random().nextInt() + "_" + tv.getText() + "_" + 0 + "_" + gps1 + "_" + gps2 + "_" + tv2.getText()); // write the message to output stream



                printwriter.flush();

                printwriter.close();

                client.close(); // closing the connection


               /* client = new Socket("10.0.2.2", 43594); // connect to the server

                ProtocolBuffer buffer = new ProtocolBuffer(5); //size of buffer
                buffer.writeByte(0);

                client.getChannel().write(buffer.getBuffer());*/

               // client.close(); // closing the connection
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;

        }



    }




}
