package com.zzs.networkcalculatorclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzs.networkcalculator.ResultData;

public class CalculateFragment extends Fragment implements View.OnClickListener {
    private EditText mSendContent;
    private TextView mMessageText;
    private ExpressionView mResultText;
    private Button mSendBtn;
    private Button mDisconnectBtn;
    private MainPresenter mMainPresenter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculate_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSendContent = view.findViewById(R.id.send_content);
        mMessageText = view.findViewById(R.id.message_text);
        mResultText = view.findViewById(R.id.result_text);
        mSendBtn = view.findViewById(R.id.send);
        mDisconnectBtn = view.findViewById(R.id.disconnect);
        mSendBtn.setOnClickListener(this);
        mDisconnectBtn.setOnClickListener(this);
    }

    public void setPresenter(MainPresenter mainPresenter) {
        this.mMainPresenter = mainPresenter;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                String sendMsg = mSendContent.getText().toString();
                mMainPresenter.sendExpression(sendMsg);
                break;
            case R.id.disconnect:
                mMainPresenter.disconnect();
                break;
            default:
                break;
        }
    }

    public void showCalculateResult(final ResultData resultData) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageText.setText(resultData.getMessageText());
                mResultText.setExpression(resultData.getCalculateResult());
                mResultText.invalidate();
            }
        });

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
