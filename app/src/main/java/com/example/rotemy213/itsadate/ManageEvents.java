package com.example.rotemy213.itsadate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageEvents extends AppCompatActivity {
    private ListView listView;
    private List<Event> eventList;
    private ArrayList<String> arrayList;
    private ArrayList<Event> arrayListOfEvents;
    private ArrayAdapter arrayAdapter;
    private TextView noResponse;
    private TextView denied;
    private TextView accepted;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);
        listView = findViewById(R.id.listview);
        arrayList = new ArrayList<>();
        noResponse = findViewById(R.id.noresponse);
        denied = findViewById(R.id.denied);
        accepted = findViewById(R.id.accepted);
        arrayListOfEvents = new ArrayList<>();
        title = findViewById(R.id.title_of_specific_event);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventList = getDataFromApi();
                    if (eventList.size() > 0) {
                        for (Event event : eventList) {
                            System.out.println(event);
                            if (event.containsKey("organizer")) {
                                if (event.getOrganizer().getEmail().equals(getIntent()
                                        .getStringExtra("usermail"))) {
                                    if (getEventDate(event) != "UNAVAILABLE") {
                                        arrayList.add(getEventDate(event));
                                        arrayListOfEvents.add(event);
                                    }

                                }
                            }
                        }
                    } else
                        Toast.makeText(ManageEvents.this, "No Events Found", Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayAdapter = new ArrayAdapter(ManageEvents.this, android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(arrayAdapter);
                        }
                    });
                    System.out.println("array" + arrayListOfEvents);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("arrived at here" + arrayListOfEvents);
                ArrayList acceptedInvitation = new ArrayList();
                ArrayList ignoredInvitation = new ArrayList();
                ArrayList deniedInvitation = new ArrayList();
                if (arrayListOfEvents.get(position).containsKey("summary")) {
                    title.setText(arrayListOfEvents.get(position).getSummary());
                } else title.setText("UnNamed Event");

                if (arrayListOfEvents.get(position).containsKey("attendees")) {

                    for (int i = 0; i < arrayListOfEvents.get(position).getAttendees().size(); i++) {
                        System.out.println("attname:" + arrayListOfEvents.get(position).getAttendees().get(i).getEmail());
                        System.out.println(arrayListOfEvents.get(position).getAttendees().get(i).getResponseStatus());
                        if (arrayListOfEvents.get(position).getAttendees().get(i).getResponseStatus().equals("accepted"))
                            acceptedInvitation.add(arrayListOfEvents.get(position).getAttendees().get(i).getEmail());
                        else if (arrayListOfEvents.get(position).getAttendees().get(i).getResponseStatus().equals("declined"))
                            deniedInvitation.add(arrayListOfEvents.get(position).getAttendees().get(i).getEmail());
                        else
                            ignoredInvitation.add(arrayListOfEvents.get(position).getAttendees().get(i).getEmail());
                    }
                    if (acceptedInvitation != null)
                        accepted.setText(acceptedInvitation.toString());
                    else accepted.setText("");
                    if (deniedInvitation != null)
                        denied.setText(deniedInvitation.toString());
                    else denied.setText("");

                    if (ignoredInvitation != null)
                        noResponse.setText(ignoredInvitation.toString());
                    else noResponse.setText("");

                } else {
                    accepted.setText("");
                    denied.setText("");
                    noResponse.setText("");

                }


            }
        });

    }


    private String getEventDate(Event event) throws IOException {
        try {
            if (event.containsKey("start")) {
                if (event.getStart().containsKey("dateTime")) {
                    String[] dateAndHour = event.getStart().getDateTime().toString().split("T");
                    dateAndHour = dateAndHour[0].split("-");
                    return dateAndHour[2] + "-" + dateAndHour[1] + "-" + dateAndHour[0];

                }
            }
            return "UNAVAILABLE";

        } catch (NullPointerException e) {
            return null;
        }


    }

    /**
     *What's the purpost of this method?
     * Retrieve all of the upcoming events on the calendar of the user, using Google
     * Calendar API.
     * The retrieved data would be arranged by order.
     * @return List<Event> items
     * @throws IOException
     */
    private List<Event> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        List<Event> eventList = new ArrayList<Event>();
        Events events = MainActivity.mService.events().list("primary")
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute(); // Uses the first activity credentials - must use them in order
                            //to use calendar's features.
                            //They represent some kind of identifier.
        List<Event> items = events.getItems();// Events is being seperated into a list.
        return items;
    }
}

