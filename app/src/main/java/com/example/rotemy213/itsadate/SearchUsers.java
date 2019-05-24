package com.example.rotemy213.itsadate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchUsers extends AppCompatActivity {

    private ArrayList<String> usernames;
    private Socket client;
    private static PrintWriter pw;
    private String dataFromServer;
    private Button createMutualEvent;
    private String usermail;
    private TextView textview_invited;
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        createMutualEvent = findViewById(R.id.create_mutual_event);
        usermail = getIntent().getStringExtra("usermail");
        textview_invited = findViewById(R.id.invited_people_tv);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search For Participants");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mutual, menu);
        MenuItem mySearchItem = menu.findItem(R.id.search);
        searchView = (SearchView) mySearchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String wantedUser) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client = new Socket("192.168.43.32", 8002);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            sendData(client, wantedUser);
                            dataFromServer = readData(client.getInputStream());
                            if(dataFromServer.contains("noUser")) {
                                textview_invited.setText("No Users Were Found named: " + wantedUser);
                            }
                            else {
                                usernames = new ArrayList<String>(Arrays.asList(dataFromServer.split(",")));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initRecyclerView();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                            try {
//                                client.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                    }}).start();

                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }



    private void initImageBitmaps() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_users);
        RecyclerViewAdapterForUsers adapter = new RecyclerViewAdapterForUsers(usernames, SearchUsers.this, this.createMutualEvent, usermail, textview_invited);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void sendData(Socket client, String username) throws IOException {
        pw = new PrintWriter(client.getOutputStream());
        pw.write("SearchUser," + username);
        pw.flush();
    }
    public static String readData(InputStream istream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int bytesReceived = istream.read(buffer);
        String data = new String(buffer, 0, bytesReceived);
        System.out.println("data from server: " + data);
        data = data.replace("'", "");
        data = data.replace("[", "");
        data = data.replace("]", "");

        pw.close();
        return data;
    }

}
