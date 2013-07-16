package trash;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;


import android.content.Context;
import android.util.Log;

public class WebServer {

	public static boolean RUNNING = false;
	public static int serverPort = 8080;
	public int port;
	private String password;

	private static final String ALL_PATTERN = "*";
	private static final String EXCEL_PATTERN = "/*.xls";
	private static final String HOME_PATTERN = "/home.html";

	private Context context = null;

	private BasicHttpProcessor httpproc = null;
	private BasicHttpContext httpContext = null;
	private HttpService httpService = null;
	private HttpRequestHandlerRegistry registry = null;

	public WebServer(Context context) {
		this.setContext(context);

		httpproc = new BasicHttpProcessor();
		httpContext = new BasicHttpContext();

		httpproc.addInterceptor(new ResponseDate());
		httpproc.addInterceptor(new ResponseServer());
		httpproc.addInterceptor(new ResponseContent());
		httpproc.addInterceptor(new ResponseConnControl());

		httpService = new HttpService(httpproc,
		    new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());

		registry = new HttpRequestHandlerRegistry();

		registry.register(HOME_PATTERN, new HttpCommandHandler(context));

		httpService.setHandlerResolver(registry);
	}

	private ServerSocket serverSocket;

	
	public void runServer(int port) {
		try {
			setPort(port);
			Log.i("run","przed socketem");
		//	serverSocket = new ServerSocket(serverPort);
			serverSocket = new ServerSocket(port);
			getLocalIpAddress();
			Log.i("run","po socketecie");
		//	getLocalIpAddress();
			serverSocket.setReuseAddress(true);

			while (RUNNING) {
				try {
					final Socket socket = serverSocket.accept();
//					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+getLocalIpAddress()+":8080/home.html"));
//					startActivity(browserIntent);
					DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();

					serverConnection.bind(socket, new BasicHttpParams());

					httpService.handleRequest(serverConnection, httpContext);

					serverConnection.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				}
			}

			serverSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		RUNNING = false;
	}

	public synchronized void startServer(int port) {
		RUNNING = true;
		runServer(port);
	}
	
	public synchronized void startServer() {
		RUNNING = true;
		runServer(8080);
	}

	public synchronized void stopServer() {
		RUNNING = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	  private String getLocalIpAddress() {
	        try {
	            for (Enumeration<NetworkInterface> en = NetworkInterface
	                    .getNetworkInterfaces(); en.hasMoreElements();) {
	                NetworkInterface intf = en.nextElement();
	                for (Enumeration<InetAddress> enumIpAddr = intf
	                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                    InetAddress inetAddress = enumIpAddr.nextElement();
	                    if (!inetAddress.isLoopbackAddress()) {
	                        return inetAddress.getHostAddress().toString();
	                    }
	                }
	            }
	        } catch (SocketException ex) {
	            return "ERROR Obtaining IP";
	        }
	        return "No IP Available";
	    }
	
		protected InputStream openUrl() throws MalformedURLException, IOException {
			String url = "http://www.ii.uj.edu.pl/~surmacka/";
		    InputStream stream = null;
		    HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		        stream = connection.getInputStream();
		    }
		    return stream;
		}
	  
		
		private static String tryGetIpAddress()
    {
        try
        {
            final List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (final NetworkInterface intf : interfaces)
            {
                final List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (final InetAddress addr : addrs)
                {
                    if (!addr.isLoopbackAddress())
                    {
                        final String sAddr = addr.getHostAddress().toUpperCase();
                        if (InetAddressUtils.isIPv4Address(sAddr))
                        {
                            return sAddr;
                        } // if
                        else
                        {
                            // Drop IP6 port suffix
                            final int delim = sAddr.indexOf('%');
                            return delim < 0 ? sAddr : sAddr.substring(0, delim);
                        } // else
                    } // if
                } // for
            } // for
        } // try
        catch (final Exception e)
        {
            // Ignore
        } // for now eat exceptions
        return null;
    } // tryGetIpAddress()
		
		
		void setPort(int port){
			this.port = port;
		}
		
		void setPassword(String password){
			this.password = password;
		}
}