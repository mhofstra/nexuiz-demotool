package org.alientrap.nexuiz.demotool;

import java.nio.ByteBuffer;

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
			return timeToDouble(time);
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
		long length = getLEUnsignedIntFromByteArray(getLength(), 0);
		String ret = "length: " + length  + "\n\n";
		
		ret += "angles: " + byteArrayToHexString(angles) + "\n\n";
		
		ret += "data:\n";
		
		String dat = byteArrayToHexString(data);
		for(int i = 0; i < Math.floor(dat.length()/3); i++) {
			ret += dat.substring(i*3, (i*3)+3);
			if (i % 8 == 7) {
				ret += "\n";
			}
		}
		
		return ret;
	}
	
	public static double timeToDouble(byte[] b) {
		int bits = 0;
		int i = 3;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((int) b[i] & 0xFF) << (shifter * 8);
			i--;
		}

		return Float.intBitsToFloat(bits);
	}
	
	public static String byteArrayToHexString(byte[] a) {
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		String r = new String("");
		for (int i = 0; i < a.length; i++) {
			int n =  ((int)a[i] & 0xFF);
			r += hex[(int)Math.floor(n / 16)];
			r += hex[(n % 16)];
			r += " ";
		}
		return r;
	}
	
	/*
	 * Little Endian conversion
	 */
	public static long getLEUnsignedIntFromByteArray(byte[] array,int pos) {

		if((array[pos]&0x80) == 0x80){
			long i = 0;

			i += ((array[pos+3])&(0x7f)) << 24;
			i += unsignedByteToInt(array[pos+2]) << 16;
			i += unsignedByteToInt(array[pos+1]) << 8;
			i += unsignedByteToInt(array[pos]) << 0;

			return (i + 2147483647 + 1);
		}

		int i=0;

		i += unsignedByteToInt(array[pos+3]) << 24;
		i += unsignedByteToInt(array[pos+2]) << 16;
		i += unsignedByteToInt(array[pos+1]) << 8;
		i += unsignedByteToInt(array[pos]) << 0;

		return ((long)i);

	}
	
	public static int unsignedByteToInt(byte b) {
		return((int) b & 0xFF);
	}
	
}
