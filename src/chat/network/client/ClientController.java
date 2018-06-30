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

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This class handle the Client inteface
 * @author Giovanni Scanferla 5^ID
 */
public class ClientController implements Initializable {

    //##################FXML VARIABLES#################################
    @FXML
    BorderPane mainPane;

    @FXML
    VBox vbDisplay;

    @FXML
    JFXTextField tfMessage;

    //##################VARIABLES#################################
    private GestoreClient gestoreClient;
    private ScrollPane scroll;

    //##################IMPLEMENTED METHODS#################################
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scroll = new ScrollPane();
        scroll.setContent(vbDisplay);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPane.setRight(scroll);

        vbDisplay.heightProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                scroll.setVvalue((Double) newValue + 20.0);
            }
        });
    }

    //##################SETTERS#################################
    /**
     *
     * @param gestoreClient
     */
    public void setGestoreClient(GestoreClient gestoreClient) {
        this.gestoreClient = gestoreClient;
    }

    //##################FXML METHODS#################################
    /**
     * Send a new message to the Server and write it on the interface
     */
    @FXML
    private void sendMessage() {
        if (!tfMessage.getText().equals("")) {
            HBox hbContainer = new HBox();
            hbContainer.setPadding(new Insets(10, 10, 10, 10));

            Label lbMessage = new Label(tfMessage.getText());
            lbMessage.setMinSize(190, 50);
            lbMessage.setMaxSize(190, Double.MAX_VALUE);
            lbMessage.setAlignment(Pos.CENTER_RIGHT);
            lbMessage.setWrapText(true);
            lbMessage.setPadding(new Insets(10, 10, 10, 10));
            lbMessage.setStyle("-fx-background-color: #c9c9c9;");

            Label lbVoid = new Label();
            lbVoid.setMinSize(190, 50);

            Platform.runLater(() -> {
                hbContainer.getChildren().add(lbVoid);
                hbContainer.getChildren().add(lbMessage);
                vbDisplay.getChildren().add(hbContainer);
            });

            gestoreClient.sendToServer("CHAT§" + gestoreClient.getUSERNAME() + "§" + tfMessage.getText());
            tfMessage.setText("");
        }
    }

    //##################PUBLIC METHODS#################################
    /**
     * Write a warn label whith the text and the color parsed
     * @param warn sentence to write
     * @param color background color
     */
    public void addWarn(String warn, String color) {
        Label lbError = new Label(warn);
        lbError.setMinSize(400, 50);
        lbError.setAlignment(Pos.CENTER);
        lbError.setWrapText(true);
        lbError.setStyle("-fx-background-color: " + color + "; -fx-text-fill: WHITE;");

        Platform.runLater(() -> {
            vbDisplay.getChildren().add(lbError);
        });

    }

    /**
     * Add a recived message to the Client interfac
     * @param user user owner of the message
     * @param message message to write
     */
    public void addRecivedMessage(String user, String message) {
        HBox hbContainer = new HBox();
        hbContainer.setPadding(new Insets(10, 10, 10, 10));

        VBox vbMessage = new VBox();
        vbMessage.setMinSize(190, 50);
        vbMessage.setMaxSize(190, Double.MAX_VALUE);
        vbMessage.setAlignment(Pos.CENTER_LEFT);
        vbMessage.setPadding(new Insets(10, 10, 10, 10));
        vbMessage.setStyle("-fx-background-color: #353535;");

        Label lbUser = new Label(user);
        /*lbMessage.setMinSize(190, 50);
        lbMessage.setMaxSize(190, Double.MAX_VALUE);*/
        lbUser.setAlignment(Pos.CENTER_LEFT);
        lbUser.setWrapText(true);
        //lbUser.setPadding(new Insets(10, 10, 10, 10));
        lbUser.setStyle("-fx-background-color: #353535; -fx-text-fill: WHITE; -fx-font-weight: bold;");

        Label lbMessage = new Label(message);
        /*lbMessage.setMinSize(190, 50);
        lbMessage.setMaxSize(190, Double.MAX_VALUE);*/
        lbMessage.setAlignment(Pos.CENTER_LEFT);
        lbMessage.setWrapText(true);
        //lbMessage.setPadding(new Insets(10, 10, 10, 10));
        lbMessage.setStyle("-fx-background-color: #353535; -fx-text-fill: WHITE;");

        Label lbVoid = new Label();
        lbVoid.setMinSize(190, 50);

        Platform.runLater(() -> {
            vbMessage.getChildren().addAll(lbUser, lbMessage);
            hbContainer.getChildren().add(vbMessage);
            hbContainer.getChildren().add(lbVoid);
            vbDisplay.getChildren().add(hbContainer);
        });

    }

}
