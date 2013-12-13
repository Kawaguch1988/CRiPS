import java.awt.Color;

/*
 * Shell.java
 * Created on 2012/01/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * @author macchan
 */
public class ShellTest extends Turtle {

	public static void main(String[] args) {
		Turtle.startTurtle(new ShellTest());
	}

	public void start() {
		window.canvas().setBackground(Color.red);
		Shell shell = new Shell();
		shell.warp(200, 200);
		while (true) {
			sleep(0.05);
			shell.fd(1);
			shell.rt(1);
			System.out.println(shell.location());
			update();
		}
	}
}
