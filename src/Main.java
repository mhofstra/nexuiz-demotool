import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.alientrap.nexuiz.demotool.Demo;
import org.alientrap.nexuiz.demotool.DemoPacket;


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
		
		ArrayList<DemoPacket> ar1 = d1.getPackets();
		ArrayList<DemoPacket> ar2 = d2.getPackets();
		for (int i = 0; i < ar1.size(); i++) {
			if (!ar1.get(i).toString().equals(ar2.get(i).toString())) {
				System.out.println(ar1.get(i) + "\n");
				System.out.println(ar2.get(i) + "\n");
			}
		}
		d2.writeDemo(new File("/home/merlijn/.nexuiz/data/cuttest-removed.dem"));
	}
}
