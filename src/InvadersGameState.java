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
	
	private final double default_enemy_radius = 6;
	private final double default_enemy_velocity = 2;
	private final Polygon default_enemy_model = Polygon.circle(default_enemy_radius, 10);
	private final int default_enemy_rows = 1; //5
	private final int default_enemy_coloums = 1; //9
	private final double default_enemy_gap = 6;
	private final double default_enemy_start_height = default_scale - 20;

	private Draw canvas;
	private Player player;
	private ArrayList<GameObject> projectiles;
	private ArrayList<GameObject> enemies;
	
	private int shootDelay = 0;
	private int playerScore = 0;

	private boolean shoot = true;
	private boolean shotFired = false;
	
	public InvadersGameState() {
		this.player = new Player();
		this.canvas = new Draw();
		this.projectiles = new ArrayList<GameObject>();
		this.enemies = new ArrayList<GameObject>();
		createEnemies();
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
		
		if (this.player.outside(default_scale)) this.player.bounce(default_player_bounce_coeff);
		if (this.player.getAngularPosition() < 0) this.player.setAngle(0);
		if (this.player.getAngularPosition() > Math.PI) this.player.setAngle(Math.PI);

		if (this.shotFired && this.shootDelay < default_shoot_count_max - 1) this.shootDelay++;
		else {
			this.shootDelay = 0;
			this.shotFired = false;
			this.player.reload();
		}
		
		checkIntersections();
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
		printScore();
	}
	
	private void checkIntersections() {
		ArrayList<GameObject> removede = new ArrayList<>();
		ArrayList<GameObject> removedp = new ArrayList<>();
		
		for (Iterator<GameObject> enemies = this.enemies.iterator(); enemies.hasNext();) {
			GameObject enemy = enemies.next();
			for (Iterator<GameObject> projectiles = this.projectiles.iterator(); projectiles.hasNext();) {
				GameObject projectile = projectiles.next();
				
				if (enemy.getPosition().minus(projectile.getPosition()).magnitude() < 6) {
					playerScore++;
					removede.add(enemy);
					removedp.add(projectile);
				}
			}
		}
		
		this.projectiles.removeAll(removedp);
		this.enemies.removeAll(removede);
	}
	
	private void printScore() {
		this.canvas.setFont();
		this.canvas.text(-(default_scale - 15), default_scale - 6, "Score: " + this.playerScore);
	}
	
	public boolean isOver() {
		if (this.enemies.size() == 0) return true;
		else return false;
	}
	
	private void createEnemies() {
		double startingPosition = 0;
		double diameter = 2 * default_enemy_radius;
		if (default_enemy_coloums % 2 == 0) startingPosition = -(((default_enemy_coloums / 2) - 0.5) * (default_enemy_gap + diameter));
		else startingPosition = -(((default_enemy_coloums - 1) / 2) * (default_enemy_gap + diameter));
		
		double delta = diameter + default_enemy_gap;
		
		for (int i = 0; i < default_enemy_rows; i++) {
			for (int j = 0; j < default_enemy_coloums; j++) {
				Vector position = new Vector(startingPosition + (j * delta), default_enemy_start_height - (i * delta));
				GameObject enemy = new GameObject(position, new Vector(0, -default_enemy_velocity), 0, 0, default_enemy_model);
				this.enemies.add(enemy);
			}
		}
	}
	
	public void blackScreen() {
		this.canvas.clear();
		this.buffer();
	}
	
	public boolean quitPressed() {
		if (this.canvas.isKeyPressed(KeyEvent.VK_Q)) return true;
		else return false;
	}

	public static void main(String[] args) {
		InvadersGameState gs = new InvadersGameState();

		while (true) {
			gs.updateAll();
			
			if (!gs.isOver()) {
				gs.drawAll();
				gs.buffer();
			} else break;
		}
		
		while (!gs.quitPressed());
	}
}
