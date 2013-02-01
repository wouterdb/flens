package flens.input;

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

public abstract class ListenerInput<T> extends AbstractInput {

	private ServerSocket listener;
	private List<Handler> handlers = new LinkedList<Handler>();

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
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
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
