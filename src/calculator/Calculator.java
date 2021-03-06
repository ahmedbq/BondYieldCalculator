package calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/*
 * This class stores all of the calculations used by the calculator
 */
public class Calculator {
	/*
	 * Variables used to set the accuracy of the calculations.
	 * If the price calculator sees performance issues, you can branch off
	 * a separate precision constant with 1 less zero which is still
	 * accurate enough.
	 */
	public static final int SCALE = 7;
	public static final double PRECISION = 0.00_000_001;
	
	/*
	 * Variables used to speed up or slow down the binary search algorithm
	 * so that the calculation is both fast and efficient
	 */
	private static final double SPEED_UP_INCREMENT = 0.01;
	private static final double CLOSE_SEARCH_INCREMENT = 0.1;

	/*
	 * Calculates the price given:
	 * 		1. coupon
	 * 		2. years
	 * 		3. face
	 * 		4. rate
	 */
	public double calcPrice(double coupon, int years, double face, double rate) {
		// Note: YTM is the same thing as the interest rate,
		// and it's the same thing as the discount rate
		
		double price = summateCashFlow(coupon, years, face, rate);
		
		// Setting accuracy
		return truncateDouble(price);
	}

	/*
	 * This sets the accuracy to 10^-7
	 */
	private double truncateDouble(double price) {
		return BigDecimal.valueOf(price)
				.setScale(SCALE, RoundingMode.HALF_UP)
				.doubleValue();
	}

	private double summateCashFlow(double coupon, int years, double face, double rate) {
		double price = 0.0;
		
		// Do summation years amount of time
		for (int exponent = 1; exponent <= years; exponent++) {
			price += (coupon * face) / Math.pow(1 + rate, exponent);
		}
		
		// Do summation again for the last year, but with bond price
		price += face / Math.pow(1 + rate, years);
		
		return price;
	}
	
	/*
	 * Calculates the yield given:
	 * 		1. coupon
	 * 		2. years
	 * 		3. face
	 * 		4. price
	 */
	public double calcYield(double coupon, int years, double face, double price) {
		// Note: Bond's yield is referring to rate r
		
		// Search for a rate via binary search
		// until one is found where you can plug it into the 
		// formula and the equation = Price
		double r = binarySearchForRate(0.0, 1.0, coupon, years, face, price);
		
		return truncateDouble(r);
	}

	private double binarySearchForRate(double low, double high, double coupon, int years, double face, double price) {
		double rate = Double.MAX_VALUE;
		
		while (low <= high) {
			// The mid value will be used in summateCashFlow as the denominator. The higher the mid value,
			// the lower the rate. And vice versa. The logic is made to complement this rule.
			double mid = (low + high) / 2;
			double midPrice = summateCashFlow(coupon, years, face, mid);
			
			// If the midPrice == price
			// OR the midPrice is just under price and incrementing mid
			// by precision would make it > price, return mid 
			if (midPrice == price
					  || (  (summateCashFlow(coupon, years, face, mid - PRECISION) > price)
					      && (midPrice < price))  ){
				
				return rate;
			}
			
			// If midPrice < price, that means we need to decrease the high, to increase the midPrice
			if (midPrice < price) {
				// Search is close, so slow down to be more precise
				if ((midPrice += CLOSE_SEARCH_INCREMENT) > price) {
					high -= PRECISION;
				} 
				// Search has some time, speed up
				else {
					high -= SPEED_UP_INCREMENT;	
				}
				
				rate = mid;
			
		    // If midPrice > price, that means we need to increase the low, to decrease the midPrice
			} else if (midPrice > price) {
				// Search is close, so slow down to be more precise
				if ((midPrice -= CLOSE_SEARCH_INCREMENT) < price) {
					low += PRECISION;
				}
				// Search has some time, speed up
				else {
					low += SPEED_UP_INCREMENT;
				}
				
				rate = mid;
				
			} 
		}
		
		return rate;
		
	}

	/*
	 * Grabs the gridPane and translates the fields
	 * into something that the main calcYield method can read
	 */
	public double calcYield(GridPane gridPane) {
		List<Node> fields = gridPane.getChildren()
								    .stream()
								    .filter(f -> f.getId() != null)
								    .collect(Collectors.toList());
		
		Optional<Double> coupon = Optional.of(0.0);
		Optional<Integer> years = Optional.of(0);
		Optional<Double> face = Optional.of(0.0);
		Optional<Double> price = Optional.of(0.0);
		
		coupon = fields.stream()
					   .filter(f -> f.getId().equals(INPUT.COUPON.toString()))
					   .map(m -> {
						   TextField text = (TextField) m; 
						   return Double.parseDouble(text.getText());})
					   .findAny();
		
		years = fields.stream()
				   	  .filter(f -> f.getId().equals(INPUT.YEARS.toString()))
				   	  .map(m -> {
				   		  TextField text = (TextField) m; 
				   		  return Integer.parseInt(text.getText());})
				   	  .findAny();
		
		face = fields.stream()
		   		     .filter(f -> f.getId().equals(INPUT.FACE.toString()))
		   		     .map(m -> {
		   		    	 TextField text = (TextField) m; 
		   		    	 return Double.parseDouble(text.getText());})
		   		     .findAny();
		
		price = fields.stream()
			   		  .filter(f -> f.getId().equals(INPUT.PRICE.toString()))
			   		  .map(m -> {
			   			  TextField text = (TextField) m; 
			   			  return Double.parseDouble(text.getText());})
			   		  .findAny();
		
		return calcYield(coupon.get(), years.get(), face.get(), price.get());
	}

	/*
	 * Grabs the gridPane and translates the fields
	 * into something that the main calcPrice method can read
	 */
	public double calcPrice(GridPane gridPane) {
		List<Node> fields = gridPane.getChildren()
								    .stream()
								    .filter(f -> f.getId() != null)
								    .collect(Collectors.toList());
		
		Optional<Double> coupon = Optional.of(0.0);
		Optional<Integer> years = Optional.of(0);
		Optional<Double> face = Optional.of(0.0);
		Optional<Double> rate = Optional.of(0.0);
		
		coupon = fields.stream()
					   .filter(f -> f.getId().equals(INPUT.COUPON.toString()))
					   .map(m -> {
						   TextField text = (TextField) m; 
						   return Double.parseDouble(text.getText());})
					   .findAny();
		
		years = fields.stream()
				   	  .filter(f -> f.getId().equals(INPUT.YEARS.toString()))
				   	  .map(m -> {
				   		  TextField text = (TextField) m; 
				   		  return Integer.parseInt(text.getText());})
				   	  .findAny();
		
		face = fields.stream()
		   		     .filter(f -> f.getId().equals(INPUT.FACE.toString()))
		   		     .map(m -> {
		   		    	 TextField text = (TextField) m; 
		   		    	 return Double.parseDouble(text.getText());})
		   		     .findAny();
		
		rate = fields.stream()
			   		  .filter(f -> f.getId().equals(INPUT.RATE.toString()))
			   		  .map(m -> {
			   			  TextField text = (TextField) m; 
			   			  return Double.parseDouble(text.getText());})
			   		  .findAny();
		
		return calcPrice(coupon.get(), years.get(), face.get(), rate.get());
	}

}
