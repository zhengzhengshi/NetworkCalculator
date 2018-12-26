package com.zzs.networkcalculatorclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculateFragment extends Fragment implements View.OnClickListener {
    private EditText mSendContent;
    private TextView mTextView;
    private Button mSendBtn;
    private Button mDisconnectBtn;
    private MainPresenter mMainPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculate_fragment, container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSendContent = view.findViewById(R.id.send_content);
        mTextView = view.findViewById(R.id.textView);
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

    public void showCalculateResult(String receiveMsg) {
        mTextView.setText(receiveMsg);
    }
}
