package org.alientrap.nexuiz.demotool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


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
		cdtrack = new byte[i+1];
		System.arraycopy(temp, 0, cdtrack, 0, i);
		
		// start reading packets
		while (fis.available() > 0) {
			DemoPacket packet = new DemoPacket();
			
			byte[] len = new byte[4];
			fis.read(len);
			
			long length = DemoPacket.getLEUnsignedIntFromByteArray(len, 0);
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
			
			if (DemoPacket.getLEUnsignedIntFromByteArray(len, 0) == 1 && data[0] == DemoPacket.SVCSIGNON) {
				
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
	
	public Demo[] splitDemo() {
		if (packets.isEmpty()) {
			return null;
		}
		
		ArrayList<Demo> demos = new ArrayList<Demo>();
		Demo current = null;
		
		Iterator<DemoPacket> it = packets.iterator();
		while (it.hasNext()) {
			DemoPacket dp = (DemoPacket) it.next();
			long length = DemoPacket.getLEUnsignedIntFromByteArray(dp.getLength(), 0);
			
			//if (length > 1 && dp.getData()[0] == (byte)010 && dp.getData()[(int)length-1] == 013) {
				//System.out.println(dp + "\n");
			//}
			
			if (length == 1 && dp.getData()[0] == DemoPacket.SVCSIGNON) {
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
