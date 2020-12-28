public class InvadersMain {

    public static void main(String[] args) {
        InvadersGameState gs = new InvadersGameState();

        gs.displayIntro();
        while (gs.keyNotPressed()) ; // These while loops break the ability to stop the program using the close button
                                     // but that is a dumb limitation of the engine itself
        if (gs.quitNotPressed()) {
            while (gs.quitNotPressed()) {
                gs.updateAll();

                if (!gs.isOver()) {
                    gs.drawAll();
                    gs.buffer();
                } else {
                    gs.endScreen();
                    while (gs.keyNotPressed()) ;

                    if (gs.mustRestart()) gs.restart();
                }
            }
        }

        gs.dispose();
    }
}
