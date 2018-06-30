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

import chat.ChatController;
import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class handle all the communication between the Client and the Client Interface
 * @author Giovanni Scanferla 5^ID
 */
public class GestoreClient {

    //##################CONSTRUCTORS#################################
    private final String USERNAME;
    private final String PASSWORD;
    private final String SERVERIP;
    private final int CLIENTPORT;
    private final int SERVERPORT;

    //##################VARIABLES#################################
    private Client client;
    private String type;
    private ChatController chatController;
    private ClientController clientController;
    private Thread clientThread;

    //##################CONSTRUCTORS#################################
    /**
     * Client constructor
     *
     * @param USERNAME client username
     * @param PASSWORD client password
     * @param SERVERIP server ip
     * @param CLIENTPORT client port
     * @param SERVERPORT server port
     * @param type type of client connection(JOIN or REGISTER)
     * @param chatController main window controller
     * @throws SocketException error instantiating the client
     */
    public GestoreClient(String USERNAME, String PASSWORD, String SERVERIP, int CLIENTPORT, int SERVERPORT, String type, ChatController chatController) throws SocketException {
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
        this.SERVERIP = SERVERIP;
        this.CLIENTPORT = CLIENTPORT;
        this.SERVERPORT = SERVERPORT;
        this.chatController = chatController;
        this.type = type;
        startClient();
        serverInteraction();
    }

    
    //##################GETTERS#################################
    
    public String getUSERNAME() {
        return USERNAME;
    }

    //##################PUBLIC METHODS#################################
    
    /**
     * Init the Client interface
     */
    public void startClientInterface() {
        Platform.runLater(() -> {
            try {
                start(new Stage());
            } catch (IOException ex) {
                Logger.getLogger(GestoreClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * Send a message to the server
     * @param message message to send
     */
    public void sendToServer(String message) {
        client.sendToServer(message);
    }
    
    private void startClient() throws SocketException {
        client = new Client(SERVERPORT, CLIENTPORT, SERVERIP, USERNAME, this);
        clientThread = new Thread(client, USERNAME + "-Thread");
        clientThread.start();
    }
    
    /**
     * Write a warn info to the main pane
     * @param sentence warn to write
     */
    public void mainWarn(String sentence) {
        chatController.writeWarn(sentence);
    }

    /**
     * Write an info in the client controller
     * @param color background color of the info
     * @param warn warn to write
     */
    public void writeInfo(String color, String warn) {
        clientController.addWarn(warn, color);
    }

    /**
     * Write a new message recived from the server to the client interface
     * @param username message username
     * @param recived message recived
     */
    public void writeMessage(String username, String recived) {
        clientController.addRecivedMessage(username, recived);
    }

    //##################PRIVATE METHODS#################################
    
    /**
     * Register or join the server
     */
    private void serverInteraction() {
        if (type.equals("REGISTER")) {
            sendToServer("REGISTER§" + USERNAME + "§" + PASSWORD);
        } else {
            sendToServer("JOIN§" + USERNAME + "§" + PASSWORD);
        }
    }
    
    /**
     * Start the client interface
     * @param primaryStage
     * @throws Exception 
     */
    private void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Client: " + USERNAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(600);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            clientThread.interrupt();
            client.disconnect();

        });
        clientController = fxmlLoader.getController();
        clientController.setGestoreClient(this);
    }

    

}
