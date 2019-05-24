"""
AUTHOR: Rotem Yaacovi.
Name Of Project: It's  a Date!
Submitted on May 2019
Descrption:
    A Server which is able to handle few requests at once, has an access to the sql Database.
    Furthermore, recieves data from client and handles it accordingly.
"""

# Imports
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

import socket
from select import select
import sql_access  # Module that I wrote by myself - provides access to database "users_details" in "main.db"
import calendar_access  # Module that I wrote by myself - use database data according to app's - user request.

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

# Constants
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
PORT = 8002
IP = '192.168.43.32'
BUFFER = 65535
NUMBER_OF_USERS = 100
SERVER_ADDR = (IP, PORT)  # Server address
USER_DETAILS = sql_access.Database("users_details",
                                   "main.db")  # Building an object which provies us access to database.
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


def handle_requests(data, client_address):
    """
    :param data - the data which was sent from the client.
    :type string
    :param client_address - the client socket itself.
    :type socket
    :return: None

    Purpose: Classification of the functions the server can provide to the user, according to the
             activity the user is located at.
    """
    #  Activity Profile
    #  Format of recieved data: string-casted list:
    #  First element is "Events" so the server will be able to classify the function that is requested.
    #  Second Element is user's Email address.
    #  From the third element and onwards - all the events of the user.
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    if data.startswith("Events"):
        # Removing all the extensions that comes as a result of sending string-casted lists through
        # sockets.
        data = data.replace('[', '')
        data = data.replace(']', '')
        data = data.replace(' ', '')
        data = data.replace("Events,", '')

        # Get all the events in a list so it"ll be easier to iterate through.
        data_events = data.split(",")
        usermail = data_events[0]

        # The list of all the events, sice data_events list holds unnecessary
        # details to events list such as usermail and the name of the activity.
        events = []
        for item in data_events:
            if data_events[0] != item and item:
                events.append(item)

        interactions_of_user_cal_events_database = calendar_access.UserCalendarDetails(usermail, events)
        if USER_DETAILS.does_user_exists(usermail):
            #  Check the existence of the user in the database.
            #  if exists - update user's details in database, else, insert new user in db and upload his events.
            USER_DETAILS.update_events(interactions_of_user_cal_events_database.relevant_events(events), usermail)
        else:
            USER_DETAILS.insert_user_details(usermail, interactions_of_user_cal_events_database.relevant_events(events))

    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    # Activity SearchUser
    #  Format of recieved data: string-casted list:
    #  First element is "Search User" so the server will be able to classify the function that is requested.
    #  Second Element is what the user wanted to search for, whether it is a full Email address or a name of a user.
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    elif data.startswith("SearchUser"):
        data = data.split(",")
        wanted_user = data[1]
        users_found = str(USER_DETAILS.does_user_exists(wanted_user)).replace(" ", "")
        # via the class sql_access, server gets
        #  a list of the existing users that conform to requirements.

        if USER_DETAILS.does_user_exists(wanted_user):
            client_address.send(users_found.encode())
        else:
            client_address.send("noUser".encode())
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    # Activity MutualEvent
    #  Format of recieved data: string-casted list:
    #  First element is "MutualEvent" so the server will be able to classify the function that is requested.
    #  Second Element is what the user wanted to search for, whether it is a full Email address or a name of a user.
    #  From the third element and onwards, there's a list of all the requested users,
    #  the organizer wants to have in his event.
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    elif data.startswith("MutualEvent"):
        all_mutual_events_dates = []
        data = data.replace("[", "").replace("]", "").replace(" ", "")
        data = data.split(",")
        del data[0]
        for name in data:
            access_to_user = calendar_access.UserCalendarDetails(name, None)
            users_free_days = access_to_user.free_days_this_month()
            if not all_mutual_events_dates:
                all_mutual_events_dates += users_free_days
            else:
                all_mutual_events_dates = list(set(all_mutual_events_dates) & set(users_free_days))

        all_mutual_events_dates = set(all_mutual_events_dates)
        all_mutual_events_dates = sorted(all_mutual_events_dates)
        client_address.send(str(all_mutual_events_dates).replace(" ", "").replace("'", "").replace("[", "").
                            replace("]", "").encode())
        return all_mutual_events_dates
    # ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    return


def handle_clients():
    """
    This code handles all the clients simultaneously, by using socket programming.
    The benefits of using this kind of server, are mainly due to the fact it disconnects
    right after a response is sent to the client.
    :return: None
    """
    # Initialize server socket
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.bind(SERVER_ADDR)
    server_sock.listen(NUMBER_OF_USERS)  # Listen to multiple clients

    try:
        open_clients_list = []  # List of all the currently open clients
        while True:
            # wlist - sockets that we can write to
            # rlist - sockets that we can read from
            rlist, wlist, xlist = select([server_sock] + open_clients_list, [], [])
            # Read from rlist
            for sock in rlist:
                if sock is server_sock:
                    # In case new clients attempt to connect
                    open_clients_list.append(server_sock.accept()[0])
                else:
                    data = sock.recv(BUFFER)
                    if not data:
                        # In case socket closed connection
                        open_clients_list.remove(sock)
                        sock.close()
                    else:
                        handle_requests(data.decode(), sock)
        for sock in open_clients_list:
            sock.close()
    finally:
        server_sock.close()


def main():
    handle_clients()


if __name__ == '__main__':
    main()
