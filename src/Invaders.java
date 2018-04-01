public class Invaders {
	
	public static void main(String[] args) {
		InvadersGameState gs = new InvadersGameState();

		gs.displayIntro();
		while (!gs.keyPressed());

		if (!gs.quitPressed()) {
			while (!gs.quitPressed()) {
				gs.updateAll();

				if (!gs.isOver()) {
					gs.drawAll();
					gs.buffer();
				} else {
					gs.endScreen();
					while (!gs.keyPressed());

					if (gs.mustRestart()) gs.restart(); 
				}
			}
		}

		gs.dispose();
	}
}
