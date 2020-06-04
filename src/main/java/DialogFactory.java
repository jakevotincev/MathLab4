import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class DialogFactory {

    private JDialog createChartDialog(JFrame owner, JPanel content) {
        JDialog dialog = new JDialog(owner, "Решение", true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        content.setLayout(new BorderLayout());
        dialog.setContentPane(content);
        dialog.setSize(600, 500);
        dialog.setLocation(450, 150);
        return dialog;
    }


    public JDialog createGraphDialog(JFrame owner, Dataset dataset) {
        LineChart chart = new LineChart();
        return createChartDialog(owner, chart.createChartPanel(dataset));
    }

}
