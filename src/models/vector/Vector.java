package models.vector;

public class Vector {
    private final double[] components;
    private final int dimension;

    public Vector(int dimension) {
        this.components = new double[dimension];
        this.dimension = dimension;
    }

    public Vector(double... components) {
        this.components = components;
        this.dimension = components.length;
    }

    public double dot(Vector that) {
        double out = 0;

        if (this.dimension != that.dimension) System.out.println("Dimensions don't match");
        else for (int i = 0; i < that.dimension; i++) out += this.components[i] * that.components[i];

        return out;
    }

    public Vector times(double a) {
        Vector out = new Vector(this.dimension);
        for (int i = 0; i < this.dimension; i++) out.components[i] = this.components[i] * a;

        return out;
    }

    public void print() {
        for (int i = 0; i < this.dimension; i++) System.out.print(this.components[i] + " ");
        System.out.println();
    }

    public double magnitude() {
        double inside = 0;
        for (int i = 0; i < this.dimension; i++) inside += Math.pow(this.components[i], 2);

        return Math.sqrt(inside);
    }

    public Vector unit() {
        return this.times(1 / this.magnitude());
    }

    public Vector add(Vector that) {
        double[] comp = new double[this.dimension];
        for (int i = 0; i < this.dimension; i++) comp[i] = this.components[i] + that.components[i];

        return new Vector(comp);
    }

    public Vector minus(Vector that) {
        double[] comp = new double[this.dimension];
        for (int i = 0; i < this.dimension; i++) comp[i] = this.components[i] - that.components[i];

        return new Vector(comp);
    }

    public double getComponent(int n) {
        return this.components[n];
    }

    public static void main(String[] args) {
        double[] v1c = {5, 6, 7};
        Vector v1 = new Vector(v1c);

        double[] v2c = {5, 6, 7};
        Vector v2 = new Vector(v2c);

        System.out.println(v1.dot(v2));
        v1.times(3).print();
        System.out.println(v1.magnitude());
        v1.unit().print();
    }
}
