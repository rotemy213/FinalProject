package com.example.rotemy213.itsadate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerViewAdapterForMutualEvents extends RecyclerView.Adapter<RecyclerViewAdapterForMutualEvents.ViewHolder>{
    private Context mContext;
    private String[] allEventsAvailable;
    private String[] invited_people_array;

    public RecyclerViewAdapterForMutualEvents(String[] dates, String[] invitedPeople, Context mContext)
    {
        this.mContext = mContext;
        this.allEventsAvailable = dates;
        this.invited_people_array = invitedPeople;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.listitem_mutual_events, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.date.setText(allEventsAvailable[i]);
        viewHolder.date.setTypeface(viewHolder.date.getTypeface(), Typeface.BOLD);
        viewHolder.createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PopUpActivity.class);
                intent.putExtra("date_of_event", viewHolder.date.getText().toString());
                intent.putExtra("invited_people_array" , invited_people_array);
                mContext.startActivity(intent);
//                Event event = new Event()
//                        .setSummary("Google I/O 2015")
//                        .setLocation("800 Howard St., San Francisco, CA 94103")
//                        .setDescription("A chance to hear more about Google's developer products.");
//
//                DateTime startDateTime = new DateTime("2015-05-28T09:00:00-07:00");
//                EventDateTime start = new EventDateTime()
//                        .setDateTime(startDateTime)
//                        .setTimeZone("America/Los_Angeles");
//                event.setStart(start);
//
//                DateTime endDateTime = new DateTime("2015-05-28T17:00:00-07:00");
//                EventDateTime end = new EventDateTime()
//                        .setDateTime(endDateTime)
//                        .setTimeZone("America/Los_Angeles");
//                event.setEnd(end);
//
//                String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
//                event.setRecurrence(Arrays.asList(recurrence));
//
//                EventAttendee[] attendees = new EventAttendee[] {
//                        new EventAttendee().setEmail("lpage@example.com"),
//                        new EventAttendee().setEmail("sbrin@example.com"),
//                };
//                event.setAttendees(Arrays.asList(attendees));
//
//                EventReminder[] reminderOverrides = new EventReminder[] {
//                        new EventReminder().setMethod("email").setMinutes(24 * 60),
//                        new EventReminder().setMethod("popup").setMinutes(10),
//                };
//                Event.Reminders reminders = new Event.Reminders()
//                        .setUseDefault(false)
//                        .setOverrides(Arrays.asList(reminderOverrides));
//                event.setReminders(reminders);
//
//                String calendarId = "primary";
//                event = MainActivity.mService.events().insert(calendarId, event).execute();
//                sendMail(event.getHtmlLink(), members);

            }
        });
    }

    @Override
    public int getItemCount() {
        return allEventsAvailable.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView date;
        LinearLayout parentLayout;
        Button createButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date= itemView.findViewById(R.id.date);
            parentLayout = itemView.findViewById(R.id.parent_of_events);
            createButton = itemView.findViewById(R.id.btn_create_mutual_event);
        }
    }
    public void sendMail(String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, "rotemy213@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "CHECK");
        intent.putExtra(Intent.EXTRA_TEXT, link);
        intent.setType("message/rfc822");
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(iCalendarAttachment()));
        mContext.startActivity(intent.createChooser(intent,"choose an email client"));


    }
}
