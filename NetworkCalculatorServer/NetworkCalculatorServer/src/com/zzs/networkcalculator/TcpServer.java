package com.zzs.networkcalculator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TcpServer {
	private static final int PORT = 9999;
	private ServerSocket mServerSocket = null;
	private ExecutorService mExecutorService = null;

	public static void main(String[] args) {
		new TcpServer();
	}

	public TcpServer() {
		try {
			mServerSocket = new ServerSocket(PORT);
			mExecutorService = Executors.newCachedThreadPool();
			getLocalIp();
			// Get server IP.
			System.out.println("Server port:" + PORT);
			System.out.println("Service starts");
			Socket clientSocket = null;
			while (true) {
				clientSocket = mServerSocket.accept();
				mExecutorService.execute(new ServiceRunable(clientSocket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class ServiceRunable implements Runnable {
		private String mReceiveMsg;
		private ResultData mResultData;
		private Socket mSocket;
		private BufferedReader mBufferedReader = null;
		private ObjectOutputStream mObjectOutputStream = null;
		private int mMessageType;
		private String mMessageText;
		private String mCalculateResult;
		private String mClientAdress;
		private StackCalculators mStackCalculators;

		public ServiceRunable(Socket socket) {
			this.mSocket = socket;
			try {
				mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
				mMessageType = Constants.MESSAGE_CONNECT_SUCCESS;
				mMessageText = "Conenect success";
				mCalculateResult = "";
				mResultData = new ResultData(mMessageType, mMessageText, mCalculateResult);
				mObjectOutputStream.writeObject(mResultData);
				mObjectOutputStream.flush();
				mClientAdress = mSocket.getInetAddress().getHostAddress();
				System.out.println(mClientAdress + ":Connect to server success");
				mStackCalculators = new StackCalculators();
				mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					if ((mReceiveMsg = mBufferedReader.readLine()) != null) {
						System.out.println("Receive Message:" + mReceiveMsg);
						if (mReceiveMsg.equals(Constants.DISCONNECT)) {
							mMessageType = Constants.MESSAGE_DISCONNECT;
							mMessageText = "Disconnect success";
							mCalculateResult = "";
							System.out.println(mClientAdress + ":" + mMessageText);
							mResultData = new ResultData(mMessageType, mMessageText, mCalculateResult);
							mObjectOutputStream.writeObject(mResultData);
							mObjectOutputStream.flush();
							mSocket.close();
							mBufferedReader.close();
							mObjectOutputStream.close();
							break;
						} else {
							System.out.println(mClientAdress + ":receiveMsg:" + mReceiveMsg);
							if (!"".equals(Utils.checkInput(mReceiveMsg))) {
								mMessageType = Constants.MESSAGE_RESULT_ERROR;
								mMessageText = Utils.checkInput(mReceiveMsg);
								mCalculateResult = "";
								System.out.println(mClientAdress + ":Check input:" + Utils.checkInput(mReceiveMsg));
							} else {
								mMessageType = Constants.MESSAGE_RESULT_SUCCESS;
								mMessageText = "Calculate success";
								mCalculateResult = mStackCalculators.calculate(mReceiveMsg);
								System.out.println(mClientAdress + ":calculate result:" + mCalculateResult);
							}
							mResultData = new ResultData(mMessageType, mMessageText, mCalculateResult);
							mObjectOutputStream.writeObject(mResultData);
							mObjectOutputStream.flush();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean isWindowOS() {
		boolean isWindowOS = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowOS = true;
		}
		return isWindowOS;
	}

	private void getLocalIp() {
		Enumeration<NetworkInterface> allNetInterfaces; // 定义网络接口枚举类
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces(); // 获得网络接口

			InetAddress ip = null; // 声明一个InetAddress类型ip地址
			while (allNetInterfaces.hasMoreElements()) // 遍历所有的网络接口
			{
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses(); // 同样再定义网络地址枚举类
				while (addresses.hasMoreElements()) {
					ip = addresses.nextElement();
					if (ip != null && !ip.isLinkLocalAddress() && !ip.isLoopbackAddress()
							&& ip instanceof Inet4Address) {
						System.out.println("Server IP: " + ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
