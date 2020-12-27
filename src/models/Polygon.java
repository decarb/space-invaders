package models;

import draw.Draw;
import models.vector.Vector;
import models.vector.Vector2D;

public class Polygon {
    private final double[] x;
    private final double[] y;
    private final int points;

    public Polygon(double[] x, double[] y) {
        this.x = x;
        this.y = y;
        this.points = x.length;
    }

    public Polygon(Vector... points) {
        this.points = points.length;
        double[] x = new double[points.length];
        double[] y = new double[points.length];

        for (int i = 0; i < points.length; i++) {
            x[i] = points[i].getComponent(0);
            y[i] = points[i].getComponent(1);
        }

        this.x = x;
        this.y = y;
    }

    public void draw(Draw canvas) {
        canvas.filledPolygon(this.x, this.y);
    }

    public Polygon translate(Vector translation) {
        Vector[] pointsOld = this.toVectors();
        Vector[] pointsNew = new Vector[this.points];
        for (int i = 0; i < this.points; i++) pointsNew[i] = pointsOld[i].add(translation);

        return new Polygon(pointsNew);
    }

    public Polygon rotate(double theta) {
        Vector[] pointsOld = this.toVectors();
        Vector[] pointsNew = new Vector[this.points];
        Vector centroid = this.getCentroid();

        for (int i = 0; i < this.points; i++)
            pointsNew[i] = centroid.add(Vector2D.rotate(pointsOld[i].minus(centroid), theta));
        return new Polygon(pointsNew);
    }

    public Polygon rotate(Vector reference, double theta) {
        Vector[] pointsOld = this.toVectors();
        Vector[] pointsNew = new Vector[this.points];

        for (int i = 0; i < this.points; i++)
            pointsNew[i] = reference.add(Vector2D.rotate(pointsOld[i].minus(reference), theta));
        return new Polygon(pointsNew);
    }

    public Vector getCentroid() {
        double x = 0, y = 0;
        double area = this.getArea();

        for (int i = 0; i < this.points; i++) {
            x += ((this.x[i] + this.x[(i + 1) % points]) * ((this.x[i] * this.y[(i + 1) % points]) - (this.x[(i + 1) % points] * this.y[i]))) / (6.0 * area);
            y += ((this.y[i] + this.y[(i + 1) % points]) * ((this.x[i] * this.y[(i + 1) % points]) - (this.x[(i + 1) % points] * this.y[i]))) / (6.0 * area);
        }

        return new Vector(x, y);
    }

    public Vector[] toVectors() {
        Vector[] out = new Vector[this.points];
        for (int i = 0; i < this.points; i++) out[i] = new Vector(this.x[i], this.y[i]);
        return out;
    }

    public double getArea() {
        double inner = 0;
        for (int i = 0; i < this.points; i++)
            inner += ((this.x[i] * this.y[(i + 1) % this.points]) - (this.x[(i + 1) % this.points] * this.y[i]));
        return 0.5 * inner;
    }

    public static Polygon circle(double r, int n) {
        Vector[] points = new Vector[n];
        for (int i = 0; i < n; i++) points[i] = Vector2D.polar(r, i * (Math.PI * 2 / n));
        return new Polygon(points);
    }

    public static Polygon rectangle(double width, double height) {
        Vector[] points = new Vector[4];
        points[0] = new Vector(width / 2.0, -(height / 2.0));
        points[1] = new Vector(width / 2.0, height / 2.0);
        points[2] = new Vector(-(width / 2.0), height / 2.0);
        points[3] = new Vector(-(width / 2.0), -(height / 2.0));

        return new Polygon(points);
    }

    public void printPoints() {
        for (int i = 0; i < this.points; i++) new Vector(x[i], y[i]).print();
    }

    public static void main(String[] args) {
        Polygon p = rectangle(10, 5);
        Polygon p1 = p.translate(new Vector(20, 10));
        Polygon p2 = p1.rotate(Math.PI / 2);

        Draw d = new Draw();
        d.setCanvasSize(512, 512);
        d.setXscale(-100, 100);
        d.setYscale(-100, 100);

        p1.draw(d);
        p2.draw(d);
    }
}
