package org.alientrap.nexuiz.demotool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.alientrap.nexuiz.utils.Util;


public class Demo {

	private File demofile;
	private ArrayList<DemoPacket> packets = new ArrayList<DemoPacket>();
	byte[] cdtrack;
	
	public Demo(File demofile) {
		this.demofile = demofile;
	}
	
	public Demo() {}
	
	public void parseDemoFile() throws IOException {
		FileInputStream fis = new FileInputStream(demofile);
		
		// get cdtrack
		byte[] temp = new byte[1024];
		int i = 0;
		while ((temp[i] = (byte)fis.read()) != DemoPacket.CDTRACKEND) { i++; }
		i++;
		cdtrack = new byte[i];
		System.arraycopy(temp, 0, cdtrack, 0, i);
		
		// start reading packets
		while (fis.available() > 0) {
			DemoPacket packet = new DemoPacket();
			
			byte[] len = new byte[4];
			fis.read(len);
			
			long length = Util.getLEUnsignedIntFromByteArray(len, 0);
			length = length & 0x7FFFFFFF; // only useful for server demos
			if (length > Integer.MAX_VALUE) {
				System.out.println("packet too large");
				System.exit(0);
			}
			packet.setLength(len);
			
			byte[] angles = new byte[12];
			fis.read(angles);
			packet.setAngles(angles);
			
			byte[] data = new byte[(int)length];
			fis.read(data);
			packet.setData(data);
			
			if (Util.getLEUnsignedIntFromByteArray(len, 0) == 1 && data[0] == DemoPacket.SVC_NOP) {
				
			}
			
			packets.add(packet);
		}
		
		fis.close();
	}
	
	public void writeDemo(File out) throws IOException {
		FileOutputStream fos = new FileOutputStream(out);
		
		fos.write(cdtrack);
		
		Iterator<DemoPacket> it = packets.iterator();
		while (it.hasNext()) {
			DemoPacket packet = (DemoPacket) it.next();
			fos.write(packet.getLength());
			fos.write(packet.getAngles());
			fos.write(packet.getData());
		}
		
		fos.flush();
		fos.close();
	}
	
	/**
	 * remove any cutmarks that may have been inserted to capture a video from this demo
	 */
	public void removeCutmarks() {
		Iterator<DemoPacket> it = packets.iterator();
		ArrayList<DemoPacket> dps = new ArrayList<DemoPacket>();
		while (it.hasNext()) {
			DemoPacket dp = it.next();
			byte[] data = dp.getData();
			byte[] angles = dp.getAngles();
			long length = Util.getLEUnsignedIntFromByteArray(dp.getLength(), 0);
			
			if (length >= 12 && data[0] == (byte)011) {
				byte[] cutmark = new byte[11];
				System.arraycopy(data, 1, cutmark, 0, 11);
				String check = new String(cutmark);
				if (check.equals("\n//CUTMARK\n")) {
					int i = 12;
					while (data[i] != (byte)000) { i++; }
					i++;
					byte[] newdata = new byte[(int)(length-i)];
					System.arraycopy(data, i, newdata, 0, (int)length-i);
					//System.out.println(dp + "\n");
					dp = new DemoPacket();
					dp.setData(newdata);
					dp.setAngles(angles);
					dp.setLength(Util.unsignedIntToLEByteArray(newdata.length));
					//System.out.println(dp + "\n");
				}
			}
			
			dps.add(dp);
		}
		
		packets = dps;
	}
	
	public Demo[] splitDemo() {
		if (packets.isEmpty()) {
			return null;
		}
		
		ArrayList<Demo> demos = new ArrayList<Demo>();
		Demo current = null;
		
		Iterator<DemoPacket> it = packets.iterator();
		while (it.hasNext()) {
			DemoPacket dp = (DemoPacket) it.next();
			long length = Util.getLEUnsignedIntFromByteArray(dp.getLength(), 0);
			
			//if (length > 1 && dp.getData()[0] == (byte)010 && dp.getData()[(int)length-1] == 013) {
				//System.out.println(dp + "\n");
			//}
			
			if (length == 1 && dp.getData()[0] == DemoPacket.SVC_NOP) {
				System.out.println("received signon");
				if (current != null) {
					demos.add(current);
				}
				current = new Demo();
				current.setCdtrack(cdtrack);
			}
			
			if (current == null) {
				//System.out.println("no signon received");
				//System.exit(0);
			} else {
				current.getPackets().add(dp);
			}
			
			
		}
		
		if (demos.isEmpty()) {
			return null;
		}
		
		return (Demo[]) demos.toArray();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Demo))
			return false;
		
		Demo other = (Demo)o;
		
		if (!Util.compareByteArray(cdtrack, other.getCdtrack()))
			return false;
		
		ArrayList<DemoPacket> ar1 = getPackets();
		ArrayList<DemoPacket> ar2 = other.getPackets();
		if (ar1.size() != ar2.size())
			return false;
		
		for (int i = 0; i < ar1.size(); i++) {
			if (!ar1.get(i).toString().equals(ar2.get(i).toString())) {
				return false;
			}
		}
		
		return true;
	}

	public File getDemofile() {
		return demofile;
	}

	public void setDemofile(File demofile) {
		this.demofile = demofile;
	}

	public ArrayList<DemoPacket> getPackets() {
		return packets;
	}

	public void setPackets(ArrayList<DemoPacket> packets) {
		this.packets = packets;
	}

	public byte[] getCdtrack() {
		return cdtrack;
	}

	public void setCdtrack(byte[] cdtrack) {
		this.cdtrack = cdtrack;
	}
}
