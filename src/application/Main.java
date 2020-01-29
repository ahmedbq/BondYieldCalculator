package application;
	
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import calculator.Calculator;
import calculator.INPUT;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// Set up page architecture
			BorderPane borderPane = new BorderPane();
			GridPane gridPane = new GridPane();
			borderPane.setCenter(gridPane);
			
			// Put formula image on top
			Image formulaImage = new Image("resources/Formula.png", false);
			ImageView banner = new ImageView(formulaImage);
			banner.setFitWidth(820.0);
			banner.setPreserveRatio(true);
			borderPane.setTop(banner);
			
			// Create the HOW TO description on the bottom
			Label howTo = new Label(" You can press ENTER on the given field you want to calculate OR you can use the buttons.");
			howTo.setPadding(new Insets(10,10,10,10));
			borderPane.setBottom(howTo);
			
			// Create instance of class which has the mathematical functionality
			Calculator calc = new Calculator();
			
			// Create all of the input text fields
			createInputTextFields(gridPane, calc);
			
			// Create barrier between text fields and buttons for space
			Region space = new Region();
			space.setPadding(new Insets(20, 20, 20, 20));
			gridPane.add(space, 1, 3);
			
			// Create Price and Yield Button
			createButton(gridPane, calc, INPUT.PRICE);
			createButton(gridPane, calc, INPUT.RATE);
			
			// Create the scene
			Scene scene = new Scene(borderPane,800,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setFill(Color.rgb(181,198,145));
			
			// Sets the scene on the stage (what you currently see)
			primaryStage.setTitle("Bond Yield Calculator");
			primaryStage.setScene(scene);
			// Disabling the resize button because the application is not responsive yet
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Creates button and sets its logic upon clicking.
	 */
	private void createButton(GridPane gridPane, Calculator calc, INPUT field) {
		String displayName = "Calculate " + field.toString();
		Button button = new Button(displayName);
		button.setPadding(new Insets(10, 10, 10, 10));
		button.setTooltip(new Tooltip(displayName));
		
		TextField input = (TextField) gridPane.getChildren().stream()
															.filter(f -> f.getId() != null)
															.filter(f -> f.getId().equals(field.toString())).findAny().get();
		
		switch(field) {
			case PRICE:
				gridPane.add(button, 3, 4);
				break;
		
			case RATE:
				gridPane.add(button, 1, 4);
				break;
				
			default: 
				break;
		}
		
		setButtonPressHandler(gridPane, calc, field, button, input);
	}

	private void createInputTextFields(GridPane gridPane, Calculator calc) {
		// Loop through the EnumSet and generate an input text field
		// and description for all of them
		EnumSet.allOf(INPUT.class)
			   .forEach(e -> {
				   // Create an input text field
				   TextField text = new TextField();
				   text.setPadding(new Insets(10, 10, 10, 10));
				   text.setTooltip(new Tooltip(e.toString()));
				   text.setId(e.toString());
				   
				   // Handles what happens when you press enter on an input field
				   setEnterKeyHandler(gridPane, calc, e, text);
				   
				   // Handles input which are not numbers or decimals
				   // Also limits the amount of decimals
				   setInputHandling(e, text);
				   
				   // Creates label underneath the input box
				   // For e.g. YEARS
				   Label desc = new Label();
				   desc.setText(e.toString());
				   
				   // Adds and aligns the input and description on the grid
				   gridPane.add(text, e.ordinal(), 1);
				   gridPane.add(desc, e.ordinal(), 2);
				   GridPane.setHalignment(desc, HPos.CENTER);
			   });
	}

	/*
	 * This sets what happens when a new value is put onto the screen.
	 * Also makes sure any non-numerical characters are cut out and 
	 * not included.
	 */
	private void setInputHandling(INPUT e, TextField text) {
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
	}
	
	/*
	 * This handles what happens when the user presses the enter key on an input field.
	 * This is an alternative to using the button on the screen.
	 */
	private void setButtonPressHandler(GridPane gridPane, Calculator calc, INPUT field, Button button, TextField input) {
		button.setOnMouseClicked(new EventHandler<MouseEvent>() {
			   @Override
			   public void handle(MouseEvent me) {
					   // When enter is clicked for the relevant fields,
					   // it checks if the other fields are filled out 
					   // and then it can safely calculate the input field
					   switch(field) {
					   		case PRICE:
					   			if (areFieldsFilledOut(field, gridPane)) {
					   				double price = calc.calcPrice(gridPane);
					   				input.setText(String.valueOf(price));
					   			}
					   			
					   			break;
					   		case RATE:
					   			if (areFieldsFilledOut(field, gridPane)) {
					   				double rate = calc.calcYield(gridPane);
					   				input.setText(String.valueOf(rate));
					   			}
					   			
					   			break;
					   		default:
					   			break;
					   }
			   }
		   });
	}

	/*
	 * This handles what happens when the user presses the enter key on an input field.
	 * This is an alternative to using the button on the screen.
	 */
	private void setEnterKeyHandler(GridPane gridPane, Calculator calc, INPUT field, TextField text) {
		text.setOnKeyPressed(new EventHandler<KeyEvent>() {
			   @Override
			   public void handle(KeyEvent ke) {
				   if (ke.getCode().equals(KeyCode.ENTER)) {
					   // When enter is clicked for the relevant fields,
					   // it checks if the other fields are filled out 
					   // and then it can safely calculate the input field
					   switch(field) {
					   		case PRICE:
					   			if (areFieldsFilledOut(field, gridPane)) {
					   				double price = calc.calcPrice(gridPane);
						   			text.setText(String.valueOf(price));
					   			}
					   			
					   			break;
					   		case RATE:
					   			if (areFieldsFilledOut(field, gridPane)) {
					   				double rate = calc.calcYield(gridPane);
						   			text.setText(String.valueOf(rate));
					   			}
					   			
					   			break;
					   		default:
					   			break;
					   }
					   
				   }
			   }
		   });
	}
	
	/*
	 * This checks if the corresponding text fields which are
	 * used to calculate the field you want to calculate are 
	 * filled out. It checks if all of them are empty except the
	 * field you are on.
	 */
	public boolean areFieldsFilledOut(INPUT fieldToCalculate, GridPane gridPane) {
		// Grabs all fields besides the one you are on
		List<Node> otherFields = gridPane.getChildren()
				   .stream()
				   .filter(f -> f.getId() != null)
				   .filter(f -> !f.getId().equals(fieldToCalculate.toString()))
				   .collect(Collectors.toList());
		
		// Grabs the fields which are not filled out
		List<Node> emptyFields = otherFields.stream().filter(f -> {
									 	TextField text = (TextField) f;
									 	if (text.getText().equals("")) {
									 		return true;
									 	}
									 	return false;
									}).collect(Collectors.toList());
		
		// If there are empty fields, then display an error message
		// with the corresponding missing fields.
		if (!emptyFields.isEmpty()) {
			String message = "Fill out missing fields: "; 
			
			// Grab the names (stored in the id) 
			// of each node in the empty fields.
			for (Node node : emptyFields) {
				TextField text = (TextField) node;
				message += "\n\t" + text.getId();
			}

			// Create alert message.
			Alert error = new Alert(AlertType.INFORMATION);
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
	
	/*
	 * Main method
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
}
