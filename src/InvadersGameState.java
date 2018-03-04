import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class InvadersGameState {
	private final double fps = 240.0;
	private final int dt = (int) (1.0 / fps * 1000.0);

	private final int default_canvas_size = 512;
	private final double default_scale = 100;
	
	private final double default_player_acc = 700;
	private final double default_player_friction = default_player_acc;
	private final double default_player_turn_rate = 2;
	private final double default_player_bounce_coeff = 0.5;
	
	private final double default_shoot_delay = 0.2; //second
	private final int default_shoot_count_max = (int) (fps * default_shoot_delay);
	
	private final double default_enemy_radius = 7;
	private final double default_enemy_spawn_height = (default_scale * 1.1) + (2 * default_enemy_radius);
	private final double default_enemy_delay = 2.5;
	private final int default_enemy_count_max = (int) (fps * default_enemy_delay);
	private final double default_enemy_velocity = 30;
	private final Polygon default_enemy_model = Polygon.circle(default_enemy_radius, 10);

	private Draw canvas;
	private Player player;
	private ArrayList<GameObject> projectiles;
	private ArrayList<GameObject> enemies;

	private int enemyCountMax = default_enemy_count_max;
	private int shootDelay = 0;
	private int enemyDelay = 0;

	private boolean shoot = true;
	private boolean shotFired = false;
	
	public InvadersGameState() {
		this.player = new Player();
		this.canvas = new Draw();
		this.projectiles = new ArrayList<GameObject>();
		this.enemies = new ArrayList<>();
		initCanvas();
	}

	private void initCanvas() {
		this.canvas.setCanvasSize(default_canvas_size, default_canvas_size);
		this.canvas.setXscale(-default_scale, default_scale);
		this.canvas.setYscale(-default_scale, default_scale);
		this.canvas.enableDoubleBuffering();
	}

	public void updateAll() {
		Vector playerAcc = new Vector(0, 0);
		double playerTurn = 0.0;

		if (this.canvas.isKeyPressed(KeyEvent.VK_LEFT)) playerAcc = playerAcc.add(new Vector(-default_player_acc, 0));
		if (this.canvas.isKeyPressed(KeyEvent.VK_RIGHT)) playerAcc = playerAcc.add(new Vector(default_player_acc, 0));
		if (!this.canvas.isKeyPressed(KeyEvent.VK_RIGHT) && !this.canvas.isKeyPressed(KeyEvent.VK_LEFT) && this.player.getVelocity().magnitude() != 0) playerAcc = this.player.getVelocity().unit().times(-default_player_friction);
		if (this.canvas.isKeyPressed(KeyEvent.VK_A)) playerTurn += default_player_turn_rate;
		if (this.canvas.isKeyPressed(KeyEvent.VK_D)) playerTurn -= default_player_turn_rate;
		if (this.canvas.isKeyPressed(KeyEvent.VK_SPACE)) {
			if (this.shoot && this.shootDelay == 0) {
				this.projectiles.add(this.player.shoot());
				this.player.cooldown();
				this.shoot = false;
				this.shotFired = true;
			} else this.shoot = true;
		}
		
		Iterator<GameObject> pr = this.projectiles.iterator();
		
		this.player.move(playerAcc, dt / 1000.0);
		this.player.rotate(playerTurn * dt / 1000.0);
		for (GameObject p : this.projectiles) p.move(new Vector(0, 0), dt / 1000.0);
		for (GameObject e : this.enemies) e.move(new Vector(0, 0), dt / 1000.0);
		
		pr = this.projectiles.iterator();
		while (pr.hasNext()) if (pr.next().getPosition().magnitude() > 150) pr.remove();

		Random r = new Random();
		if (this.enemyDelay == 0) {
			double randomValue = -90 + (90 + 90) * r.nextDouble();
			this.enemies.add(new GameObject(new Vector(randomValue, default_enemy_spawn_height), Vector2D.polar(default_enemy_velocity, 3 * Math.PI / 2), 0, 0, default_enemy_model));
			this.enemyDelay++;
		} else if (this.enemyDelay == default_enemy_count_max) this.enemyDelay = 0;
		else this.enemyDelay++;
		
		if (this.player.outside(default_scale)) this.player.bounce(default_player_bounce_coeff);
		if (this.player.getAngularPosition() < 0) this.player.setAngle(0);
		if (this.player.getAngularPosition() > Math.PI) this.player.setAngle(Math.PI);

		if (this.shotFired && this.shootDelay < default_shoot_count_max - 1) this.shootDelay++;
		else {
			this.shootDelay = 0;
			this.shotFired = false;
			this.player.reload();
		}
	}

	public void buffer() {
		this.canvas.show();
		this.canvas.pause(dt);
	}

	public void drawAll() {
		this.canvas.clear();
		this.player.draw(this.canvas);
		for (GameObject p : this.projectiles) p.draw(this.canvas);
		for (GameObject e : this.enemies) e.draw(this.canvas);
	}

	public void addProjectile(GameObject projectile) {
		this.projectiles.add(projectile);
	}

	public static void main(String[] args) {
		InvadersGameState gs = new InvadersGameState();

		while (true) {
			gs.updateAll();
			gs.drawAll();
			gs.buffer();
		}
	}
}
