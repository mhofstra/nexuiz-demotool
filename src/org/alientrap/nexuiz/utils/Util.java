package org.alientrap.nexuiz.utils;

public class Util {

	public static boolean compareByteArray(byte[] a, byte[] b) {
		if (!(a.length == b.length)) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (unsignedByteToInt(a[i]) != unsignedByteToInt(b[i])) {
				return false;
			}
		}
		return true;
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

		if((array[array.length-(pos+1)]&0x80) == 0x80){
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
	
	public static byte[] unsignedIntToLEByteArray(int a) {
		byte[] b = new byte[4];
		for (int i=3; i>=0;i--) {
			b[i] = (byte)(Math.floor(a / Math.pow(256, i)) % 256);
		}
		return b;
	}
	
	public static int unsignedByteToInt(byte b) {
		return((int) b & 0xFF);
	}
}
