package application;
	
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import calculator.Calculator;
import calculator.INPUT;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
			Calculator calc = new Calculator();
			
			// Create all of the input text fields
			EnumSet.allOf(INPUT.class)
				   .forEach(e -> {
					   TextField text = new TextField();
					   text.setTooltip(new Tooltip(e.toString()));
					   text.setId(e.toString());
					   
					   text.setOnKeyPressed(new EventHandler<KeyEvent>() {
						   @Override
						   public void handle(KeyEvent ke) {
							   if (ke.getCode().equals(KeyCode.ENTER)) {
								   switch(e) {
								   		case PRICE:
								   			if (areFieldsFilledOut(e, gridPane)) {
								   				double price = calc.calcPrice(gridPane);
									   			text.setText(String.valueOf(price));
								   			};
								   			
								   			break;
								   		case RATE:
								   			if (areFieldsFilledOut(e, gridPane)) {
								   				double rate = calc.calcYield(gridPane);
									   			text.setText(String.valueOf(rate));
								   			};
								   			
								   			break;
								   		default:
								   			break;
								   }
								   
							   }
						   }
					   });
					   
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
	
	/*
	 * This checks if the corresponding text fields which are
	 * used to calculate the field you want to calculate are 
	 * filled out. It checks if all of them are empty except the
	 * field you are on.
	 */
	public boolean areFieldsFilledOut(INPUT fieldToCalculate, GridPane gridPane) {
		List<Node> fields2 = gridPane.getChildren()
				   .stream()
				   .filter(f -> f.getId() != null)
				   .filter(f -> !f.getId().equals(fieldToCalculate.toString()))
				   .collect(Collectors.toList());
		
		Stream<Node> filledOutFieldStream = fields2.stream().filter(f -> {
											 	TextField text = (TextField) f;
											 	if (text.getText().equals("")) {
											 		return false;
											 	}
											 	return true;
											});
												
		List<Node> emptyFields = fields2.stream().filter(f -> {
									 	TextField text = (TextField) f;
									 	if (text.getText().equals("")) {
									 		return true;
									 	}
									 	return false;
									}).collect(Collectors.toList());
		
		Alert error = new Alert(AlertType.INFORMATION);
		
		long count = filledOutFieldStream.count();
		
		if (Math.toIntExact(count) != INPUT.values().length - 1) {
			String message = "Fill out missing fields: "; 
			
			System.out.println("Size of emptyFieldsNodes: " + emptyFields.size());
			System.out.println("INPUT.values().length - 1: " + (INPUT.values().length - 1));
			System.out.println("filledOutFieldStream.count(): " + count);
			for (Node node : emptyFields) {
				TextField text = (TextField) node;
				message += "\n\t" + text.getId();
			}

			error.setContentText(message);
			error.show();
			
			return false;
		}
		
		return true;
	}
	
	/*
	 * Used to replace the last occurrence of a character. Mainly used to remove
	 * any decimal point when one is already there.
	 */
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
	
    /*
     * This is used to check if a number is valid with 1 decimal point
     * if it's supposed to have a decimal, or if it's a valid number
     * which cannot have a decimal point.
     */
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
	
}
