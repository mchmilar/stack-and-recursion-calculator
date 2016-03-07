import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class SecondArithmeticRecursion {

	public static void main(String[] args) {
		
		PrintWriter output = null;
		Scanner input = null;
		String expression;
		ArrayList<String> outputArrayList = new ArrayList<String>();
		
		try {
			input = new Scanner(new FileInputStream("expressions.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Error opening expressions.txt");
			System.exit(0);
		}
		
		try {
			while (input.hasNextLine()) {
				
				expression = input.nextLine().replaceAll("\\s", "");
				outputArrayList.add(expression);
				outputArrayList.add(evaluateEquation(expression));
			}
		} catch (NoSuchElementException e) {
			System.out.println("ERROR: Tried to read a line that doesn't exist");
			System.exit(0);
		} catch (IllegalStateException e) {
			System.out.println("ERROR: Input stream is close");
			System.exit(0);
		}
		input.close();
		
		
		//output to file
		try {
			output = new PrintWriter(new FileOutputStream("expressionsOutput.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Error opening file");
			System.exit(0);
		}
		for (String s: outputArrayList) {
			output.println(s);
		}
		output.close();
	}
	
	
	private static String evaluateEquation(String expression) {
		expression = expression.replaceAll("\\s", "");
		String[] parsedExpression = expression.split("(?=[=!]=|[><])|(?<=[=!><]=|[><](?!=))");
		boolean result = true;
	
		
		for (int i = 0; i < parsedExpression.length; i++) {
			if (!isEvaluator(parsedExpression[i]))
				parsedExpression[i] = resolveExpression(parsedExpression[i]);
		}
		
		for (int i = 0; i < parsedExpression.length -1 ; i = (i + 2)) {
			
			if (!isTrueSubExpression(parsedExpression[i+1], parsedExpression[i], parsedExpression[i+2])) {
				result = false;
				break;
			}				
		}
		return result + "";
		
	}
	
	


	private static boolean isTrueSubExpression(String operator, String sLHS,
			String sRHS) {

		double LHS = Double.parseDouble(sLHS);
		double RHS = Double.parseDouble(sRHS);
		if (operator.equals("=="))		return LHS == RHS;
		else if (operator.equals("!="))	return LHS != RHS;
		else if (operator.equals(">"))	return LHS > RHS;		
		else if (operator.equals(">="))	return LHS >= RHS;
		else if (operator.equals(">"))	return LHS > RHS;
		else return LHS >= RHS;	
	
	}


	private static boolean isEvaluator(String s) {
		return s.equals("==") || s.equals("!=") || s.equals(">=") || s.equals("<=") || s.equals(">") || s.equals("<");
	}
	
	private static String resolveExpression(String expression) {
		double result = 0;
				
		// Base case
		if (!containOps(expression))
			return expression;
		else {
			
			
			// Resolve all parentheses
			if (expression.contains("("))
				expression = resolveBrackets(expression);
			
			
			// Separate terms by + or -
			if (expression.contains("+") || expression.contains("-"))
				expression = resolveTerms(expression);
			
			
			//Calculate * and /
			if (expression.contains("*") || expression.contains("/"))
				expression = resolveMultAndDiv(expression);
			
			// Calculate ^
			if (expression.contains("^"))
				expression = resolvePower(expression);
			
			
			// Calculate !
			if (expression.contains("!"))
				expression = resolveFactorial(expression);
				
			return expression;
		}
		
			
	}
	
	
	private static String resolveFactorial(String e) {
		int rightIndex = e.length() - 1;
		boolean termOpFound = e.charAt(rightIndex) == '!' ;
		
		while (!termOpFound) {			
			rightIndex--;
			termOpFound = e.charAt(rightIndex) == '!';
		}
			
		
		String LHS = e.substring(0, rightIndex);
		
		return doOp(e.charAt(rightIndex), resolveExpression(LHS));	
	}


	private static String resolvePower(String e) {
		int rightIndex = e.length() - 1;
		boolean termOpFound = e.charAt(rightIndex) == '^' ;
		
		while (!termOpFound) {			
			rightIndex--;
			termOpFound = e.charAt(rightIndex) == '^';
		}
			
		
		String LHS = e.substring(0, rightIndex);
		String RHS = e.substring(rightIndex + 1);
		
		return doOp(e.charAt(rightIndex), resolveExpression(LHS), resolveExpression(RHS));	
	}


	private static String resolveMultAndDiv(String e) {
		int leftIndex = 0;
		boolean termOpFound = e.charAt(leftIndex) == '*' || e.charAt(leftIndex) == '/';
		
		while (!termOpFound) {			
			leftIndex++;
			termOpFound = e.charAt(leftIndex) == '*' || e.charAt(leftIndex) == '/';
		}
			
		
		String LHS = e.substring(0, leftIndex);
		String RHS = e.substring(leftIndex + 1);
		
		return doOp(e.charAt(leftIndex), resolveExpression(LHS), resolveExpression(RHS));	
			
	}


	// Should not be in this method if e does not contain a binary + or -
	private static String resolveTerms(String e) {
		
		int leftIndex = 0;
		boolean termOpFound = e.charAt(leftIndex) == '+' || (e.charAt(leftIndex) == '-' && (leftIndex != 0 && !isOperator(e.charAt(leftIndex -1))));
		
		while (!termOpFound) {			
			leftIndex++;
			termOpFound = e.charAt(leftIndex) == '+' || (e.charAt(leftIndex) == '-' && (leftIndex != 0 && !isOperator(e.charAt(leftIndex -1))));
		}
			
		
		String LHS = e.substring(0, leftIndex);
		String RHS = e.substring(leftIndex + 1);
		
		return doOp(e.charAt(leftIndex), resolveExpression(LHS), resolveExpression(RHS));	
		
	}
	
	private static String doOp(char op, String EXPR) {
		String result = "";
		if (op == '!')
			result = factorial(Double.parseDouble(EXPR)) + "";
		return result;
	}
	
	private static String doOp(char op, String LHS, String RHS) {
		if (op == '+')
			return (Double.parseDouble(LHS) + Double.parseDouble(RHS)) + "";
		else if (op == '-')
			return (Double.parseDouble(LHS) - Double.parseDouble(RHS)) + "";
		else if (op == '*')
			return (Double.parseDouble(LHS) * Double.parseDouble(RHS)) + "";
		else if (op == '/')
			return (Double.parseDouble(LHS) / Double.parseDouble(RHS)) + "";
		else if (op == '^')
			return pow(Double.parseDouble(LHS), Double.parseDouble(RHS)) + "";
		else
			return "default";
	}
	
	
	private static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '!';
	}
	
	private static String resolveBrackets(String expression) {
		int leftIndex = 0, rightIndex = expression.length() - 1;
		
		// Get index of outside opening bracket
		while (expression.charAt(leftIndex) != '(')
			leftIndex++;
		// Get index of outside closing bracket
		while (expression.charAt(rightIndex) != ')')
			rightIndex--;
		
		
		// Returns the expression with everything inside brackets being resolved by a recursive call to resolveExpression
		return expression.substring(0, leftIndex) + resolveExpression(expression.substring(leftIndex + 1, rightIndex)) + expression.substring(rightIndex + 1);
		
	}
	
	private static boolean containOps(String e) {
		return e.contains("+") || e.contains("-") || e.contains("*") || e.contains("/") || e.contains("!") || e.contains("^") || e.contains("(") || e.contains(")");
	}
	
	
	private static double square( double x ) { 
		return x * x; 
		}
	// meaning of 'precision': the returned answer should be base^x, where
//	                         x is in [power-precision/2,power+precision/2]
	private static double pow( double base, double power, double precision )
	{   
		if (power == 0.0) return 1;
		if (power == Math.floor(power)) {
			int intPower = (int)power;
			int total = (int)base;
			for (int i = 1; i < intPower; i++)
				total *= base;
			return total;
		}
	   if ( power < 0 ) return 1 / pow( base, -power, precision );
	   if ( power >= 10 ) return square( pow( base, power/2, precision/2 ) );
	   if ( power >= 1 ) return base * pow( base, power-1, precision );
	   if ( precision >= 1 ) return Math.sqrt( base );
	   return Math.sqrt( pow( base, power*2, precision*2 ) );
	}
	private static double pow( double base, double power ) { return pow( base, power, .00000000001 ); }
	
	
	private static double factorial (double x) {
		if (x == Math.floor(x)) {
			double product = 1;
			for (int i = 2; i <= x; i++)
				product *= i;
			return product;
		} else
			return gamma(x);
	}
	
	
	// Stirling approximation for real gamma function
	// Used when factorial is called on a real number
	private static double gamma(double x) {
		double stirlingSeries = 1 + (1/(12*x)) + 1/(288*Math.pow(x,2)) - 139/(51840*Math.pow(x,3)) - 571/(2488320*Math.pow(x,4));
		return Math.sqrt(2 * Math.PI * x) * Math.pow(x/Math.E,x) * stirlingSeries ;
		
	}
	
	
	

	
}
