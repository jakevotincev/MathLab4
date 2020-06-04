import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainFrame extends JFrame {


    private JPanel mainPanel = new JPanel();
    private GridBagConstraints constraints = new GridBagConstraints();
    private JTextField funcInput = new JTextField();
    private JTextField xInput = new JTextField();
    private JTextField yInput = new JTextField();
    private JTextField bInput = new JTextField();
    private JTextField accInput = new JTextField();
    private JButton button = new JButton("Вычислить");


    public MainFrame(String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setLayout(new GridBagLayout());

        //description
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(5, 10, 5, 10);
        addComponent(new JLabel("Решение уравнений вида y' + f(x,y) = 0"), 0, 0, 4);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(new JLabel("Введите f(x,y):"), 1, 0, 2);
        addComponent(funcInput, 1, 2, 2);

        constraints.fill = GridBagConstraints.CENTER;
        addComponent(new JLabel("x0:"), 2, 0, 1);
        addComponent(new JLabel("y0:"), 2, 2, 1);

        xInput.setPreferredSize(new Dimension(60, 20));
        yInput.setPreferredSize(new Dimension(60, 20));
        addComponent(xInput, 2, 1, 1);
        addComponent(yInput, 2, 3, 1);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(new JLabel("Конец отрезка:"), 3, 0, 2);
        addComponent(bInput, 3, 2, 2);
        addComponent(new JLabel("Точность:"), 4, 0, 2);
        addComponent(accInput, 4, 2, 2);

        constraints.fill = GridBagConstraints.CENTER;
        button.addActionListener(buttonListener);
        addComponent(button, 5, 0, 4);


        this.setContentPane(mainPanel);
    }

    private void addComponent(JComponent component, int row, int col, int nCol) {
        constraints.gridx = col;
        constraints.gridy = row;
        constraints.gridwidth = nCol;
        mainPanel.add(component, constraints);
    }

    ActionListener buttonListener = e -> {
        String function = funcInput.getText();
        DialogFactory factory = new DialogFactory();
        try {
            double x = Double.parseDouble(xInput.getText());
            double y = Double.parseDouble(yInput.getText());
            double b = Double.parseDouble(bInput.getText());
            double acc = Double.parseDouble(accInput.getText());
            EulerMethod eulerMethod = new EulerMethod();
            eulerMethod.solve(function, x, y, b, acc);
            ArrayList<Double> abscissas = eulerMethod.getAbscissas();
            ArrayList<Double> ordinates = eulerMethod.getOrdinates();
            LeastSquares leastSquares = new LeastSquares(abscissas, ordinates);
            Dataset dataset = new Dataset();
            dataset.setPointsDataset(abscissas, ordinates);
            ArrayList<Double> params = leastSquares.calculateParams();
            if (params.size()<2) throw new Exception("Ошибка вычислений, попробуйте ввести другие данные");
            dataset.setFunctionDataset(leastSquares.getApproximateFunction(), params);
            factory.createGraphDialog(this, dataset).setVisible(true);

        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this,"Недопустимые данные","Ошибка",0);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Ошибка",0);
        }

    };

}
