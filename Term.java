public class Term implements Comparable<Term> {
    // -------------------------------------------------------------
    // Data Fields
    // -------------------------------------------------------------
    private double coefficient;
    private int exponent;
    private boolean isTrig; // check for if term is a trig function
    private String trigType;
    private int innerCoefficient; // inner coefficient for ex: cos(10x)

    // -------------------------------------------------------------
    // Constructor & Overloaded Constructor
    // -------------------------------------------------------------
    public Term() {
        this.coefficient = 0;
        this.exponent = 0;
        this.isTrig = false;
    }
    public Term(double coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
        this.isTrig = false;
    }
    public Term(double coefficient, String trigType, int innerCoefficient) { // constructor for trig terms
        this.coefficient = coefficient;
        this.exponent = 0;
        this.isTrig = true;
        this.trigType = trigType;
        this.innerCoefficient = innerCoefficient;
    }

    // -------------------------------------------------------------
    // Accessor
    // -------------------------------------------------------------
    public double getCoefficient() {return coefficient;}
    public int getExponent() {return exponent;}
    public boolean isTrig() {return isTrig;}
    public String getTrigType() {return trigType;}
    public int getInnerCoefficient() {return innerCoefficient;}

    // -------------------------------------------------------------
    // Mutators
    // -------------------------------------------------------------
    public void setCoefficient(double coefficient) {this.coefficient = coefficient;}
    public void setExponent(int exponent) {this.exponent = exponent;}
    public void setTrig(boolean isTrig) {this.isTrig = isTrig;}
    public void setTrigType(String trigType) {this.trigType = trigType;}
    public void setInnerCoefficient(int innerCoefficient) {this.innerCoefficient = innerCoefficient;}

    // -------------------------------------------------------------
    // compareTo method
    // -------------------------------------------------------------
    @Override
    public int compareTo(Term term2) {
        if(this.isTrig && !term2.isTrig) return 1; // trig term comes after the polynomial
        if(!this.isTrig && term2.isTrig) return -1; // polynomial comes before trig term
        if(this.isTrig && term2.isTrig) { // if both are trig terms, sort alphabetically
            int cmp = this.trigType.compareTo(term2.trigType);
            if(cmp != 0) return cmp; 
            return Integer.compare(this.innerCoefficient, term2.innerCoefficient); // sort by
        }

        if(this.exponent < term2.exponent) return -1;
        else if(this.exponent > term2.exponent) return 1;
        else return 0;
    }

    // -------------------------------------------------------------
    // toString method
    // -------------------------------------------------------------
    @Override
    public String toString() {
        if(isTrig) {
            String strCoefficient;
            if(coefficient == 1) strCoefficient = "";
            else if(coefficient == -1) strCoefficient = "-";
            else strCoefficient = String.valueOf(coefficient);
            String strInnerCoefficient;
            if(innerCoefficient == 1) strInnerCoefficient = "x";
            else strInnerCoefficient = innerCoefficient + "x";
            return strCoefficient + trigType + " " + strInnerCoefficient;
        }


        if(exponent == 0) return String.valueOf(coefficient);
        else if(exponent == 1) return coefficient + "x";
        else return coefficient + "x^" + exponent;
    }
}
