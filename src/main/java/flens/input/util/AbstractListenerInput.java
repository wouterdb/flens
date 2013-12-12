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
package flens.input.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import flens.core.Tagger;

public abstract class AbstractListenerInput<T> extends AbstractActiveInput {

	private ServerSocket listener;
	private List<Handler> handlers = new LinkedList<Handler>();

	public AbstractListenerInput(String name,Tagger tagger) {
		super(name,tagger);
	}

	@Override
	public void start() {
		try {
			listener = makeListener();
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"could not open server socket", e);
		}
		super.start();
	}

	@Override
	public void stop() {
		try {
			listener.close();
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"could not close server socket", e);
		}
		super.stop();
		synchronized (handlers) {
			for (Handler handler : handlers) {
				handler.interrupt();
			}
		}

	}

	@Override
	public void join() throws InterruptedException {
		super.join();
		for (Handler handler : handlers) {
			handler.join();
		}
	}

	protected abstract ServerSocket makeListener() throws IOException;

	public void run() {
		try {
			while (running) {
				handle(listener.accept());
			}
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,
					"listener socket broken", e);
		}
	}

	private void handle(Socket newSocket) {
		synchronized (handlers) {
			handlers.add(getHandler(newSocket));
		}
	}

	public Handler getHandler(Socket newSocket) {
		return new Handler(newSocket);
	}

	public abstract T getStream(Socket newSocket) throws IOException;

	public abstract void readAndProcess(T in) throws IOException;

	public abstract void tearDown(T in2) throws IOException;

	class Handler extends Thread {

		private Socket socket;
		private T in;

		public Handler(Socket newSocket) {
			this.socket = newSocket;
			try {
				this.in = getStream(socket);
				this.start();
			} catch (IOException e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE,
						"can not open socket stream", e);
			}

		}

		@Override
		public void run() {
			try {
				while (running) {
					readAndProcess(in);
				}
			} catch (IOException e) {
				Logger.getLogger(getClass().getName()).log(Level.INFO,
						"socket broken", e);
			} finally {
				try {
					tearDown(in);
					socket.close();
				} catch (IOException e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING,
							"could not close socket", e);
				}
			}
		}

	}

}
