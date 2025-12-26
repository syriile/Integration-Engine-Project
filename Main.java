import java.io.*;
import java.util.*;
public class Main {
    // -------------------------------------------------------------
    // static variables
    // -------------------------------------------------------------
    private static String filename;
    private static BinTree<Term> tree;
    private static Double upperBound;
    private static Double lowerBound;

    // -------------------------------------------------------------
    // Main method
    // -------------------------------------------------------------
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a filename: ");
        filename = sc.next();
        readFile(filename);
        sc.close();
    }

    // -------------------------------------------------------------
    // readFile method to read contents the contents of the file,
    // then parse each line, build a tree, then integrate the tree.
    // finally print the result of the list after integration
    // -------------------------------------------------------------
    private static void readFile(String filename) {
        try(Scanner fs = new Scanner(new File(filename))) {
            while(fs.hasNextLine()) {
                String line = fs.nextLine();
                if(line.isEmpty()) continue; // skip if line is empty
                String expression = parseLine(line); // create expression by parsing the current line
                tree = new BinTree<>();
                tree = buildTree(expression); // use expression to build tree
                List<Term> integrated = integrateTree(tree); // integrate each expression
                printResult(integrated); // print result
            }
        } catch(FileNotFoundException e) { // Catch FileNotFound Exception for invalid filename input
            System.out.println("Caught: Could not open file - (" + filename + ")");
        }
    }

    // -------------------------------------------------------------
    // parseLine helper method 
    // -------------------------------------------------------------
    private static String parseLine(String line) {
        line = cleanLine(line); // use cleanLine helper to clean the expression if needed
        setBounds(line); // sets bounds for definite integrals
        String expression = setExpression(line); // sets the expression for all integrals
        return expression;
    }

    // -------------------------------------------------------------
    // cleanLine helper method to remove all trailing and leading spaces.
    // replace multiple spaces with just 1 space (if applicable)
    // then, remove "dx"
    // -------------------------------------------------------------
    private static String cleanLine(String line) {
        line = line.replace("–", "-").replace("—", "-");
        line = line.trim();
        int dxChecker = line.indexOf("dx"); // removes "dx" 
        if (dxChecker != -1)
            line = line.substring(0, dxChecker).trim();
        while(line.contains("  ")) 
            line = line.replace("  ", " "); // replaces 2 spaces with just 1 space
        line = line.replace(" ^", "^"); // replaces spaces before "^" if applicable
        line = line.replace("^ ", "^");
        line = line.replace("- ", "-"); // replaces spaces after "-"
        return line;
    }

    // -------------------------------------------------------------
    // setBounds helper method to set the bounds for upper/lower (left and right) 
    // -------------------------------------------------------------
    private static void setBounds(String line) {
        upperBound = null;
        lowerBound = null;
        if(!line.contains("|")) return;

        String[] parts = line.split("\\|"); // splits into left and right
        String left = parts[0].trim(); // Left Side Bounds
        if (!left.isEmpty()) { // if left side is not empty, then set lower bound
            try {
                lowerBound = Double.parseDouble(left);
            } catch (NumberFormatException e) {}
        }
        if (parts.length > 1) { // Right Side Bounds
            String right = parts[1].trim();
            String[] tokens = right.split("\\s+");
            if (tokens.length > 0) {
                try {
                    upperBound = Double.parseDouble(tokens[0]);
                } catch (NumberFormatException e) {}
            }
        }
    }   
  
    // -------------------------------------------------------------
    // setExpression helper method to trim the line of 
    // any extra spaces or any extra symbols. 
    // -------------------------------------------------------------
    private static String setExpression(String line) {
        int dx = line.indexOf("dx");
        if (dx != -1) line = line.substring(0, dx).trim();

        int index = line.indexOf('|');
        if (index == -1) return line.trim();
        String left = line.substring(0, index).trim(); // split into left and right
        String right = line.substring(index + 1).trim();
        if (!left.isEmpty()) {
            String[] parts = right.split("\\s+", 2);
            if (parts.length > 1 && parts[0].matches("-?\\d+(\\.\\d+)?")) {
                right = parts[1].trim();
            }
        }
        return right;
    }

    // -------------------------------------------------------------
    // buildTree helper method to construct a binary search tree
    // -------------------------------------------------------------
    private static BinTree<Term> buildTree(String expression) {
        BinTree<Term> tree = new BinTree<>();
        expression = expression.replace(" ", ""); // replace any spaces with no spaces
        if(!expression.startsWith("-") && !expression.startsWith("+")) // make sure the expression starts with + or -
            expression = "+" + expression;

        List<String> termsList = splitTerms(expression);

        for(String t: termsList) {
            Term newTerm = createTerm(t);
            Term found = tree.search(newTerm);
            if(found != null)
                found.setCoefficient(found.getCoefficient() + newTerm.getCoefficient());
            else 
                tree.insert(newTerm);
        }
        return tree;
    }

    // -------------------------------------------------------------
    // splitTerms helper method to split each term into an ArrayList
    // -------------------------------------------------------------
    private static List<String> splitTerms(String expression) {
        List<String> termsList = new ArrayList<>();
        String cur = "";
        for(int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if((c == '+' || c == '-') && i != 0 && expression.charAt(i - 1) != '^') { // ignore '-', '+', '^'
                termsList.add(cur);
                cur = "" + c; // start new term with the sign
            } else 
                cur += c;
        }
        if(!cur.isEmpty()) termsList.add(cur);
        return termsList;
    }

    // -------------------------------------------------------------
    // createTerm helper method to create each term, and as well as
    // parse each trig/polynomial term
    // -------------------------------------------------------------
    private static Term createTerm(String t) {
        t = t.trim();
        if(t.isEmpty()) return null;    
        if(t.contains("sin") || t.contains("cos"))
            return parseTrig(t);
        return parsePolynomial(t);
    }

    // -------------------------------------------------------------
    // parsePolynomial helper method to parse each polynomial term
    // -------------------------------------------------------------
    private static Term parsePolynomial(String t) {
        t = t.replace("–", "-").replace("—", "-").trim();
        double coefficient;
        int exponent;
        if(t.contains("x")) { // coefficient process
            int index = t.indexOf('x');
            String coeff = t.substring(0, index);
            if(coeff.equals("") || coeff.equals("+"))
                coefficient = 1;
            else if(coeff.equals("-"))
                coefficient = -1;
            else
                coefficient = Double.parseDouble(coeff);

            if(t.contains("^")) { // exponent process
                int index2 = t.indexOf('^');
                exponent = Integer.parseInt(t.substring(index2 + 1).trim());
            } else 
                exponent = 1;
        } else {
            coefficient = Double.parseDouble(t);
            exponent = 0;
        }
        return new Term(coefficient, exponent);
    }

    // -------------------------------------------------------------
    // parseTrig helper method to parse each trig term
    // -------------------------------------------------------------
    private static Term parseTrig(String t) {
        t = t.replace(" ", ""); // replace any spaces with no space
        String trigType;
        if(t.contains("sin")) trigType = "sin";
        else trigType = "cos";

        double coefficient = 1.0;
        int innerCoefficient = 1;
        int trigIndex = t.indexOf(trigType);
        String outside = t.substring(0, trigIndex).trim();
        if(outside.equals("-")) coefficient = -1;
        else if(!outside.equals("") && !outside.equals("+")) 
            coefficient = Double.parseDouble(outside);

        int x = t.indexOf('x', trigIndex);
        if(x != -1) {
            String inside = t.substring(trigIndex + trigType.length(), x).trim();
            if(!inside.equals("")) 
                innerCoefficient = Integer.parseInt(inside);
        }
        return new Term(coefficient, trigType, innerCoefficient);
    }

    // -------------------------------------------------------------
    // recursive integrateTree method to integrate the tree
    // -------------------------------------------------------------
    private static List<Term> integrateTree(BinTree<Term> tree) {
        List<Term> integrated = new ArrayList<>();
        integrateHelper(tree.getRoot(), integrated);
        return integrated;
    }
    private static void integrateHelper(Node<Term> node, List<Term> integrated) {
        if(node == null) return;
        integrateHelper(node.getLeft(), integrated);
        Term t = node.getData();
        if(t.isTrig()) // integrate trig terms
            integrated.add(integrateTrigTerm(t));
        else { // integrate polynomial terms
            Term polynomial = integratePolynomialTerm(t);
            if (polynomial != null) integrated.add(polynomial);
        }
        integrateHelper(node.getRight(), integrated);
    }

    // -------------------------------------------------------------
    // integratePolynomialTerm helper method to integrate polynomial terms
    // -------------------------------------------------------------
    private static Term integratePolynomialTerm(Term t) {
        int exp = t.getExponent();
        double coeff = t.getCoefficient(); 
        if(exp == -1) return null;
        
        int newExp = exp + 1;
        double newCoeff = coeff / (exp + 1.0);
        return new Term(newCoeff, newExp);
    }

    // -------------------------------------------------------------
    // integrateTrigTerm helper method to integrate trig terms
    // -------------------------------------------------------------
    private static Term integrateTrigTerm(Term t) {
        double c = t.getCoefficient();
        int x = t.getInnerCoefficient();
        String trig = t.getTrigType();
        if(trig.equals("sin"))
            return new Term(-c/x, "cos", x);
        else if(trig.equals("cos")) 
            return new Term(c/x, "sin", x);
        return null;
    }

    // -------------------------------------------------------------
    // printResult helper method to format the new integrated expression 
    // (format to 3 decimal places if applicable) [only in final submission for definite integrals]
    // -------------------------------------------------------------
    private static void printResult(List<Term> terms) {
        if(lowerBound == null || upperBound == null) { // indefinite integral printing
            String formatted = formatExpression(terms ,null);
            System.out.println(formatted);
        } else { // definite integral printing
            double definite = evaluateDefinite(terms);
            String formatted = formatExpression(terms, definite);
            System.out.println(formatted);
        }
    }

    // -------------------------------------------------------------
    // formatExpression method to format the integrated expressions
    // (including fractions)
    // -------------------------------------------------------------
    private static String formatExpression(List<Term> terms, Double definiteValue) {
        List<Term> polynomials = new ArrayList<>();
        List<Term> trig = new ArrayList<>();
        splitType(terms, polynomials, trig);
        polynomials.sort((a, b) -> b.getExponent() - a.getExponent()); // sorting polynomial terms
        trig.sort((a, b) -> { // sorting trig terms
            int cmp = a.getTrigType().compareTo(b.getTrigType());
            if (cmp != 0) return cmp;
            return Integer.compare(a.getInnerCoefficient(), b.getInnerCoefficient());
        });
        String formatted = "";
        boolean nonZero = false; // checking if at least 1 non-zero term exists

        for(Term t: polynomials) { // formatting polynomial terms
            if(t.getCoefficient() == 0) continue; // skips all terms with 0 as coefficient
            nonZero = true; 
            formatted += formatPolynomials(t, formatted);
        }

        for(Term t: trig) { // formatting trig terms
            if(t.getCoefficient() == 0) continue;
            nonZero = true;
            formatted += formatTrig(t, formatted);
        }

        if(!nonZero) formatted = "0"; // if nonZero is still false, then print 0 for entire expression
        if(definiteValue == null) formatted += " + C"; // print + C after if the function had a null definite value.
        else formatted += String.format(", %.0f|%.0f = %.3f", lowerBound, upperBound, definiteValue);
        return formatted;
    }

    // -------------------------------------------------------------
    // splitType helper method to split each term into their 
    // respective group (polynomial or trig)
    // -------------------------------------------------------------
    private static void splitType(List<Term> terms, List<Term> polynomials, List<Term> trig) {
        for(Term t: terms) {
            if(t.isTrig()) trig.add(t);
            else polynomials.add(t);
        }
    }

    // -------------------------------------------------------------
    // formatPolynomials to format polynomial terms only
    // -------------------------------------------------------------
    private static String formatPolynomials(Term t, String formatted) {
        double coeff = t.getCoefficient();
        int exp = t.getExponent();
        String part = "";
        if (!formatted.isEmpty()) { // for terms that are not the first term
            if (coeff > 0) part += " + ";
            else part += " - ";
        } 

        double absoluteCoeff = Math.abs(coeff);
        int[] fraction = convertToFraction(absoluteCoeff);
        int numerator = Math.abs(fraction[0]);
        int denominator = fraction[1];
        boolean isFraction = (denominator != 1);
        String stringCoefficient;

        if (isFraction) {
            if (formatted.isEmpty() && coeff < 0)
                stringCoefficient = "(-" + numerator + "/" + denominator + ")";
            else
                stringCoefficient = "(" + numerator + "/" + denominator + ")";
        } else {
            if (formatted.isEmpty() && coeff < 0)
                stringCoefficient = "-" + numerator;
            else
                stringCoefficient = (numerator == 1 ? "" : String.valueOf(numerator));
        }

        if (exp == 0)
            part += stringCoefficient;
        else if (exp == 1) {
            if (absoluteCoeff == 1.0)
                part += "x";
            else
                part += stringCoefficient + "x";
        } else {
            if (absoluteCoeff == 1.0)
                part += "x^" + exp;
            else
                part += stringCoefficient + "x^" + exp;
        }

        return part;
    }

    // -------------------------------------------------------------
    // formatTrig helper method to format trig terms only
    // -------------------------------------------------------------
    private static String formatTrig(Term t, String formatted) {
        double coeff = t.getCoefficient();
        String part = "";

        if (!formatted.isEmpty()) {
            part += (coeff >= 0) ? " + " : " - ";
        } else if (coeff < 0) {
            part += "-";
        }

        double absoluteCoeff = Math.abs(coeff);
        int[] fraction = convertToFraction(absoluteCoeff);
        int numerator = Math.abs(fraction[0]);
        int denominator = fraction[1];
        boolean isFraction = (denominator != 1);
        String stringCoefficient;

        if (isFraction)
            stringCoefficient = "(" + numerator + "/" + denominator + ")";
        else
            stringCoefficient = (numerator == 1 ? "" : String.valueOf(numerator));
        
        String inside = (t.getInnerCoefficient() == 1) ? "x" : t.getInnerCoefficient() + "x";
        part += stringCoefficient + t.getTrigType() + " " + inside;
        return part;
    }

    // -------------------------------------------------------------
    // evaluateDefinite method to evaluate definite integrals
    // -------------------------------------------------------------
    private static Double evaluateDefinite(List<Term> terms) {
        if(lowerBound == null || upperBound == null) return null; // return null for all indefinite integrals
        double result = 0.0;
        for(Term t: terms) {
            double coefficient = t.getCoefficient();
            int exponent = t.getExponent();
            double s2 = coefficient * Math.pow(upperBound, exponent);
            double s1 = coefficient * Math.pow(lowerBound, exponent);
            result += (s2 - s1);
        }
        return result;
    }

    // -------------------------------------------------------------
    // convertToFraction method to convert integrated expressions
    // into fractions
    // -------------------------------------------------------------
    private static int[] convertToFraction(double value) {
        boolean negative = value < 0;
        value = Math.abs(value);

        int maxDen = 1000;
        int bestNum = 1;
        int bestDen = 1;
        double bestError = Math.abs(value - 1.0);

        for (int den = 1; den <= maxDen; den++) { // trying all possible denominators
            int num = (int)Math.round(value * den);
            double error = Math.abs(value - (double)num / den);
            if (error < bestError) {
                bestError = error;
                bestNum = num;
                bestDen = den;
            }
            if (bestError < 1e-9) break;
        }
        int g = gcd(bestNum, bestDen); // simplifies fraction using gcd helper method 
        bestNum /= g;
        bestDen /= g;
        if (negative) bestNum *= -1; // if negative, then add a negative number back into the fraction
        return new int[]{bestNum, bestDen};
    }

    // -------------------------------------------------------------
    // Recursive gcd method to find greatest common denominator 
    // to convert into a fraction
    // -------------------------------------------------------------
    private static int gcd(int a, int b) {
        if (b == 0) return Math.abs(a);
        return gcd(b, a % b);
    }
}
