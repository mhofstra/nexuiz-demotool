package org.alientrap.nexuiz.demotool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class Demo {

	private File demo;
	private ArrayList<DemoPacket> packets = new ArrayList<DemoPacket>();
	byte[] cdtrack;
	
	public Demo(File demo) {
		this.demo = demo;
	}
	
	public void parseDemoFile() throws IOException {
		FileInputStream fis = new FileInputStream(demo);
		
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
		
		return null;
	}

	public File getDemo() {
		return demo;
	}

	public void setDemo(File demo) {
		this.demo = demo;
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
