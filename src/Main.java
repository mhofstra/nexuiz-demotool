import java.io.File;
import java.io.IOException;

import org.alientrap.nexuiz.demotool.Demo;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Demo d = new Demo(new File("/home/merlijn/.nexuiz/data/demos/2009-01-07_20-19_dance.dem"));
		d.parseDemoFile();
		d.splitDemo();
	}
}
