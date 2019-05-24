package com.example.rotemy213.itsadate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.util.Arrays;

public class PopUpActivity extends FragmentActivity implements TimePickerDialog.OnTimeSetListener {
    private Button chooseStartHour;
    private Button chooseEndHour;
    private Button createAnEvent;
    private EditText title;
    private EditText description;
    private Boolean startDateOrEndDAte;
    private String date;
    private String[] invited_people_array;
    private Event event;
    private String link_to_event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        chooseStartHour = findViewById(R.id.starts_at);
        chooseEndHour = findViewById(R.id.ends_at);
        createAnEvent = findViewById(R.id.its_a_date_button);
        title = findViewById(R.id.editText_title);
        description = findViewById(R.id.editText_description);
        date = getIntent().getStringExtra("date_of_event");
        invited_people_array = getIntent().getStringArrayExtra("invited_people_array");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = -28;
        getWindow().setAttributes(layoutParams);

        chooseStartHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerStart = new TimePickerFragment();
                timePickerStart.show(getSupportFragmentManager(), "time picker");
                startDateOrEndDAte = true;
            }
        });
        chooseEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerStart = new TimePickerFragment();
                timePickerStart.show(getSupportFragmentManager(), "time picker");
                startDateOrEndDAte = false;
            }
        });
        createAnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {

                    event = new Event()
                            .setSummary(title.getText().toString())
                            .setDescription(description.getText().toString());

                    DateTime startDateTime = new DateTime(date + "T" + chooseStartHour.getText() + ":00+03:00");
                    EventDateTime start = new EventDateTime()
                            .setTimeZone("Asia/Jerusalem")
                            .setDateTime(startDateTime);
                    event.setStart(start);

                    DateTime endDateTime = new DateTime(date + "T" + chooseEndHour.getText() + ":00+03:00");
                    EventDateTime end = new EventDateTime()
                            .setTimeZone("Asia/Jerusalem")
                            .setDateTime(endDateTime);
                    event.setEnd(end);

                    EventAttendee[] attendees = new EventAttendee[invited_people_array.length];
                    for (int i = 0; i < invited_people_array.length; i++) {
                        attendees[i] = new EventAttendee().setEmail(invited_people_array[i]);
                    }
                    event.setAttendees(Arrays.asList(attendees));
                    System.out.println("hours " + event);

                    try {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    event = MainActivity.mService.events().insert("primary", event).execute();
                                    link_to_event = event.getHtmlLink();
                                    sendMail(link_to_event);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void sendMail(String link_to_event) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, invited_people_array);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Invitation To a " + title.getText() + " on " + date);
        intent.putExtra(Intent.EXTRA_TEXT, link_to_event);
        intent.setType("message/rfc822");
        title.getText().clear();
        startActivity(intent.createChooser(intent, "choose an email client"));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (startDateOrEndDAte == true) {
            if (minute < 10 && hourOfDay < 10)
                chooseStartHour.setText("0" + hourOfDay + ":0" + minute);
            else if (minute >= 10 && hourOfDay >= 10)
                chooseStartHour.setText(hourOfDay + ":" + minute);
            else if (minute < 10 && hourOfDay >= 10)
                chooseStartHour.setText(hourOfDay + ":" + "0" + minute);
            else
                chooseStartHour.setText("0" + hourOfDay + ":" + minute);

        } else {
            if (minute < 10 && hourOfDay < 10)
                chooseEndHour.setText("0" + hourOfDay + ":0" + minute);
            else if (minute >= 10 && hourOfDay >= 10)
                chooseEndHour.setText(hourOfDay + ":" + minute);
            else if (minute < 10 && hourOfDay >= 10)
                chooseEndHour.setText(hourOfDay + ":" + "0" + minute);
            else
                chooseEndHour.setText("0" + hourOfDay + ":" + minute);
        }
    }
}
