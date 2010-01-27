package org.alientrap.nexuiz.demotool;

import org.alientrap.nexuiz.utils.*;

public class DemoPacket {

	public static byte CDTRACKEND = (byte)012;
	public static byte SVCSIGNON = (byte)001;
	
	private byte[] length;
	private byte[] angles;
	private byte[] data;
	
	public double getTime() {
		if (data[0] == (byte)007) {
			byte[] time = new byte[4];
			System.arraycopy(data, 1, time, 0, 4);
			return Util.timeToDouble(time);
		}
		return 0;
	}
	
	public byte[] getLength() {
		return length;
	}
	public void setLength(byte[] length) {
		this.length = length;
	}
	public byte[] getAngles() {
		return angles;
	}
	public void setAngles(byte[] angles) {
		this.angles = angles;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public String toString() {
		long length = Util.getLEUnsignedIntFromByteArray(getLength(), 0);
		String ret = "length: " + length  + "\n\n";
		
		ret += "angles: " + Util.byteArrayToHexString(angles) + "\n\n";
		
		ret += "data:\n";
		
		String dat = Util.byteArrayToHexString(data);
		for(int i = 0; i < Math.floor(dat.length()/3); i++) {
			ret += dat.substring(i*3, (i*3)+3);
			if (i % 8 == 7) {
				ret += "\n";
			}
		}
		
		return ret;
	}
	
	
	
}
