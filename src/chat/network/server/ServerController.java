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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * This Class handle the Server Interface
 *
 * @author Giovanni Scanferla 5^ID
 */
public class ServerController implements Initializable {

    //##################FXML VARIABLES#################################
    @FXML
    VBox vbDisplay;

    @FXML
    BorderPane mainPane;

    //##################VARIABLES#################################
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

    //##################PUBLIC METHODS#################################
    /**
     * Write a new line to the console
     *
     * @param sentence sentence to write
     */
    public void writeLine(String sentence) {

        vbDisplay.setPadding(new Insets(10, 10, 10, 10));
        vbDisplay.setSpacing(10);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        sentence = df.format(calobj.getTime()) + "---" + sentence;

        Label label = new Label(sentence);
        label.setWrapText(true);
        label.setMinWidth(400);

        Platform.runLater(() -> {
            vbDisplay.getChildren().add(label);
        });
    }

}
