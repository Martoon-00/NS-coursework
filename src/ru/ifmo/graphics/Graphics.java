package ru.ifmo.graphics;

import com.xeiam.xchart.*;
import ru.ifmo.lang.Typke;

import java.util.LinkedList;
import java.util.List;


public class Graphics {

    private Chart chart;

    public Graphics(String XAsis, String YAsis) {
        chart = new ChartBuilder().width(800).height(600).build();
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
        chart.setXAxisTitle(XAsis);
        chart.setYAxisTitle(YAsis);
    }

    public static void main(String[] args) throws Exception {


        LinkedList<Typke> list = new LinkedList<>();
        list.add(new Typke(2,2));
        list.add(new Typke(4,4));
        list.add(new Typke(100, 4));

        LinkedList<Typke> list1 = new LinkedList<>();
        list1.add(new Typke(2,4));
        list1.add(new Typke(4,5));
        list1.add(new Typke(100, 34));

        Graphics g = new Graphics("A", "s");
        g.addLnInvGraphic(list, "G1");
        g.addLnInvGraphic(list1, "G2");
        g.show();

    }



    public void addLnInvGraphic(List<Typke>  points, String graphicName) {

            List<Double> xData = new LinkedList<>();
            List<Double> yData = new LinkedList<>();
            for (Typke point : points) {
                xData.add(Math.log(point.x));
                yData.add(1 / point.y);
            }
            chart.addSeries(graphicName, xData, yData);
    }

    public void show() {
        new SwingWrapper(chart).displayChart();
    }
}
