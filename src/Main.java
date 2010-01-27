import java.io.File;
import java.io.IOException;

import org.alientrap.nexuiz.demotool.Demo;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Demo d1 = new Demo(new File("/home/merlijn/.nexuiz/data/demos/2009-01-07_20-19_dance.dem"));
		Demo d2 = new Demo(new File("/home/merlijn/.nexuiz/data/cuttest.dem"));
		d1.parseDemoFile();
		d2.parseDemoFile();
		d2.removeCutmarks();
		
		if (d1.equals(d2)) {
			d2.writeDemo(new File("/home/merlijn/.nexuiz/data/cuttest-removed.dem"));
			System.out.println("Successful removal");
		}
	}
}
