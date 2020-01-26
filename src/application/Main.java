package application;
	
import java.util.EnumSet;
import java.util.regex.Pattern;

import org.junit.platform.commons.util.StringUtils;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
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
					   text.textProperty().addListener(new ChangeListener<String>() {
						   @Override
						   public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							   
							   // Years cannot have a decimal value
							   if (e.name().equals("YEARS")) {
								   text.setText(newValue.replaceAll("[^-?\\d+(\\d+)?]", ""));
							   } 
							   // Otherwise decimals are allowed
							   else {
								   // If there are more than 1 decimal points, remove the latest one
								   if(newValue.chars().filter(ch -> ch == '.').count() > 1) {
									   text.setText(replaceLast(newValue, "[.]", ""));
								   }
								   // Otherwise user is trying to put in a non-numeric character. Remove it.
								   else {
									   text.setText(newValue.replaceAll("[^-?\\d+(\\.\\d+)?]", ""));   
								   }
							   }
						   }
					   });
					   
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
	
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
	
	public static boolean isNumeric(String strNum, boolean hasDecimal) {
	    if (strNum == null) {
	        return false;
	    }
	    
		String regex = hasDecimal ? "-?\\d+(\\.\\d+)?" : "-?\\d+(\\d+)?";
		Pattern pattern = Pattern.compile(regex);
		
		return pattern.matcher(strNum).matches();
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
