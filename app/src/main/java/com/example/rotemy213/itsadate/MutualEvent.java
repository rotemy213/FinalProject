package com.example.rotemy213.itsadate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MutualEvent extends AppCompatActivity {
    private static PrintWriter pw;
    private static ArrayList<String> invited;
    private Socket client;
    private String[] common_dates;
    private String[] arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_event);
        String data = getIntent().getStringExtra("invitedPeople").replace("[", "");
        data = data.replace("]", "");
        arr = data.split(", ");
        invited = new ArrayList();
        LinearLayout linearLayout1 = findViewById((R.id.linear_layout));
        LinearLayout linearLayout2 = findViewById(R.id.linear_layout2);
        LinearLayout linearLayout;
        for (int i = 0; i < arr.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(arr[i]);
            textView.setBackgroundResource(R.drawable.bordered_rectangle_rounded_corners);
            invited.add(textView.getText().toString());

            if (i % 2 == 1)
            {
                linearLayout = linearLayout2;
            }
            else
                linearLayout = linearLayout1;
            textView.setTextSize(3 * getResources().getDisplayMetrics().scaledDensity);
            linearLayout.addView(textView);
        }

        Button mutualEventCreator = findViewById(R.id.create_mutual_event);
        mutualEventCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client = new Socket("192.168.43.32", 8002);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            System.out.println("invitedPeople " + invited);
                            sendData(client, invited.toString());
                            common_dates = readData(client.getInputStream());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initRecyclerView();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }}).start();

            }
        });
    }
    public void sendData(Socket client, String invitedPeople) throws IOException {
        pw = new PrintWriter(client.getOutputStream());
        pw.write("MutualEvent," + invitedPeople);
        pw.flush();
    }

    /**
     * The main purpose of this method is to recieve the data that was sent
     * from the server, and converting it from Bytes into String.
     * @param istream   inputStream
     * @return String[] arr_of_available_dates
     * @throws IOException
     */
    public static String[] readData(InputStream istream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int bytesReceived = istream.read(buffer); //bytesRecieved - the number of bytes that were
                                                  // sent from the server.
                                                  // The data is now being written into the buffer.
        String data = new String(buffer, 0, bytesReceived); //data now has the converted
                                                                   // infomation.
        String[] arr_of_available_dates = data.split(","); //the data which was sent from
                                                                 // the server was seperated
                                                                 // intentionally with comas.
        pw.close();
        return arr_of_available_dates;
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_available_events);
        RecyclerViewAdapterForMutualEvents adapter = new
                RecyclerViewAdapterForMutualEvents(common_dates, arr, MutualEvent.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
