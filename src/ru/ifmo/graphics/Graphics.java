package ru.ifmo.graphics;

import com.xeiam.xchart.*;
import ru.ifmo.lang.Typke;

import java.util.LinkedList;
import java.util.List;


public class Graphics {


    public static void main(String[] args) throws Exception {

        double[] xData = new double[]{0.0, 1.0, 2.0};
        double[] yData = new double[]{2.0, 1.0, 0.0};

        LinkedList<Typke> list = new LinkedList<>();
        list.add(new Typke(2,2));
        list.add(new Typke(4,4));
        list.add(new Typke(100, 4));

        LinkedList<Typke> list1 = new LinkedList<>();
        list1.add(new Typke(2,4));
        list1.add(new Typke(4,5));
        list1.add(new Typke(100, 34));
        drawLnInv(list, list1);

    }

    public static void drawLnInv(LinkedList<Typke> ... functions) {
        Chart chart = new ChartBuilder().width(800).height(600).build();
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
        //chart.setXAxisTitle("1/T");
        //chart.setYAxisTitle("ln()");
        int i = 0;
        for (LinkedList<Typke> function : functions) {
            List<Double> xData = new LinkedList<>();
            List<Double> yData = new LinkedList<>();
            for (Typke point : function) {
                xData.add(Math.log(point.x));
                yData.add(1 / point.y);
            }
            i++;
            chart.addSeries("G" + i,xData, yData);
        }
        new SwingWrapper(chart).displayChart();
    }
}
