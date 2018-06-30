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

import java.net.SocketException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class handle all the communication between the Server and the Server
 * Interface
 *
 * @author Giovanni Scanferla 5^ID
 */
public class GestoreServer {

    //##################VARIABLES#################################
    private Server server;
    private Thread serverThread;
    private ServerController serverController;

    //##################CONSTRUCTORS#################################
    /**
     * Create a new Server
     *
     * @param PORT server listening port
     * @throws Exception
     */
    public GestoreServer(int PORT) throws Exception {

        try {
            this.server = new Server(PORT, this);
            start(new Stage());
        } catch (SocketException ex) {
            throw new Exception("Error creating the server");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error creating the interface");

        }

        this.serverThread = new Thread(server, "ServerThread");
        this.serverThread.start();

    }

    //##################PUBLIC METHODS#################################
    /**
     * Write a server sentence in the server interface
     *
     * @param sentence sentence to write
     */
    public void serverSentence(String sentence) {
        serverController.writeLine(sentence);
    }

    //##################PRIVATE METHODS#################################
    /**
     * Start the server interface
     *
     * @param primaryStage
     * @throws Exception
     */
    private void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Server.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Server Console");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            server.disconnet();

        });
        serverController = fxmlLoader.getController();
    }

}
