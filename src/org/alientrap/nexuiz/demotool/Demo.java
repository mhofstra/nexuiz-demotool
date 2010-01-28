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
	private short cutmarks = 0; // -1 == no cutmarks, 0 == unknown, 1 == cutmarks

	public Demo(File demofile) {
		this.demofile = demofile;
	}

	public Demo() {}

	/**
	 * Read the data into native Java storage. Always perform this before doing other actions on a data file.
	 * @throws IOException - if the file cannot be read
	 * @throws DemoException - if the file is malformed
	 */
	public void parseDemoFile() throws IOException, DemoException {
		if (demofile == null)
			throw new DemoException(13);

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
				throw new DemoException("" + length, 10);
			}
			packet.setLength(len);

			byte[] angles = new byte[12];
			fis.read(angles);
			packet.setAngles(angles);

			byte[] data = new byte[(int)length];
			if (fis.read(data) == -1) {
				throw new DemoException(11);
			}
			packet.setData(data);

			if (data[0] == DemoPacket.SVC_BAD) {
				throw new DemoException(packet.toString(), 12);
			}

			if (length >= 12 && data[0] == (byte)011) {
				byte[] cutmark = new byte[11];
				System.arraycopy(data, 1, cutmark, 0, 11);
				String check = new String(cutmark);
				if (check.equals("\n//CUTMARK\n")) {
					cutmarks = 1;
				}
			}

			packets.add(packet);
		}

		fis.close();
	}

	/**
	 * Write the demo to disk
	 * @param out - the output file
	 * @throws IOException - if data cannot be written
	 * @throws DemoException - if the demo is inconsistent
	 */
	public void writeDemo(File out) throws IOException, DemoException {
		FileOutputStream fos = new FileOutputStream(out);

		if (cdtrack == null)
			throw new DemoException(20);
		fos.write(cdtrack);

		if (packets.isEmpty())
			throw new DemoException(21);

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

					dp = new DemoPacket();
					dp.setData(newdata);
					dp.setAngles(angles);
					dp.setLength(Util.unsignedIntToLEByteArray(newdata.length));
				}
			}
			dps.add(dp);
		}
		packets = dps;
		cutmarks = -1;
	}

	public void insertCutmarks(double start, double end, boolean capture) throws DemoException {
		Iterator<DemoPacket> it = packets.iterator();
		ArrayList<DemoPacket> dps = new ArrayList<DemoPacket>();
		boolean first = true;
		short demoStarted = 0;
		boolean demoStopped = false;
		double time = 0.1;

		while (it.hasNext()) {
			DemoPacket dp = it.next();
			if (dp.getTime() != 0)
				time = dp.getTime();

			if (first && start > 1) {
				dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\nslowmo 100\n\000").getBytes());
				first = false;
			}

			if (demoStarted < 1 && time > start - 50) {
				dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\nslowmo 10\n\000").getBytes());
				demoStarted = 1;
			}

			if (demoStarted < 2 && time > start - 5) {
				dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\nslowmo 1\n\000").getBytes());
				demoStarted = 2;
			}

			if (demoStarted < 3 && time > start) {
				if (capture) {
					dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\ncl_capturevideo 1\n\000").getBytes());
				} else {
					dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\nslowmo 0; defer 1 \"slowmo 1\"\n\000").getBytes());
				}
				demoStarted = 3;
			}

			if (!demoStopped && time > end) {
				if (capture) {
					dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\ncl_capturevideo 0; defer 0.5 \"disconnect\"\n\000").getBytes());
				} else {
					dp = DemoPacket.insertCutmark(dp, new String("\011\n//CUTMARK\ndefer 0.5 \"disconnect\"\n\000").getBytes());
				}
				demoStopped = true;
			}

			dps.add(dp);
		}

		packets = dps;
		cutmarks = 1;
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

			if (length >= 1 && dp.getData()[0] == DemoPacket.SVC_PRINT) {
				System.out.println(dp + "\n");
			}

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

	public boolean hasCutmarks() {
		if (cutmarks == 0) {
			Iterator<DemoPacket> it = packets.iterator();
			while (it.hasNext()) {
				DemoPacket dp = it.next();
				byte[] data = dp.getData();
				long length = Util.getLEUnsignedIntFromByteArray(dp.getLength(), 0);

				if (length >= 12 && data[0] == (byte)011) {
					byte[] cutmark = new byte[11];
					System.arraycopy(data, 1, cutmark, 0, 11);
					String check = new String(cutmark);
					if (check.equals("\n//CUTMARK\n")) {
						cutmarks = 1;
						return true;
					}
				}
			}
		}

		return cutmarks > 0;
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
			if (!ar1.get(i).equals(ar2.get(i))) {
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
