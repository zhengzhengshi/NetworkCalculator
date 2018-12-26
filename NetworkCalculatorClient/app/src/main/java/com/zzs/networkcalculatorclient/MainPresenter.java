package com.zzs.networkcalculatorclient;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zzs.networkcalculator.ResultData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zzs.networkcalculator.Constants.DISCONNECT;
import static com.zzs.networkcalculator.Constants.MESSAGE_CONNECT_SUCCESS;
import static com.zzs.networkcalculator.Constants.MESSAGE_DISCONNECT;

public class MainPresenter {
    private Context mContext;
    private ExecutorService mExecutorService;
    private PrintWriter mPrintWriter;
    private Socket mSocket;
    private ObjectInputStream mObjectInputStream;
    private String mReceiveMsg;
    private int mMessageType;
    private String mCalculateResult;
    private ResultData mResultData;

    private LoginFragment mLoginFragment;
    private CalculateFragment mCalculateFragment;
    private FragmentManager mFragmentManager;

    public MainPresenter(Context context) {
        mContext = context;
        mExecutorService = Executors.newCachedThreadPool();
        initFragment();
    }

    private void initFragment() {
        mLoginFragment = new LoginFragment();
        mCalculateFragment = new CalculateFragment();
        mLoginFragment.setPresenter(this);
        mCalculateFragment.setPresenter(this);
        mFragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_layout, mLoginFragment).addToBackStack(null).commit();
    }

    public void startConnect(String serverIp, int serverPort) {
        mExecutorService.execute(new connectService(serverIp, serverPort));
    }

    public void sendExpression(String expression) {
        mExecutorService.execute(new sendService(expression));
    }

    public void disconnect() {
        mExecutorService.execute(new sendService(DISCONNECT));
    }


    private class sendService implements Runnable {
        private String mExpression;

        public sendService(String expression) {
            this.mExpression = expression;
        }

        @Override
        public void run() {
            mPrintWriter.println(this.mExpression);
        }
    }

    private class connectService implements Runnable {
        private String mServerIp;
        private int mServerPort;

        public connectService(String serverIp, int serverPort) {
            this.mServerIp = serverIp;
            this.mServerPort = serverPort;
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(mServerIp, mServerPort);
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
                receiveMsg();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveMsg() {
        try {
            while (true) {
                try {
                    if ((mResultData = (ResultData) mObjectInputStream.readObject()) != null) {
                        switch (mResultData.getMessageType()) {
                            case MESSAGE_CONNECT_SUCCESS:
                                updateFragment(mCalculateFragment);
                                break;
                            case MESSAGE_DISCONNECT:
                                updateFragment(mLoginFragment);
                                mSocket.close();
                                mPrintWriter.close();
                                mObjectInputStream.close();
                                updateFragment(mLoginFragment);
                                return;
                            default:
                                break;
                        }
                        if (mCalculateFragment == mFragmentManager.findFragmentById(R.id.content_layout)) {
                            mCalculateFragment.showCalculateResult(mResultData);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).addToBackStack(null).commit();
    }
}
