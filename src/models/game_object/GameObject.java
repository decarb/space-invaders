package models.game_object;

import draw.Draw;
import models.Polygon;
import models.vector.Vector;

public class GameObject {
    private Vector position;
    private Vector velocity;
    private double angularPosition;
    private Polygon model;

    public GameObject(Vector position, Vector velocity, double angularPosition, Polygon model) {
        this.position = position;
        this.velocity = velocity;
        this.angularPosition = angularPosition;
        this.model = model;
    }

    public Vector getPosition() {
        return this.position;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public double getAngularPosition() {
        return this.angularPosition;
    }

    public Polygon getModel() {
        return this.model;
    }

    public void setAngle(double theta) {
        this.angularPosition = theta;
    }

    public void move(Vector acceleration, double dt) {
        this.velocity = this.velocity.add(acceleration.times(dt));
        this.position = this.position.add(this.velocity.times(dt));
    }

    public void rotate(double theta) {
        this.angularPosition += theta;
    }

    public void draw(Draw canvas) {
        this.model.translate(this.position).rotate(this.angularPosition).draw(canvas);
    }

    public void bounce(double coeff) {
        this.velocity = this.velocity.times(-coeff);
    }
}
