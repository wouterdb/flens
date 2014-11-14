/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: wouter.deborger@cs.kuleuven.be
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
import flens.input.collectd.TypeingTable.Mapping;
import flens.input.util.AbstractActiveInput;

/**
 * collectd UDP protocol receiver. See collectd/src/network.c:parse_packet
 */
public class CollectdInput extends AbstractActiveInput {

	public CollectdInput(String name, String plugin, Tagger tagger, int port, String bindAddress, String _ifAddress) {
		super(name, plugin, tagger);
		this.port = port;
		this.bindAddress = bindAddress;
		this.ifAddress = _ifAddress;
	}

	private static final Logger _log = Logger.getLogger(CollectdInput.class.getName());
	private static final int BUFFER_SIZE = 1472;

	static final int UINT8_LEN = 1;
	static final int UINT16_LEN = UINT8_LEN * 2;
	static final int UINT32_LEN = UINT16_LEN * 2;
	static final int UINT64_LEN = UINT32_LEN * 2;
	public static final int HEADER_LEN = UINT16_LEN * 2;

	private DatagramSocket _socket;
	private int port;
	private String bindAddress;
	private String ifAddress;

	protected int getPort() {
		return port;
	}

	public String getListenAddress() {
		return bindAddress;
	}

	public String getInterfaceAddress() {
		return ifAddress;
	}

	public DatagramSocket getSocket() throws IOException {
		if (_socket == null) {
			if (bindAddress == null) {
				_socket = new DatagramSocket(port);
			} else {
				InetAddress addr = InetAddress.getByName(bindAddress);
				if (addr.isMulticastAddress()) {
					MulticastSocket mcast = new MulticastSocket(port);
					if (ifAddress != null) {
						mcast.setInterface(InetAddress.getByName(ifAddress));
					}
					mcast.joinGroup(addr);
					_socket = mcast;
				} else {
					_socket = new DatagramSocket(port, addr);
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

		Record out = new Record();

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
				List<?> values = readValues(is);
				out.setValue(CollectdConstants.VALUES, values);
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
				out.getValues().put(CollectdConstants.PLUGIN, plugin);
				break;
			case PLUGIN_INSTANCE:
				String pluginInstance = readString(is, len);
				out.getValues().put(CollectdConstants.PLUGIN_INSTANCE, pluginInstance);
				break;
			case TYPE:
				String _type = readString(is, len);
				out.getValues().put(Constants.TYPE, _type);
				break;
			case TYPE_INSTANCE:
				String tI = readString(is, len);
				out.getValues().put(CollectdConstants.TYPE_INSTANCE, tI);
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

	private final CollectdTypeingTable cdet = new CollectdTypeingTable();

	/**
	 * collectd has multi-valued metrics. The values are not self-descriptive
	 * 
	 * @param rec
	 */
	@SuppressWarnings("unchecked")
	private void expandAndDispatch(Record rec) {
		List<Number> values = (List<Number>) rec.getValues().get(CollectdConstants.VALUES);
		int size = values.size();

		Mapping typeing = cdet.resolve(rec);
		if (typeing == null) {
			failAndDispatch(rec, "multi valued record not exanded");
			return;
		}

		if (size != typeing.getNames().length) {
			failAndDispatch(rec, "multi valued record has unexpected number of values");
			return;
		}

		normalizeAndDispatch(rec, values, typeing);

	}

	private void normalizeAndDispatch(Record rec, List<Number> values, Mapping typeing) {
		rec.getValues().remove(CollectdConstants.VALUES);
		rec = rec.doClone();
		
		//cut out collectd specific parts
		rec.getValues().remove(CollectdConstants.PLUGIN);
		Object instance = rec.getValues().remove(CollectdConstants.PLUGIN_INSTANCE);
		rec.getValues().remove(CollectdConstants.TYPE_INSTANCE);
		if(instance!=null){
			rec.getValues().put(Constants.INSTANCE, instance);
			rec.getValues().put(Constants.TYPE, typeing.plugin);
		}else{
			rec.getValues().remove(Constants.TYPE);
		}
			
		
		for(int i =0;i<typeing.names.length;i++){
			Number value = values.get(i);
			Record out = rec.doClone();
			out.getValues().put(Constants.VALUE, value);
			out.getValues().put(Constants.METRIC, typeing.names[i]);
			
			
			dispatch(out);
		}
		
	}

	private void failAndDispatch(Record rec, String msg) {
		err(msg + ":" + rec.toString());
		dispatch(rec.doClone());
	}

	public void stop() {
		super.stop();
		if (_socket != null) {
			_socket.close();
			_socket = null;
		}
	}

}
