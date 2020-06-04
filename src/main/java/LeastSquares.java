import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;

public class LeastSquares {

    ArrayList<Double> xCoordList;
    ArrayList<Double> yCoordList;
    String approximateFunction = "";


    public LeastSquares(ArrayList<Double> xCoordList, ArrayList<Double> yCoordList) throws NumberFormatException, IndexOutOfBoundsException {
        this.xCoordList = new ArrayList<>();
        this.yCoordList = new ArrayList<>();
        for (int i = 0; i < xCoordList.size(); i++) {
            double xValue = xCoordList.get(i);
            double yValue = yCoordList.get(i);
            this.xCoordList.add(xValue);
            this.yCoordList.add(yValue);
        }
    }

    public String getApproximateFunction() {
        return approximateFunction;
    }

    double getFuncValue(String function, double x, ArrayList<Double> params) {
        Expression exp;
        if (function.contains("e")) exp = new ExpressionBuilder(function).variables("x", "a", "b", "e").build().
                setVariable("x", x).setVariable("a", params.get(0)).setVariable("b", params.get(1)).
                setVariable("e", Math.E);
        else if (params.size() == 2)
            exp = new ExpressionBuilder(function).variables("x", "a", "b").build().setVariable("x", x).
                    setVariable("a", params.get(0)).setVariable("b", params.get(1));
        else exp = new ExpressionBuilder(function).variables("x", "a", "b", "c").build().setVariable("x", x).
                    setVariable("a", params.get(0)).setVariable("b", params.get(1)).setVariable("c", params.get(2));
        return exp.evaluate();
    }



    public ArrayList<Double> calculateParams() {
        double min = Double.MAX_VALUE;
        double value = 0;
        ArrayList<Double> params = new ArrayList<>();
        String func[] = {"ax + b", "ax^2 + bx + c", "a + b/x", "e^(a+bx)", "a + blogx", "a * x^b"};
        ArrayList<Double> p = new ArrayList<>();
        for (int i = 0; i < func.length; i++) {
            switch (func[i]) {
                case ("ax + b") -> p = calculateLinearParams();
                case ("ax^2 + bx + c") -> p = calculateSquareParams();
                case ("a + b/x") -> p = calculateHyperbolicParams();
                case ("e^(a+bx)") -> p = calculateExponentialParams();
                case ("a + blogx") -> p = calculateLogarithmicParams();
                case ("a * x^b") -> p = calculatePowerParams();
            }
            if (p == null) {
                value = Double.MAX_VALUE;
                continue;
            }
            for (int j = 0; j < xCoordList.size(); j++) {
                value += Math.pow(yCoordList.get(j) - getFuncValue(func[i], xCoordList.get(j), p), 2);
            }
            if (value < min) {
                min = value;
                params = p;
                approximateFunction = func[i];
            }
            value = 0;
        }
        return params;
    }

    //ax + b
    ArrayList<Double> calculateLinearParams() throws IllegalArgumentException {
        int n = xCoordList.size();
        double sumXY = 0;
        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double a;
        double b;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumXY += x * y;
            sumX += x;
            sumY += y;
            sumXX += x * x;
        }

        a = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        b = (sumY - a * sumX) / n;
        ArrayList<Double> params = new ArrayList<>();
        if (checkError(a, b)) return null;
        params.add(a);
        params.add(b);

        return params;
    }

    //ax^2 + bx + c
    ArrayList<Double> calculateSquareParams() throws IllegalArgumentException {
        SLAUSolver solver = new SLAUSolver();
        int n = xCoordList.size();
        double sumX = 0;
        double sumY = 0;
        double sumXX = 0;
        double sumXXX = 0;
        double sumXY = 0;
        double sumXXXX = 0;
        double sumXXY = 0;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumX += x;
            sumY += y;
            sumXX += x * x;
            sumXXX += x * x * x;
            sumXXXX += x * x * x * x;
            sumXY += x * y;
            sumXXY += x * x * y;
        }
        double[][] matrix = {
                {sumXX, sumX, n, sumY},
                {sumXXX, sumXX, sumX, sumXY},
                {sumXXXX, sumXXX, sumXX, sumXXY}
        };

        return solver.solve(matrix);
    }

    //a + b/x
    ArrayList<Double> calculateHyperbolicParams() throws IllegalArgumentException {
        ArrayList<Double> params = new ArrayList<>();
        double n = xCoordList.size();
        double sumYX = 0;
        double sum1X = 0;
        double sumY = 0;
        double sum1XX = 0;
        double a;
        double b;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumYX += y / x;
            sum1X += 1 / x;
            sumY += y;
            sum1XX += 1 / (x * x);
        }
        b = (n * sumYX - sum1X * sumY) / (n * sum1XX - sum1X * sum1X);
        a = 1 / n * sumY - b / n * sum1X;
        if (checkError(a, b)) return null;
        params.add(a);
        params.add(b);
        return params;
    }

    //e^(a+bx)
    ArrayList<Double> calculateExponentialParams() throws IllegalArgumentException {
        ArrayList<Double> params = new ArrayList<>();
        double n = xCoordList.size();
        double sumXlnY = 0;
        double sumX = 0;
        double sumLnY = 0;
        double sumXX = 0;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumXlnY += x * Math.log(y);
            sumX += x;
            sumLnY += Math.log(y);
            sumXX += x * x;
        }
        double b = (n * sumXlnY - sumX * sumLnY) / (n * sumXX - sumX * sumX);
        double a = 1 / n * sumLnY - b / n * sumX;
        if (checkError(a, b)) return null;
        params.add(a);
        params.add(b);
        return params;
    }

    //a + blogx
    ArrayList<Double> calculateLogarithmicParams() throws IllegalArgumentException {
        ArrayList<Double> params = new ArrayList<>();
        double n = xCoordList.size();
        double sumYlnX = 0;
        double sumLnX = 0;
        double sumY = 0;
        double sumLnXLnX = 0;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumYlnX += y * Math.log(x);
            sumLnX += Math.log(x);
            sumY += y;
            sumLnXLnX += Math.log(x) * Math.log(x);
        }

        double b = (n * sumYlnX - sumLnX * sumY) / (n * sumLnXLnX - sumLnX * sumLnX);
        double a = 1 / n * sumY - b / n * sumLnX;
        if (checkError(a, b)) return null;
        params.add(a);
        params.add(b);

        return params;
    }

    //a * x^b
    ArrayList<Double> calculatePowerParams() throws IllegalArgumentException {
        ArrayList<Double> params = new ArrayList<>();
        double n = xCoordList.size();
        double sumLnXLnY = 0;
        double sumLnX = 0;
        double sumLnY = 0;
        double sumLnXLnX = 0;

        for (int i = 0; i < n; i++) {
            double x = xCoordList.get(i);
            double y = yCoordList.get(i);
            sumLnXLnX += Math.log(x) * Math.log(x);
            sumLnXLnY += Math.log(x) * Math.log(y);
            sumLnX += Math.log(x);
            sumLnY += Math.log(y);
        }

        double b = (n * sumLnXLnY - sumLnX * sumLnY) / (n * sumLnXLnX - sumLnX * sumLnX);
        double a = Math.exp(1 / n * sumLnY - b / n * sumLnX);
        if (checkError(a, b)) return null;
        params.add(a);
        params.add(b);

        return params;
    }

    private boolean checkError(double a, double b) {
        return !(a == a && b == b);
    }
}
