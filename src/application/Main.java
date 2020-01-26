package application;
	
import java.util.EnumSet;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane borderPane = new BorderPane();
			GridPane gridPane = new GridPane();
			
			// Create all of the input text fields
			EnumSet.allOf(INPUT.class)
				   .forEach(e -> {
					   TextField text = new TextField();
					   text.setTooltip(new Tooltip(e.toString()));
					   
					   gridPane.add(text, e.ordinal(), 1);
					   
					   Label desc = new Label();
					   desc.setText(e.toString());
					   
					   gridPane.add(desc, e.ordinal(), 2);
					   GridPane.setHalignment(desc, HPos.CENTER);
				   });
			
			
			
			borderPane.setCenter(gridPane);
			Scene scene = new Scene(borderPane,800,800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setFill(Color.rgb(181,198,145));
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public enum INPUT {
		PRICE,
		COUPON,
		YEARS,
		FACE,
		RATE
	}
}
