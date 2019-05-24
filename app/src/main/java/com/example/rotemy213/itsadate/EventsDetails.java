package com.example.rotemy213.itsadate;

public class EventsDetails {
    private String title;
    private String date;
    private String hour;
    public EventsDetails(String event)
    {
        System.out.println(event);
        String[] eventStr = event.split("\\(");
        String dateAndHour = eventStr[1].split("\\)")[0];
        this.title = eventStr[0];
        String[] timeDate = dateAndHour.split("T");
        this.date = timeDate[0];

        if(timeDate.length == 2) {
            System.out.println(timeDate[1]);
            this.hour = timeDate[1].replace(".", " ");
            this.hour = this.hour.split(" ")[0];

        }
        else
            this.hour = "unavailable";

    }

    public String getDate() {
        return this.date;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHour() {
        return this.hour;
    }
}
