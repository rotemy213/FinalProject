from __future__ import print_function
import datetime
import sql_access

NOW = datetime.datetime.now()
USERS_DETAILS_DB = sql_access.Database("users_details", "main.db")
MONTHS_NUM_OF_DAYS = {"01": 31,
                      "02": (28, 29),
                      "03": 31,
                      "04": 30,
                      "05": 31,
                      "06": 30,
                      "07": 31,
                      "08": 31,
                      "09": 30,
                      "10": 31,
                      "11": 30,
                      "12": 31}


class UserCalendarDetails:
    def __init__(self, name_of_user, date):

        self.usermail = name_of_user
        self.dates = date

    @staticmethod
    def is_relevant(yy, mm, dd):
        present = datetime.datetime.now()
        time = datetime.datetime(int(yy), int(mm), int(dd))
        if time >= present:
            return True
        return False

    def relevant_events(self, dates):
        relev = []
        for date in dates:
            date = date.split('-')
            year = date[0]
            month = date[1]
            day = date[2]
            if self.is_relevant(year, month, day):
                relev.append(year + '-' + month + '-' + day)
        return str(relev)

    def check_busy_days(self, date):
        """
        :param usermail:
        :param date: "YY-MM-DD"
        :return: True - if day is busy, False if not.
        If the user is busy at that day(has a meeting at the day) - return True,
        else return False
        """
        date = date.split("-")
        day = date[2]
        month = date[1]
        year = date[0]
        events = USERS_DETAILS_DB.get_events_of_specific_user(self.usermail)

        events = self.remove_extras(str(events))
        for event in events:
            if (event):
                event = event.split("-")
                e_day = event[2]
                e_month = event[1]
                e_year = event[0]
                if e_day == day and e_month == month and e_year == year:
                    return True  # is busy
        return False

    @staticmethod
    def remove_extras(expression):
        expression = expression.replace('[', '')
        expression = expression.replace(']', '')
        expression = expression.replace('(', '')
        expression = expression.replace(')', '')
        expression = expression.replace("'", '')
        expression = expression.replace('"', '')
        expression = expression.replace(' ', '')
        return expression.split(",")

    def free_days_this_month(self, time_period=30, day_limitations=None):
        """
        :param time_period: default equals to 30 days, approximately a month ahead.
        :param day_limitations: for example - find a meeting in fridays
        :return: a list that includes all of the dates I can meet in according to my prefernces.

        This function gets limitations and time period which is currently a month, and finds optional dates for a meeting
        while being based on my already-made calendar.
        """
        free_days = []
        date = str(NOW).split(" ")[0].split("-")  # get date (not weekday).
        day = date[2]
        month = date[1]
        year = date[0]
        current_day = str(int(day))

        #  day limitations: monday = 0, sunday = 7
        if not day_limitations:
            for i in range(time_period):
                if self.day_exists(str(year) + '-' + str(month) + '-' + str(current_day)):
                    if len(str(current_day)) < 2:
                        current_date = str(year) + '-' + str(month) + '-' + str(0) + str(current_day)
                        is_busy = self.check_busy_days(current_date)
                    else:

                        current_date = str(year) + '-' + str(month) + '-' + str(current_day)
                        is_busy = self.check_busy_days(current_date)


                else:
                    current_day = str(1)
                    month = str(int(month) + 1)
                    if len(str(month)) < 2:
                        month = '0' + month
                    if int(month) <= 12:
                        current_date = str(year) + '-' + str(month) + '-' + str(0) + str(current_day)
                        is_busy = self.check_busy_days(current_date)
                    else:
                        month = "01"
                        year = str(int(year) + 1)
                        current_date = str(year) + '-' + str(month) + '-' + str(0) + str(current_day)
                        is_busy = self.check_busy_days(current_date)

                if not is_busy:
                    free_days.append(current_date)
                current_day = int(current_day) + 1

        else:
            if day_limitations == "Monday":
                weekday = 0
            elif day_limitations == "Tuesday":
                weekday = 1
            elif day_limitations == "Wednesday":
                weekday = 2
            elif day_limitations == "Thursday":
                weekday = 3
            elif day_limitations == "Friday":
                weekday = 4
            elif day_limitations == "Saturday":
                weekday = 5
            elif day_limitations == "Sunday":
                weekday = 6
            for i in range(time_period):
                try:
                    date = datetime.datetime(int(year), int(month), int(current_day))
                except ValueError:
                    current_day = str(1)
                    month = str(int(month) + 1)
                    if len(str(month)) < 2:
                        month = '0' + month
                    if int(month) <= 12:
                        current_date = str(year) + '-' + str(month) + '-' + str(0) + str(current_day)
                    else:
                        month = "01"
                        year = str(int(year) + 1)
                    date = datetime.datetime(int(year), int(month), int(current_day))
                if datetime.datetime.weekday(date) == int(weekday):
                    if self.day_exists(str(year) + '-' + str(month) + '-' + str(current_day)):
                        if len(str(current_day)) < 2:
                            current_date = str(year) + '-' + str(month) + '-' + str(0) + str(current_day)
                            is_busy = self.check_busy_days(current_date)
                        else:
                            current_date = str(year) + '-' + str(month) + '-' + str(current_day)
                            is_busy = self.check_busy_days(current_date)

                    if not is_busy:
                        free_days.append(current_date)
                current_day = int(current_day) + 1

        return free_days

    @staticmethod
    def day_exists(date):
        date = date.split("-")
        if int(date[2]) > 31:
            return False
        if int(date[1]) == 2:
            try:
                datetime.datetime(date[0], date[1], date[2])
            except ValueError:
                return False
        if int(date[1]) > 12 or int(date[1]) < 1:
            return False
        if len(date[1]) < 2:
            if MONTHS_NUM_OF_DAYS['0' + date[1]] < int(date[2]):
                return False
        else:
            if MONTHS_NUM_OF_DAYS[date[1]] < int(date[2]):
                return False
        return True
