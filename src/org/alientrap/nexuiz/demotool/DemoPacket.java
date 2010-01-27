package org.alientrap.nexuiz.demotool;

import org.alientrap.nexuiz.utils.*;

public class DemoPacket {

	public static byte SVC_BAD = (byte)000;
	public static byte SVC_NOP = (byte)001;
	public static byte SVC_DISCONNECT = (byte)002;
	public static byte SVC_UPDATESTAT = (byte)003;// [byte] [long]
	public static byte SVC_VERSION = (byte)004;// [long] server version
	public static byte SVC_SETVIEW = (byte)005;// [short] entity number
	public static byte SVC_SOUND = (byte)006;// <see code>
	public static byte SVC_TIME = (byte)007;// [float] server time
	public static byte SVC_PRINT = (byte)010;// [string] null terminated string
	public static byte SVC_STUFFTEXT = (byte)011;	// [string] stuffed into client's console buffer
													// the string should be \n terminated
	public static byte SVC_SETANGLE = (byte)012;// [angle3] set the view angle to this absolute value
	public static byte SVC_SERVERINFO = (byte)013;	// [long] version
													// [string] signon string
													// [string]..[0]model cache
													// [string]...[0]sounds cache
	public static byte SVC_LIGHTSTYLE = (byte)014;// [byte] [string]
	public static byte SVC_UPDATENAME = (byte)015;// [byte] [string]
	public static byte SVC_UPDATEFRAGS = (byte)016;// [byte] [short]
	public static byte SVC_CLIENTDATA = (byte)017;// <shortbits + data>
	public static byte SVC_STOPSOUND = (byte)020;// <see code>
	public static byte SVC_UPDATECOLORS = (byte)021;// [byte] [byte]
	public static byte SVC_PARTICLE = (byte)022;// [vec3] <variable>
	public static byte SVC_DAMAGE = (byte)023;
	public static byte SVC_SPAWNSTATIC = (byte)024;
	public static byte SVC_SPAWNBINARY = (byte)025;
	public static byte SVC_SPAWNBASELINE = (byte)026;
	public static byte SVC_TEMP_ENTITY= (byte)027;
	public static byte SVC_SETPAUSE = (byte)030;// [byte] on / off
	public static byte SVC_SIGNONNUM = (byte)031;// [byte]  used for the signon sequence
	public static byte SVC_CENTERPRINT = (byte)032;// [string] to put in center of the screen   
	public static byte SVC_KILLEDMONSTER = (byte)033;
	public static byte SVC_FOUNDSECRET = (byte)034;
	public static byte SVC_SPAWNSTATICSOUND = (byte)035;// [coord3] [byte] samp [byte] vol [byte] aten
	public static byte SVC_INTERMISSION = (byte)036;// [string] music
	public static byte SVC_FINALE = (byte)037;// [string] music [string] text
	public static byte SVC_CDTRACK = (byte)040;// [byte] track [byte] looptrack
	public static byte SVC_SELLSCREEN = (byte)041;
	public static byte SVC_CUTSCENE = (byte)042;
	public static byte SVC_SHOWLMP = (byte)043;// [string] slotname [string] lmpfilename [short] x [short] y
	public static byte SVC_HIDELMP = (byte)044;// [string] slotname
	public static byte SVC_SKYBOX = (byte)045;// [string] skyname
	
	public static byte SVC_DOWNLOADDATA = (byte)061;// [int] start [short] size
	public static byte SVC_UPDATESTATUBYTE = (byte)062;// [byte] stat [byte] value
	public static byte SVC_EFFECT = (byte)063;// [vector] org [byte] modelindex [byte] startframe [byte] framecount [byte] framerate
	public static byte SVC_EFFECT2 = (byte)064;// [vector] org [short] modelindex [short] startframe [byte] framecount [byte] framerate
	public static byte SVC_SOUND2 = (byte)065;// (obsolete in DP6 and later) short soundindex instead of byte
	public static byte SVC_PRECACHE = (byte)066;// [short] precacheindex [string] filename, precacheindex is + 0 for modelindex and +32768 for soundindex
	public static byte SVC_SPAWNBASELINE2 = (byte)067;// short modelindex instead of byte
	public static byte SVC_SPAWNSTATIC2 = (byte)070;// short modelindex instead of byte
	public static byte SVC_ENTITIES = (byte)071;// [int] deltaframe [int] thisframe [float vector] eye [variable length] entitydata
	public static byte SVC_CSQCENTITIES = (byte)072;// [short] entnum [variable length] entitydata ... [short] 0x0000
	public static byte SVC_SPAWNSTATICSOUND2 = (byte)073;// [coord3] [short] samp [byte] vol [byte] aten
	public static byte SVC_TRAILPARTICLES = (byte)074;// [short] entnum [short] effectnum [vector] start [vector] end
	public static byte SVC_POINTPARTICLES = (byte)075;// [short] effectnum [vector] start [vector] velocity [short] count
	public static byte SVC_POINTPARTICLES1 = (byte)076;// [short] effectnum [vector] start, same as svc_pointparticles except velocity is zero and count is 1
	
	public static byte CDTRACKEND = (byte)012; // taken from demotc.pl
	
	private byte[] length;
	private byte[] angles;
	private byte[] data;
	
	public double getTime() {
		if (data[0] == SVC_TIME) {
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
