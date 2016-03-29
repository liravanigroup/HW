package pl.com.bottega.commons.math;

/**
 * Created by Slawek on 19/03/16.
 */
public class Fraction {
    //stała, pole statyczne (pole klasy a nie obiektów tej klasy)
    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ZERO = new Fraction(0, 1);


    private final int nominator;
    private final int denominator;

    /**
     * Klasa modeluje ułamek
     *
     * @param nominator licznik ułamka
     * @param denominator mianownik ułamka
     *
     * @throws IllegalArgumentException gdy mianownik jest równy 0
     */
    public Fraction(int nominator, int denominator) throws IllegalArgumentException{
        if (denominator == 0)
            throw new IllegalArgumentException("Denominator can not be zero");

        this.nominator = nominator;
        this.denominator = denominator;
    }

    public Fraction(int nominator) {
        //this(nominator, 10);   //nie chcemy walidować (sprawdzać poprawność) liczby 10
        this.nominator = nominator;
        this.denominator = 10;
    }

    /**
     *
     * @param literal ułamek w preprezentacji licznik/mianownik, np: 3/4
     */
    public Fraction(String literal) throws IllegalArgumentException{
        String[] parts = literal.split("/");
        if (parts.length != 2)
            throw new IllegalArgumentException("To nie jest ułamek");

        try {
            this.nominator = Integer.parseInt(parts[0]);
            this.denominator = Integer.parseInt(parts[1]);
            if (this.denominator == 0)
                throw new IllegalArgumentException("Zero w mianowniku jest niedozwolone");
        }
        catch(NumberFormatException ex){//nie musimy przepakować tego, mozemy rzucić NFE
            throw new IllegalArgumentException("To nie jest ułamek", ex);
            //System.out.println("dskjbvakjsdbvjkhsdavb");
        }
    }

    public Fraction add(Fraction addend) {
        if (this.denominator == addend.denominator){
            int nominatorSum = this.nominator + addend.nominator;
            return new Fraction(nominatorSum, this.denominator);
        }
        else{
            int thisNominator = this.nominator * addend.denominator;
            int addendNominator = addend.nominator * this.denominator;
            int commonDenominator = this.denominator * addend.denominator;

            return new Fraction(thisNominator + addendNominator,
                                    commonDenominator);
        }
    }

    public Fraction reverseV2() throws IllegalStateException{
        if (this.nominator == 0)
            throw new IllegalStateException("Can not reverse zero");
        return new Fraction(this.denominator, this.nominator);
    }

    public Fraction reverse() throws IllegalStateException{
        try {
            return new Fraction(this.denominator, this.nominator);
        }
        catch(IllegalArgumentException ex){
            throw new IllegalStateException("Zero can not be reserved", ex);
        }
    }

    public String toString(){
        return nominator + " / " + denominator;
    }
}
