import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

public class EulerMethod {
    ArrayList<Double> abscissas = new ArrayList<>();
    ArrayList<Double> ordinates = new ArrayList<>();


    public ArrayList<Double> getAbscissas() {
        return abscissas;
    }

    public ArrayList<Double> getOrdinates() {
        return ordinates;
    }

    private double getFuncValue(String function, double x, double y) {
        Expression exp = new ExpressionBuilder(function).variables("x", "y").build().
                setVariable("x", x).setVariable("y", y);
        return exp.evaluate();
    }

    public void solve(String function, double x0, double y0, double b, double acc)  {
        double h = Math.sqrt(acc);
        abscissas.add(x0);
        ordinates.add(y0);
        if (b<x0) throw new IllegalArgumentException();
        int n = (int) Math.round((b - x0) / h);
        for (int i = 0; i < n; i++) {
            double y = y0 + h * getFuncValue(function, x0, y0) / 2;
            y0 += h * getFuncValue(function, x0 + h / 2, y);
            x0 += h;
            abscissas.add(x0);
            ordinates.add(y0);
        }
    }
    
}
