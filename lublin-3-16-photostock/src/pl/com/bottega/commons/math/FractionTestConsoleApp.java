package pl.com.bottega.commons.math;

import static pl.com.bottega.commons.math.Fraction.ONE;
import static pl.com.bottega.commons.math.Fraction.ZERO;


/**
 * Created by Slawek on 19/03/16.
 */
public class FractionTestConsoleApp {
    public static void main(String[] args){
        Fraction f1;

        try {
            f1 = new Fraction("1/0");
        }
        catch(IllegalArgumentException ex){
            System.out.println("Złe dane wejściowe! " + ex.getMessage());
            return;
        }


        Fraction sum = ONE.add(f1);

        //Fraction sum = f1.add(f2).add(Fraction.ONE).add(Fraction.ZERO);  ;//wzorzec projektowy: Value Object (immutability)

        System.out.println(sum.toString());
    }
}
