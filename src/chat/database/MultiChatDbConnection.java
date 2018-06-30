/* 
 * Copyright (C) 2016 Giovanni Scanferla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class handle the database request from the server
 *
 * @author Giovanni Scanferla 5^ID
 */
public class MultiChatDbConnection {

    //##################VARIABLES#################################
    private Connection connection;
    private Statement statement;

    //##################CONSTRUCTORS#################################
    /**
     * Init a new database connection for the server
     */
    public MultiChatDbConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            connection = null;
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:chatroomUsersServer.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS userClient (id INTEGER PRIMARY KEY, username VARCHAR(30), userPassword VARCHAR(30), UNIQUE(username))");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException ex) {

        }

    }

    //##################PUBLIC METHODS#################################
    /**
     * Register a new user if possible
     *
     * @param user user to register
     * @param password user password
     */
    public void registerUser(String user, String password) {

        try {
            PreparedStatement insertUser = connection.prepareStatement("INSERT INTO userClient (username, userPassword) VALUES (?, ?)");
            insertUser.setString(1, user.trim());
            insertUser.setString(2, password.trim());
            insertUser.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("RegisterUser error " + ex.getMessage());
        }
    }

    /**
     * Return the user and password if exists
     *
     * @param user user to return
     * @param password password to return
     * @return user and password ResultSet or null
     */
    public ResultSet getUserList(String user, String password) {

        try {
            PreparedStatement userList = connection.prepareStatement("SELECT username, userPassword FROM userClient WHERE username = ? AND userPassword = ?");
            userList.setString(1, user.trim());
            userList.setString(2, password.trim());
            return userList.executeQuery();
        } catch (SQLException ex) {
            System.err.println("RegisterUser error " + ex.getMessage());
        }
        return null;
    }

    /**
     * Execute an update to the database
     *
     * @param query query to execute
     */
    public void executeUpdate(String query) {
        try {
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            System.err.println(query + " ERROR " + ex.getMessage());
        }
    }

    /**
     * Execute a query that return something
     *
     * @param query query to execute
     * @return ResultSet with information or null
     */
    public ResultSet executeQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            System.err.println(query + " ERROR " + ex.getMessage());
        }
        return null;
    }

    /**
     * Close the connection
     */
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException ex) {
            System.err.println("ON CLOSE: " + ex.getMessage());
        }
    }

}
