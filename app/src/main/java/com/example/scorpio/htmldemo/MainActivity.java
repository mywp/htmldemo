package com.example.scorpio.htmldemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private EditText erUrl;
    private TextView tvHtml;
    private static final int SUCCESS = 0;

    protected static final int ERROR = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    tvHtml.setText((String) msg.obj);
                    break;
                case ERROR:
                    Toast.makeText(MainActivity.this, "访问失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        erUrl = (EditText) findViewById(R.id.et_url);
        tvHtml = (TextView) findViewById(R.id.tv_html);

    }

    public void getHtml(View v) {
        final String url = erUrl.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求网络
                String html = getHtmlFromInternet(url);

                if (!TextUtils.isEmpty(html)) {
                    //更新textview的显示
                    Message msg = new Message();
                    msg.what = SUCCESS;
                    msg.obj = html;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /*根据指定的url访问网络，抓取html代码*/
    protected String getHtmlFromInternet(String url) {

        try {
            URL mURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);

            //conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                String html = getStringFromInputStream(is);
                return html;
            } else {
                Log.i(TAG, "访问失败" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*根据流返回一个字符串信息*/
    private String getStringFromInputStream(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;

        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();

        String html = baos.toString();//把流中的数据转换成字符串，采用的编码是：utf-8

        String charset = "utf-8";
        if (html.contains("gbk") || html.contains("gb2313") || html.contains("GBK") || html.contains("GB2313")) {//如果包含gbk，gb2312编码，就采用gb2312编码进行对字符串编码
            charset = "gbk";
        }
        html = new String(baos.toByteArray(), charset);
        baos.close();
        return html;
    }
}
