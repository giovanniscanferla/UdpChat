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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class of the project
 *
 * @author Giovanni Scanferla 5^ID
 */
public class Chat extends Application {

    //##################IMPLEMENTED METHODS#################################
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Chat.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("MULTICHAT");
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(800);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    //##################MAIN#################################
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
