package com.example.rotemy213.itsadate;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import java.io.IOException;

public class UseCalendarTask extends AsyncTask<Void, Void, Void> {
    private PrivateEvent handleEventsCal;
    private String title;
    private String day;
    private String month;
    private String year;
    private String start;
    private String end;
    private String date;
    private String monthName;

    public UseCalendarTask(PrivateEvent handleEventsCal, String title,
                           String start, String end, String date) {
        this.date = date;
        String divided_date[] = this.date.split("/");
        this.year = divided_date[2];
        this.month = divided_date[1];
        this.day = divided_date[0];
        Log.i("UseCalenderTask", "DEBUG: Month =  " + divided_date[1]);

        if (this.day.endsWith("1") && !this.day.equals("11"))
            this.day = this.day + "st";
        else if (this.day.endsWith("2") && !this.day.equals("12"))
            this.day = this.day + "nd";
        else if (this.day.endsWith("3") && !this.day.equals("13"))
            this.day = this.day + "rd";
        else
            this.day = this.day + "th";
        this.monthName = getMonthName(Integer.parseInt(this.month));
        this.date = String.format("%s of %s %s", this.day, this.monthName, this.year);

        this.handleEventsCal = handleEventsCal;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    /*
        Returns the month name from the given number (for example "1" returns January)
     */
    private static String getMonthName(int monthNumber) {
        String[] months = new String[] {
          "January",
          "February",
          "March",
          "April",
          "May",
          "June",
          "July",
          "August",
          "September",
          "October",
          "November",
          "December"
        };
        return months[monthNumber - 1];
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            quickAddEvent();

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {

            this.handleEventsCal.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            this.handleEventsCal.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * What's the purpose of this method?
     * This method creates an event according to the date, hours and title the user has determined.
     * As easy and simple as it seems, the eventText has to be written according to the format
     * that the events().quickAdd() function Knows - the date has to be translated
     * to words.
     * for example: if the user chose 28/05/19 ---> it eould work if only I'd change it into the
     * 28th of May 2019.
     * @throws IOException
     */
    private void quickAddEvent() throws IOException {
        String eventText = this.title + " " + this.date + " " + this.start + "-" + this.end;
        MainActivity.mService.events().quickAdd("primary", eventText).setText(eventText).execute();
    }
}
