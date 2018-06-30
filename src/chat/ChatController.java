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
package chat;

import chat.network.client.GestoreClient;
import chat.network.server.GestoreServer;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.SocketException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;

/**
 * This Class handle the MainChat Interface
 *
 * @author Giovanni Scanferla 5^ID
 */
public class ChatController {

    //##################FXML VARIABLES#################################
    @FXML
    JFXTextField tfUsername;

    @FXML
    JFXPasswordField tfPassword;

    @FXML
    JFXTextField tfServerIP;

    @FXML
    JFXTextField tfServerPort;

    @FXML
    JFXTextField tfClientPort;

    @FXML
    JFXRadioButton rbSingUp;

    @FXML
    JFXRadioButton rbSingIn;

    @FXML
    ToggleGroup toggleGroup;

    @FXML
    Label lbError;

    //##################FXML METHODS#################################
    /**
     * Start a new Client
     */
    @FXML
    public void client() {
        try {
            String username = tfUsername.getText();
            String password = tfPassword.getText();
            String serverip = tfServerIP.getText();
            String clientport = tfClientPort.getText();
            String serverport = tfServerPort.getText();
            if (checkCorrectInformation(serverip, "IP") && checkCorrectInformation(username, "INFO") && checkCorrectInformation(password, "INFO") && checkCorrectInformation(clientport, "PORT") && checkCorrectInformation(serverport, "PORT")) {
                new GestoreClient(username, password, serverip, Integer.parseInt(clientport), Integer.parseInt(serverport), getType(), this);
            } else {
                writeWarn("Check your info");
            }

        } catch (SocketException ex) {
            writeWarn(ex.getMessage());
        } catch (NumberFormatException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Start a new Server
     */
    @FXML
    public void server() {
        try {
            if (checkCorrectInformation(tfServerPort.getText(), "PORT")) {
                new GestoreServer(Integer.parseInt(tfServerPort.getText()));
            } else {
                writeWarn("Check the server port");
            }

        } catch (Exception ex) {
            writeWarn(ex.getMessage());
        }
    }

    //##################PUBLIC METHODS#################################
    /**
     * Write a warn on the label error
     *
     * @param message message to write
     */
    public void writeWarn(String message) {
        Platform.runLater(() -> {
            lbError.setText(message);
        });

    }

    //##################PRIVATE METHODS#################################
    /**
     * Return the value of the radio button
     *
     * @return REGISTER or JOIN
     */
    private String getType() {
        if (rbSingUp.isSelected()) {
            return "REGISTER";
        } else {
            return "JOIN";
        }
    }

    /**
     * Return true if the field is correct
     *
     * @param field field to check
     * @param type type of the field
     * @return true or false
     */
    private boolean checkCorrectInformation(String field, String type) {
        switch (type) {
            case "IP":
                String[] p = field.split("\\.");
                if (p.length == 4) {
                    for (String num : p) {
                        try {
                            Integer.parseInt(num);
                        } catch (Exception e) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
                break;
            case "PORT":
                try {
                    Integer.parseInt(field);
                } catch (Exception e) {
                    return false;
                }
                break;
            case "INFO":
                return !field.equals("");
        }
        return true;
    }

}
