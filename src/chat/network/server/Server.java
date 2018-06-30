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
package chat.network.server;

import chat.database.MultiChatDbConnection;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * This class create an UDP MultiChat Server
 *
 * @author Giovanni Scanferla 5^ID
 */
public class Server implements Runnable {

    //##################CONSTANTS#################################
    private final int PORT;
    private final String IP = "127.0.0.1";

    //##################VARIABLES#################################
    private boolean terminated;
    private DatagramSocket datagramSocket;
    private ArrayList<ClientInfo> ipArrayList;
    private MultiChatDbConnection databaseConnection;
    private GestoreServer gestoreServer;

    //##################CONSTRUCTORS#################################
    /**
     * Server constructor
     *
     * @param PORT port to create the server
     * @param gestoreServer gestore server
     * @throws SocketException if error on creation
     */
    public Server(int PORT, GestoreServer gestoreServer) throws SocketException {
        this.PORT = PORT;
        this.terminated = false;
        this.ipArrayList = new ArrayList<>();
        this.databaseConnection = new MultiChatDbConnection();
        this.gestoreServer = gestoreServer;
        initServer();
    }

    //#####################IMPLEMENTED METHODS#################################
    @Override
    public void run() {

        while (!terminated) {

            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);

                String dataInfo = new String(buf);

                String[] dataArray = dataInfo.split("§");

                ProtocolServerEnum enumField = ProtocolServerEnum.valueOf(dataArray[0].trim());

                String ipClient = packet.getAddress().getHostAddress();
                int portClient = packet.getPort();

                switch (enumField) {
                    case REGISTER:   //Handle a Register request by registering a new user or refuse it
                        String username = dataArray[1];
                        String password = dataArray[2];
                        String message;
                        if (!existUsername(username)) {
                            registerClient(username, password);

                            ClientInfo prov = new ClientInfo(ipClient, portClient, username);
                            message = "SUCCREGISTER";
                            prov.send(message);
                            gestoreServer.serverSentence(username + ": succesfully registered");
                        } else {
                            ClientInfo prov = new ClientInfo(ipClient, portClient, username);
                            message = "ERRORREGISTER";
                            prov.send(message);
                            gestoreServer.serverSentence(username + ": error while registering");
                        }

                        break;
                    case JOIN:    //Handle a join request by a new user
                        username = dataArray[1];
                        password = dataArray[2];

                        if (isRegisted(username, password) && !isLogged(username)) {
                            ipArrayList.add(new ClientInfo(ipClient, portClient, username));

                            message = "SUCCJOIN";
                            sendToOne(message, ipClient, username, portClient);

                            message = "JOIN§" + username;
                            sendToAllExcept(message, ipClient, username, portClient);
                            gestoreServer.serverSentence(username + ": joined");
                        } else {
                            ClientInfo prov = new ClientInfo(ipClient, portClient, username);
                            message = "ERRORJOIN";
                            prov.send(message);
                            gestoreServer.serverSentence(username + ": error joining");
                        }

                        break;
                    case CHAT:   //Handle a chat message and deliver it to all the chatroom
                        username = dataArray[1];
                        message = "";
                        for (int i = 2; i < dataArray.length; i++) {
                            message = message + dataArray[i];
                        }
                        message = "CHAT§" + username + "§" + message;
                        sendToAllExcept(message, ipClient, username, portClient);
                        break;
                    case DISCONNECT:   //Handle a disconnect request from a user

                        username = "";
                        for (int i = 1; i < dataArray.length; i++) {
                            username = username + dataArray[i];
                        }

                        ipClient = packet.getAddress().getHostAddress();
                        removeClientInfo(ipClient, username, portClient);
                        message = "DISCONNECT§USER§" + username;
                        sendToAllExcept(message, ipClient, username, portClient);
                        gestoreServer.serverSentence(username + ": disconnected");
                        break;
                    default:
                        break;
                }
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (IOException ex) {
                System.out.println("Listening on port: " + PORT + "...");
            } catch (InterruptedException ex) {
                terminated = true;
            }
        }

    }

    //##################PUBLIC METHODS#################################
    /**
     * Disconnect the clients and close the server
     */
    public void disconnet() {
        sendToAllExcept("DISCONNECT§SERVER", "", "", 0);
        this.databaseConnection.close();
        this.datagramSocket.close();
        this.terminated = true;
    }

    //##################PRIVATE METHODS#################################
    /**
     * Initialize the datagram socket
     *
     * @throws SocketException if error on creation
     */
    private void initServer() throws SocketException {
        datagramSocket = new DatagramSocket(PORT);
        datagramSocket.setSoTimeout(5000);
    }

    /**
     * Return true if the USERNAME exist
     *
     * @param username the USERNAME to check
     * @return true or false
     */
    private boolean existUsername(String username) {

        String sql = "SELECT username FROM userClient WHERE username='" + username + "';";
        ResultSet resultSet = databaseConnection.executeQuery(sql);
        username = "";

        try {
            while (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return (!username.equals(""));

    }

    /**
     * Check on the database if the credentials are correct
     *
     * @param username client USERNAME
     * @param password client password
     * @return true or false
     */
    private boolean isRegisted(String username, String password) {
        ResultSet resultSet = databaseConnection.getUserList(username, password);
        username = "";
        password = "";

        try {
            while (resultSet.next()) {
                username = resultSet.getString("username");
                password = resultSet.getString("userPassword");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return (!username.equals("") && !password.equals(""));
    }

    /**
     * Return true if the username is logged in
     *
     * @param username client username
     * @return true or false
     */
    private boolean isLogged(String username) {
        Iterator<ClientInfo> it = ipArrayList.iterator();

        while (it.hasNext()) {
            ClientInfo clientInfo = it.next();
            if (clientInfo.getUSERNAME().equals(username)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get a ClientInfo object
     *
     * @param ip client ip
     * @param username client user
     * @param port client port
     * @return ClientInfo object or null
     */
    private ClientInfo getClientInfo(String ip, String username, int port) {
        Iterator<ClientInfo> it = ipArrayList.iterator();

        while (it.hasNext()) {
            ClientInfo clientInfo = it.next();
            if (clientInfo.getIP().equals(ip) && clientInfo.getUSERNAME().equals(username.trim()) && clientInfo.getPORT() == port) {
                return clientInfo;
            }
        }

        return null;
    }

    /**
     * Remove a client from the server list
     *
     * @param ip client ip
     * @param username client USERNAME
     * @param port client port
     */
    private void removeClientInfo(String ip, String username, int port) {

        ClientInfo toRemove = getClientInfo(ip, username, port);

        if (toRemove != null) {
            ipArrayList.remove(toRemove);
        }

    }

    /**
     * Send a message to all the client except the one indicated
     *
     * @param message message to send
     * @param excludeIp ip excluded
     * @param excludeUser USERNAME excluded
     * @param excludePort port excluded
     */
    private void sendToAllExcept(String message, String excludeIp, String excludeUser, int excludePort) {

        Iterator<ClientInfo> it = ipArrayList.iterator();
        ClientInfo toExclude = getClientInfo(excludeIp, excludeUser, excludePort);

        while (it.hasNext()) {
            ClientInfo clientInfo = it.next();

            if (!clientInfo.equals(toExclude)) {
                clientInfo.send(message);
            }

        }

    }

    /**
     * Send a message to one client
     *
     * @param message message to send
     * @param ip ip to send
     * @param username USERNAME to send
     * @param port port to send
     */
    private void sendToOne(String message, String ip, String username, int port) {

        ClientInfo clientInfo = getClientInfo(ip, username, port);

        if (clientInfo != null) {
            clientInfo.send(message);
        }
    }

    /**
     * Register a new client
     *
     * @param username client USERNAME
     * @param password client password
     */
    private void registerClient(String username, String password) {
        databaseConnection.registerUser(username, password);

    }

    /**
     * Contains all the information about a client
     */
    private class ClientInfo {

        private final String IP;
        private final int PORT;
        private final String USERNAME;

        /**
         * ClientInfo constructor
         *
         * @param IP client ip
         * @param PORT client port
         * @param username client username
         */
        public ClientInfo(String IP, int PORT, String username) {
            this.IP = IP;
            this.PORT = PORT;
            this.USERNAME = username;
        }

        //##################GETTER#################################
        /**
         * IP client getter
         *
         * @return client ip
         */
        public String getIP() {
            return IP;
        }

        /**
         * PORT client getter
         *
         * @return client port
         */
        public int getPORT() {
            return PORT;
        }

        /**
         * Username client getter
         *
         * @return client username
         */
        public String getUSERNAME() {
            return USERNAME;
        }

        //##################PUBLIC METHODS#################################
        /**
         * Used to send a message to the client
         *
         * @param message message to send
         */
        public void send(String message) {
            try {
                byte[] buffer = message.getBytes();
                InetAddress receiverAddress = InetAddress.getByName(IP);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, PORT);
                datagramSocket.send(packet);
            } catch (UnknownHostException ex) {
                System.err.println(ex.getMessage());
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }

        //##################IMPLEMENTED METHODS#################################
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ClientInfo other = (ClientInfo) obj;
            if (this.PORT != other.PORT) {
                return false;
            }
            if (!Objects.equals(this.IP, other.IP)) {
                return false;
            }
            return Objects.equals(this.USERNAME, other.USERNAME);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.IP);
            hash = 31 * hash + this.PORT;
            hash = 31 * hash + Objects.hashCode(this.USERNAME);
            return hash;
        }

    }

}
