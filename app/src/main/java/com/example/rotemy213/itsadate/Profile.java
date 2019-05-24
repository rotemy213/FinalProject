package com.example.rotemy213.itsadate;
//Imports
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    /**
     * This activity represents user's profile
     * It concludes the 5 upcoming events of the user.
     * Furthermore, in this activity he has a menu where he can choose what he wants to do -
     * whether it is creating a private event, or creating a mutual one.
     */

    private static final String TAG = "Calendar";
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Uri personPhoto;
    private String personGivenName;
    private Calendar cal;

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    private ArrayList<String> mHours = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Set five events in the recycler view
        for(int i=1; i<=5; i++)
        {
            try {
                String event= getIntent().getStringExtra("event" + i);
                EventsDetails currentEvent = new EventsDetails(event);
                mNames.add(currentEvent.getTitle());
                mDates.add(currentEvent.getDate());
                mHours.add(currentEvent.getHour());
            }
            catch(Exception e) {
                break;
            }

        }

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Profile.this);
        CircleImageView userPic = findViewById(R.id.imageView3);

        if (acct != null) {
            personGivenName = acct.getGivenName();
            personPhoto = acct.getPhotoUrl();
            System.out.println("userpic"+ personPhoto);
            if(personPhoto != null)
                Picasso.with(Profile.this).load(personPhoto).into(userPic);
            else {
                userPic.setImageDrawable(getResources().getDrawable(R.drawable.incognito));
            }


        }
        else
            userPic.setImageDrawable(getResources().getDrawable(R.drawable.incognito));

        setUpToolBar(personPhoto, personGivenName);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        Intent in;
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        Intent in;
                        switch (menuItem.getItemId())
                        {
                            case R.id.Handle_Events:
                                in = new Intent(Profile.this, PrivateEvent.class);
                                startActivity(in);
                                break;
                            case R.id.search_users:
                                in = new Intent(Profile.this, SearchUsers.class)
                                        .putExtra("usermail", (getIntent().
                                                getStringExtra("usermail")).toString());
                                startActivity(in);
                                break;
                            case R.id.manage_events:
                                in = new Intent(Profile.this, ManageEvents.class)
                                .putExtra("usermail", (getIntent().
                                    getStringExtra("usermail")).toString());
                                startActivity(in);
                                break;
                            case R.id.calendar_link:
                                setContentView(R.layout.activity_google_cal);
                                Calendar cal = Calendar.getInstance();
                                in = new Intent(Intent.ACTION_EDIT);
                                in.setType("vnd.android.cursor.item/event");
                                in.putExtra("beginTime", cal.getTimeInMillis());
                                in.putExtra("allDay", true);
                                in.putExtra("rule", "FREQ=YEARLY");
                                in.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                                in.putExtra("title", "A Test Event from android app");
                                startActivity(in);
                                break;


                        }
                        return true;
                    }
                }
        );
        initImageBitmaps();


    }
    private void setUpToolBar(Uri personPhoto, String personGivenName)
    {
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView nv = findViewById(R.id.nav_view);
        View headView = nv.getHeaderView(0);
        TextView description = headView.findViewById(R.id.description);
        description.setText("Hello, " + personGivenName);
        CircleImageView user = headView.findViewById(R.id.profilePhoto);
        if(personPhoto != null)
            Picasso.with(Profile.this).load(personPhoto).into(user);
        else {
            user.setImageDrawable(getResources().getDrawable(R.drawable.incognito));
        }

    }

    private void initImageBitmaps(){
        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recyclerv_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames,this, mImageUrls, mDates, mHours);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}