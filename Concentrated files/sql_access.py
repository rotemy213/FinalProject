"""
File: Sql Access
Author: Rotem Yaacovi
Date: 15/12/2018
Role:
This code handles requests regarding to the database.
"""

import sqlite3


class Database(object):
    def __init__(self, database_name, filename):
        self._file = filename
        self._database = database_name
        self._conn = sqlite3.connect(filename)
        self._cursor = self._conn.cursor()

    def finish_editing(self):
        """
        This method is responsible for saving changes
        committed.
        :return:None
        """
        self._conn.commit()

    def _close(self):
        """
        Closing the database.
        :return: None
        """
        self._conn.close()

    def get_database(self):
        """
        :return: database details.
        """
        sql_command = """SELECT * FROM {}""".format(self._database)
        self._cursor.execute(sql_command)
        self.finish_editing()
        # -------------------------------------------------------------------------------------------
        sql_command = """SELECT * FROM {}""".format(self._database)
        self._cursor.execute(sql_command)
        ans = self._cursor.fetchall()
        return ans
        # -------------------------------------------------------------------------------------------

    def insert_user_details(self, usermail, events):
        """
        What's the purpose of the method?
        To insert the events of the new user on the database.
        :param usermail: str type: represents user's registered name
        :param events: a list of events, casted to string type.
        :return: None
        """
        sql_command = """INSERT INTO users_details(usermail, updated_events) VALUES('{0}', "{1}");""".format(usermail,
                                                                                                             events)
        self._cursor.execute(sql_command)
        self.finish_editing()

    def does_user_exists(self, username):
        sql_command = """SELECT usermail FROM users_details;"""
        self._cursor.execute(sql_command)
        users = self._cursor.fetchall()
        users_with_mutual_name = []
        for user in users:
            if username in str(user):
                users_with_mutual_name.append(str(user).split("'")[1])
        return users_with_mutual_name

    def get_all_users(self):
        sql_command = """SELECT usermail FROM users_details;"""
        self._cursor.execute(sql_command)
        users = self._cursor.fetchall()
        return users

    def update_events(self, relevant_events, username):
        """
        What's the purpose of the method?
        If user has already a row in the table, his events' details would have
        to be updated immediately after his Login(MainActivity)
        :param relevant_events: a list of events, casted to string type.
        :param username: str type: represents user's registered name
        :return: None
        """
        sql_command = """UPDATE users_details SET updated_events = "{0}" WHERE usermail="{1}";""".format(
            relevant_events, username)
        self._cursor.execute(sql_command)
        self.finish_editing()

    def get_events_of_specific_user(self, usermail):
        sql_command = """SELECT updated_events FROM users_details WHERE usermail='{0}';""".format(usermail)
        self._cursor.execute(sql_command)
        events_of_user = self._cursor.fetchall()
        return events_of_user

