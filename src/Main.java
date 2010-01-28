import java.io.File;
import java.io.IOException;

import org.alientrap.nexuiz.demotool.*;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, DemoException {
		
		Demo d1 = new Demo(new File("/home/merlijn/.nexuiz/data/demos/2009-01-07_20-19_dance.dem"));
		d1.parseDemoFile();
		//d1.splitDemo();
		d1.insertCutmarks(10, 20, false);
	}
}
