public class Player extends GameObject {
	// Player constants
	private static final double default_radius = 7.5;
	private static final Vector default_starting_position = new Vector(0, -90);
	private static final Vector default_starting_velocity = new Vector(0, 0);
	private static final double default_angular_position = Math.PI / 2;
	private static final double default_angular_velocity = 0;
	private static final Polygon default_player_model = Polygon.circle(default_radius, 20);

	// Turret constants
	private static final double default_turret_width = 4;
	private static final double default_turret_length = 9;
	private static final double default_turret_radius = default_radius + 1.5 + (default_turret_length / 2);
	private static final Polygon default_turret_model = Polygon.rectangle(default_turret_length, default_turret_width);

	// Projectile constants
	private static final double default_shoot_velocity = 200;
	private static final double default_shoot_width = 4;
	private static final double default_shoot_radius = default_turret_radius + (default_turret_length / 2) + (default_shoot_width / 2);
	private static final Polygon default_projectile_model = Polygon.rectangle(default_shoot_width, default_shoot_width);

	// Class variables
	private boolean cooldown = false;

	public Player(Vector position, Vector velocity, double angularPosition, double angularVelocity) {
		super(position, velocity, angularPosition, angularVelocity, default_player_model);
	}

	public Player() {
		super(default_starting_position, default_starting_velocity, default_angular_position, default_angular_velocity, default_player_model);
	}

	public void draw(Draw canvas) {
		this.getModel().rotate(this.getAngularPosition()).translate(this.getPosition()).draw(canvas);
		this.drawTurret(canvas);
	}

	private void drawTurret(Draw canvas) {
		double turretRadius;
		if (cooldown) turretRadius = default_turret_radius - 1;
		else turretRadius = default_turret_radius;

		Vector turretPos = this.getPosition().add(Vector2D.polar(turretRadius, this.getAngularPosition()));
		default_turret_model.rotate(this.getAngularPosition()).translate(turretPos).draw(canvas);
	}

	public GameObject shoot() {
		Vector position = this.getPosition().add(Vector2D.polar(default_shoot_radius, this.getAngularPosition()));
		Vector velocity = Vector2D.polar(default_shoot_velocity, this.getAngularPosition());
		return new GameObject(position, velocity, this.getAngularPosition(), 0, default_projectile_model);
	}

	public void cooldown() {
		this.cooldown = true;
	}

	public void reload() {
		this.cooldown = false;
	}

	public boolean outside(double scale) {
		double trueScale = scale * 1.10;
		double threshold = trueScale - (2 * default_radius); 
		if ((this.getPosition().getComponent(0) <= -threshold && Vector2D.angle(this.getVelocity()) != 0) || (this.getPosition().getComponent(0) >= threshold && Vector2D.angle(this.getVelocity()) != Math.PI)) return true;
		else return false;
	}
}
