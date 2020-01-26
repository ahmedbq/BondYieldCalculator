package calculator;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class CalculatorTest {
	
	Calculator calc = new Calculator();
	
	@Test
	public void calcPriceTest() {
		double results = calc.calcPrice(0.10, 5, 1000, 0.15);
		
		Assert.assertEquals(832.3922451, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcPriceTest2() {
		double results = calc.calcPrice(0.15, 5, 1000, 0.15);
		
		Assert.assertEquals(1000.0000000, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcPriceTest3() {
		double results = calc.calcPrice(0.10, 5, 1000, 0.08);
		
		Assert.assertEquals(1079.8542007, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcPriceTest4() {
		double results = calc.calcPrice(0.10, 30, 1000, 0.19);
		
		Assert.assertEquals(528.8807463, results, Calculator.PRECISION);
	}

	@Test
	public void calcYieldTest() {
		double results = calc.calcYield(0.10, 5, 1000, 832.4);
		
		Assert.assertEquals(0.1499974, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcYieldTest2() {
		double results = calc.calcYield(0.10, 5, 1000, 1000);
		
		Assert.assertEquals(0.1000000, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcYieldTest3() {
		double results = calc.calcYield(0.10, 5, 1000, 1079.85);
		
		Assert.assertEquals(0.0800010, results, Calculator.PRECISION);
	}
	
	@Test
	public void calcYieldTest4() {
		double results = calc.calcYield(0.10, 30, 1000, 528.8807463);
		
		Assert.assertEquals(0.1900000, results, Calculator.PRECISION);
	}
}
