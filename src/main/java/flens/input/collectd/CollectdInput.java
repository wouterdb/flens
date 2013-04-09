/*
 * jcollectd
 * Copyright (C) 2009 Hyperic, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; only version 2 of the License is applicable.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package flens.input.collectd;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Constants;
import flens.core.Record;
import flens.core.Tagger;
import flens.input.Severity;
import flens.input.util.AbstractActiveInput;

/**
 * collectd UDP protocol receiver. See collectd/src/network.c:parse_packet
 */
public class CollectdInput extends AbstractActiveInput {

	public CollectdInput(String name, Tagger tagger, int port,
			String bindAddress, String _ifAddress) {
		super(name, tagger);
		this._port = port;
		this._bindAddress = bindAddress;
		this._ifAddress = _ifAddress;
	}

	private static final Logger _log = Logger.getLogger(CollectdInput.class
			.getName());
	private static final int BUFFER_SIZE = 1472;

	static final int UINT8_LEN = 1;
	static final int UINT16_LEN = UINT8_LEN * 2;
	static final int UINT32_LEN = UINT16_LEN * 2;
	static final int UINT64_LEN = UINT32_LEN * 2;
	public static final int HEADER_LEN = UINT16_LEN * 2;

	private DatagramSocket _socket;
	private int _port;
	private String _bindAddress;
	private String _ifAddress;

	protected int getPort() {
		return _port;
	}

	public void setPort(int port) {
		_port = port;
	}

	public String getListenAddress() {
		return _bindAddress;
	}

	public void setListenAddress(String address) {
		_bindAddress = address;
	}

	public String getInterfaceAddress() {
		return _ifAddress;
	}

	public void setInterfaceAddress(String address) {
		_ifAddress = address;
	}

	public DatagramSocket getSocket() throws IOException {
		if (_socket == null) {
			if (_bindAddress == null) {
				_socket = new DatagramSocket(_port);
			} else {
				InetAddress addr = InetAddress.getByName(_bindAddress);
				if (addr.isMulticastAddress()) {
					MulticastSocket mcast = new MulticastSocket(_port);
					if (_ifAddress != null) {
						mcast.setInterface(InetAddress.getByName(_ifAddress));
					}
					mcast.joinGroup(addr);
					_socket = mcast;
				} else {
					_socket = new DatagramSocket(_port, addr);
				}
			}
		}
		return _socket;
	}

	public void setSocket(DatagramSocket socket) {
		_socket = socket;
	}

	private String readString(DataInputStream is, int len) throws IOException {
		byte[] buf = new byte[len];
		is.read(buf, 0, len);
		if (len == 1)
			return null;
		return new String(buf, 0, len - 1); // -1 -> skip \0
	}

	private List<Number> readValues(DataInputStream is) throws IOException {
		byte[] dbuff = new byte[8];
		int nvalues = is.readUnsignedShort();
		int[] types = new int[nvalues];
		for (int i = 0; i < nvalues; i++) {
			types[i] = is.readByte();
		}
		List<Number> values = new ArrayList<Number>();
		for (int i = 0; i < nvalues; i++) {
			Number val;
			if (types[i] != DataSource.Type.GAUGE.value()) {
				val = is.readLong();
			} else {
				// collectd uses x86 host order for doubles
				is.read(dbuff);
				ByteBuffer bb = ByteBuffer.wrap(dbuff);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				val = bb.getDouble();

			}
			values.add(val);
		}
		/*
		 * if (_dispatcher != null) { _dispatcher.dispatch(values); }
		 */
		return values;
	}

	public void parse(byte[] packet) throws IOException {
		int total = packet.length;
		ByteArrayInputStream buffer = new ByteArrayInputStream(packet);
		DataInputStream is = new DataInputStream(buffer);

		Record out = new Record(null);

		while ((0 < total) && (total > HEADER_LEN)) {
			int type = is.readUnsignedShort();
			int len = is.readUnsignedShort();

			if (len < HEADER_LEN) {
				break; // packet was filled to the brim
			}

			total -= len;
			len -= HEADER_LEN;

			switch (Part.find(type)) {
			case VALUES:
				List values = readValues(is);
				out.setValue("values", values);
				expandAndDispatch(out);
				break;
			case TIME:
				long tmp = is.readLong() * 1000;
				out.setTimestamp(tmp);
				break;
			case INTERVAL:
				long interval = is.readLong();
				out.getValues().put(Constants.INTERVAL, interval);
				break;
			case TIME_HIRES:
				long th = is.readLong();
				long thi = th / 1073741824L * 1000L;
				long thf = th % 1073741824L * 1000L / 1073741824L;
				out.setTimestamp(thi + thf);
				break;
			case INTERVAL_HIRES:
				interval = is.readLong();
				out.getValues().put(Constants.INTERVAL, interval);
				break;
			case HOST:
				String host = readString(is, len);
				out.setSource(host);
				break;
			case PLUGIN:
				String plugin = readString(is, len);
				out.getValues().put(Constants.PLUGIN, plugin);
				break;
			case PLUGIN_INSTANCE:
				String pluginInstance = readString(is, len);
				out.getValues().put(Constants.PLUGIN_INSTANCE, pluginInstance);
				break;
			case TYPE:
				String _type = readString(is, len);
				out.getValues().put(Constants.TYPE, _type);
				break;
			case TYPE_INSTANCE:
				String tI = readString(is, len);
				out.getValues().put(Constants.TYPE_INSTANCE, tI);
				break;
			case MESSAGE:
				String msg = readString(is, len);
				out.setValue("message", msg);
				expandAndDispatch(out);
				break;
			case SEVERITY:
				int sev = (int) is.readLong();
				Severity severity = Severity.find(sev);
				out.getValues().put(Constants.SEVERITY, severity.name());
				break;
			default:
				break;
			}
		}

	}

	public void run() {
		try {
			listen(getSocket());
		} catch (IOException e) {
			err("main loop broken", e);
			stop();
		}
	}

	void listen(DatagramSocket socket) throws IOException {
		while (true) {
			byte[] buf = new byte[BUFFER_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (SocketException e) {
				if (!running) {
					break;
				} else {
					throw e;
				}
			}
			parse(packet.getData());

			// dispatch(data);

		}
	}

	private final CollectdExpansionTable cdet = new CollectdExpansionTable();

	/**
	 * collectd has multi-valued metrics. The values are not self-descriptive
	 * 
	 * @param rec
	 */
	private void expandAndDispatch(Record rec) {
		List<Number> nx = (List<Number>) rec.getValues().get(Constants.VALUES);
		int size = nx.size();

		// single valued
		if (size == 1) {
			Number n = (Number) nx.get(0);
			rec.getValues().remove("values");
			rec.setValue("value", n);
			dispatch(rec.doClone());
			return;
		}

		// multi valued
		String[] names = cdet.resolve(rec);
		if (names == null) {
			_log.warning("multi valued record not exanded: " + rec);
			dispatch(rec.doClone());
			return;
		}

		if (size != names.length) {
			_log.log(Level.SEVERE,
					"multi valued record has unexpected number of values: "
							+ rec + " " + names);
			dispatch(rec.doClone());
			return;
		}

		rec.getValues().remove(Constants.VALUES);

		for (int i = 0; i < names.length; i++) {
			Record out = (Record) rec.doClone();
			out.getValues().put(Constants.VALUE, nx.get(i));
			out.getValues().put(Constants.TYPE_INSTANCE, names[i]);
			dispatch(out);
		}

	}

	public void stop() {
		super.stop();
		if (_socket != null) {
			_socket.close();
			_socket = null;
		}
	}

}
