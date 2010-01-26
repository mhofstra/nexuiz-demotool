package org.alientrap.nexuiz.demotool;

public class DemoPacket {

	public static byte CDTRACKEND = (byte)10;
	public static byte SVCSIGNON = (byte)1;
	
	private byte[] length;
	private byte[] angles;
	private byte[] data;
	
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
