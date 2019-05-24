package com.example.rotemy213.itsadate;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PrivateEvent extends FragmentActivity implements TimePickerDialog.OnTimeSetListener {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button mDisplayDate;
    private Button uploadEventButton;
    private Button start_time;
    private Button end_time;
    private boolean startDateOrEndDAte = true;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_events_cal);
        mDisplayDate = findViewById(R.id.change_date);
        uploadEventButton = findViewById(R.id.upload);
        start_time = findViewById(R.id.button_start);
        end_time = findViewById(R.id.button_end);
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerStart = new TimePickerFragment();
                timePickerStart.show(getSupportFragmentManager(), "time picker");
                startDateOrEndDAte = true;

            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerEnd = new TimePickerFragment();
                timePickerEnd.show(getSupportFragmentManager(), "time picker");
                startDateOrEndDAte = false;
            }
        });

        /*
        set the date on the button
         */
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        mDisplayDate.setText(dateFormat.format(date));

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        PrivateEvent.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.drawable.button_pattern_thre));
                dialog.show();
            }
        });
        uploadEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText title = findViewById(R.id.title_event);
                Button startHour = findViewById(R.id.button_start);
                Button endHour = findViewById(R.id.button_end);
                String date = (String) mDisplayDate.getText();
                if (endHour.getText() != null && startHour.getText() != null
                        && title.getText() != null) {
                    refreshResults(title.getText().toString(),
                            startHour.getText().toString(), endHour.getText().toString(), date);
                } else {
                    Toast.makeText(PrivateEvent.this, "Make sure you've defined event hours/title!", Toast.LENGTH_LONG).show();
                }
            }
        });


        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                System.out.println("DATE" + day + " " + month + " " + year);
                month += 1;
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void refreshResults(String title, String start, String end, String date) {
        if (isDeviceOnline()) {
            new UseCalendarTask(PrivateEvent.this, title, start, end, date).execute();
            Toast.makeText(PrivateEvent.this, "Event created," +
                    " you will be able to see" +
                    " it in a few minutes.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(PrivateEvent.this, "No network connection available.",
                    Toast.LENGTH_LONG).show();
        }
    }


    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        PrivateEvent.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        System.out.println("VIEW " + view);
        if (startDateOrEndDAte == true) {
            if (minute < 10 && hourOfDay < 10)
                start_time.setText("0" + hourOfDay + ":0" + minute);
            else if (minute >= 10 && hourOfDay >= 10)
                start_time.setText(hourOfDay + ":" + minute);
            else if (minute < 10 && hourOfDay >= 10)
                start_time.setText(hourOfDay + ":" + "0" + minute);
            else
                start_time.setText("0" + hourOfDay + ":" + minute);

        } else {
            if (minute < 10 && hourOfDay < 10)
                end_time.setText("0" + hourOfDay + ":0" + minute);
            else if (minute >= 10 && hourOfDay >= 10)
                end_time.setText(hourOfDay + ":" + minute);
            else if (minute < 10 && hourOfDay >= 10)
                end_time.setText(hourOfDay + ":" + "0" + minute);
            else
                end_time.setText("0" + hourOfDay + ":" + minute);
        }
    }
}
