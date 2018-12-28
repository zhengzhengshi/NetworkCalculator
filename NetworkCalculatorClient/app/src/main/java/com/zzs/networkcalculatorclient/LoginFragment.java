package com.zzs.networkcalculatorclient;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mServerIp;
    private EditText mServerPort;
    private Button mConnectBtn;
    private MainPresenter mMainPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mServerIp = view.findViewById(R.id.server_ip);
        mServerPort = view.findViewById(R.id.server_port);
        mConnectBtn = view.findViewById(R.id.connect);
        mConnectBtn.setOnClickListener(this);
    }

    public void setPresenter(MainPresenter mainPresenter) {
        this.mMainPresenter = mainPresenter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect:
                String serverIp = mServerIp.getText().toString();
                int serverPort = Integer.parseInt(mServerPort.getText().toString());
                mMainPresenter.startConnect(serverIp, serverPort);
                break;
            default:
                break;
        }
    }

    public void connectErrorToast() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "connect error, trying to reconnect..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void connectSuccessToast() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "connect success", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
