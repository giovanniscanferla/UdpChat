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
package chat.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import chat.reference.Reference;

/**
 * This class create an UDP Client to be connected to the Server 
 * @author Giovanni Scanferla 5^ID
 */
public class Client implements Runnable {

    //##################CONSTANTS#################################
    private final int SERVERPORT;
    private final int LOCALPORT;
    private final String IP; 
    private final String USERNAME;

    //##################VARIABLES#################################
    private boolean terminated;
    private DatagramSocket datagramSocket;
    private GestoreClient gestoreClient;

    //##################CONSTRUCTORS#################################
    /**
     * Client constructor
     * @param SERVERPORT server port
     * @param LOCALPORT client port
     * @param IP server ip
     * @param USERNAME client username
     * @param gestoreClient gestore client
     * @throws SocketException if error on creation
     */
    public Client(int SERVERPORT, int LOCALPORT, String IP, String USERNAME, GestoreClient gestoreClient) throws SocketException {
        this.SERVERPORT = SERVERPORT;
        this.LOCALPORT = LOCALPORT;
        this.IP = IP;
        this.USERNAME = USERNAME;
        this.terminated = false;
        this.gestoreClient = gestoreClient;
        initClient();
        
    }

    //##################IMPLEMENTED METHODS#################################
    @Override
    public void run() {
        
        while (!terminated) {

            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagramSocket.receive(packet);
                
                String dataInfo = new String(buf);
                
                String[] dataArray = dataInfo.split("§");
                ProtocolClientEnum enumField = ProtocolClientEnum.valueOf(dataArray[0].trim());
                switch (enumField) {
                    case JOIN:
                        String username = dataArray[1];
                        gestoreClient.writeInfo(Reference.INFOCOLOR, username + " joined the chat");
                        break;
                    case CHAT:
                        username = dataArray[1];
                        String message = "";
                        for (int i = 2; i < dataArray.length; i++) {
                            message = message + dataArray[i];
                        }
                        gestoreClient.writeMessage(username, message);
                        break;
                    case DISCONNECT:
                        
                        enumField = ProtocolClientEnum.valueOf(dataArray[1].trim());
                        
                        switch(enumField){
                            case USER:
                                username = dataArray[2];
                                gestoreClient.writeInfo(Reference.WARNCOLOR, username + " disconnected from the chat");
                                break;
                            case SERVER:
                                disconnect();
                                gestoreClient.writeInfo(Reference.ERRORCOLOR, "Server disconnected");
                                break;
                        }
                        
                        break;
                    case SUCCREGISTER:
                        endClient();
                        gestoreClient.mainWarn("Successfully registered");
                        break;
                    case SUCCJOIN:
                        gestoreClient.startClientInterface();
                        break;
                    case ERRORREGISTER:
                        endClient();
                        gestoreClient.mainWarn("Error registering, check your info");
                        break;
                    case ERRORJOIN:
                        endClient();
                        gestoreClient.mainWarn("Error joining the server, check your info");
                        break;
                    default:
                        break;
                }
            } catch (IOException ex) {
                
            }

        }
    }

    //##################PUBLIC METHODS#################################
    
    /**
     * Disconnect the client from the server
     */
    public void disconnect(){
        sendToServer("DISCONNECT§"+USERNAME);
        endClient();
    }
    
    
    //##################PRIVATE METHODS#################################
    /**
     * Terminate the client thread and close the socket
     */
    private void endClient(){
        this.terminated = true;
        this.datagramSocket.close();
    }    
    
    /**
     * Create the datagram socket
     * @throws SocketException if error on creation
     */
    private void initClient() throws SocketException {
        datagramSocket = new DatagramSocket(LOCALPORT);
        datagramSocket.setSoTimeout(5000);
    }
    
    /**
     * Send a message to the server
     * @param message message to send
     */
    public void sendToServer(String message){
        try {
            byte[] buffer = message.getBytes();
            InetAddress receiverAddress = InetAddress.getByName(IP);

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, SERVERPORT);
            datagramSocket.send(packet);
        } catch (IOException ex) {
            
        }
    }
}
