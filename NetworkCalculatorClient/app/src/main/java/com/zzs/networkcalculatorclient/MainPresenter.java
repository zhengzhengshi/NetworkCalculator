package com.zzs.networkcalculatorclient;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.zzs.networkcalculator.ResultData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
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
    private String mServerIp;
    private int mServerPort;
    private Timer mTimer;

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
        this.mServerIp = serverIp;
        this.mServerPort = serverPort;
        mExecutorService.execute(new connectService());
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

        public connectService() {
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(mServerIp, mServerPort);
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
                mLoginFragment.connectSuccessToast();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            //TODO 
                            mSocket.sendUrgentData(0xff);
                        } catch (Exception e) {
                            e.printStackTrace();
                            updateFragment(mLoginFragment);
                            //releaseSocket();
                            //mExecutorService.execute(new connectService());
                        }
                    }
                }, 0, 1);

                receiveMsg();
            } catch (IOException e) {
                e.printStackTrace();
                releaseSocket();
                mExecutorService.execute(new connectService());
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
                                releaseSocket();
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

    private void releaseSocket() {
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
        if (mPrintWriter != null) {
            mPrintWriter.close();

        }
        if (mObjectInputStream != null) {
            try {
                mObjectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.content_layout, fragment).addToBackStack(null).commit();
    }
}
