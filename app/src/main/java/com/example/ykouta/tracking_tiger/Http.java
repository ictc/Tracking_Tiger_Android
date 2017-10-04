package com.example.ykouta.tracking_tiger;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Y.Kouta on 2017/06/28.
 */

public class Http extends AsyncTask<String, Void, String> {
    private TT_Main tt_main;
    private int flag = 0;
    private int cnt = 0;

    public Http(TT_Main activity){
        tt_main = activity;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    
    @Override
    protected String doInBackground(String...params) {

        String str = "";

        for (int i = 0; i < params.length; i++) {
            str += params[i];
        }

        if(!PostData.PostLocation(str)){
            String strBuffer = str;
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Non_connect", "re_connect" );
            while (!PostData.PostLocation(strBuffer) && cnt != 1) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                cnt++;
            }
            strBuffer = null;
            if(cnt >= 1) {
                flag = 1;
            }
        }

        //Double location = params[0];
        /*HttpURLConnection con = null;
        URL url = null;

        String id = Build.SERIAL;
        String data = "Tablet_ID=" + id;
        String urlSt = "http://pbl.ict.ehime-u.ac.jp/tiger/tiger_gps.php";

        for (int i = 0; i < params.length; i++) {
            data += params[i];
        }

        int length = 0;
        try {
            length = data.getBytes("UTF-8").length;
            Log.d("HTTP_POST", "length: " + length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        while (flag != 200 && cnt != 3) {
            try {
                // URLの作成
                url = new URL(urlSt);

                // 接続用HttpURLConnectionオブジェクト作成
                con = (HttpURLConnection) url.openConnection();

                // キャッシュを使用しない
                con.setUseCaches(false);

                // リクエストメソッドの設定
                //con.setRequestMethod("POST");

                // 持続接続を設定
                // con.setRequestProperty("Connection", "Keep-Alive");

                // Transfer-Encoding => chunked を使わない
                con.setFixedLengthStreamingMode(length);

                // リダイレクトを自動で許可しない設定
                con.setInstanceFollowRedirects(false);

                // URL接続からデータを読み取る場合はtrue
                con.setDoInput(true);

                // URL接続にデータを書き込む場合はtrue
                con.setDoOutput(true);

                con.setReadTimeout(60000);
                con.setConnectTimeout(60000);

                // 接続
                con.connect();

                OutputStream out = null;
                try {
                    out = con.getOutputStream();
                    //OutputStreamWriter osw = new OutputStreamWriter(out);
                    out.write(data.getBytes("UTF-8"));//("key="+String.valueOf(location)).getBytes("UTF-8")
                    out.flush();
                } catch (MalformedURLException e) {
                    //post送信エラー
                    e.printStackTrace();
                    return null;
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

                final int status = con.getResponseCode();
                flag = status;
                Log.d("HTTP_POST", "ResponseCode" + status);
                if (status == HttpURLConnection.HTTP_OK) {
                    // 正常

                    // データの読み取り
                    InputStream in = null;
                    try {
                        in = con.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        String st = null;
                        while ((st = br.readLine()) != null) {
                            Log.d("HTTP_POST", st);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                    }

                } else {
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            cnt++;
        }

        return data;*/
        return str;
    }

    
    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        String str = result;
        if(flag == 0) {
            Toast.makeText(tt_main, str + "送信しました", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(tt_main, str + "送信できませんでした", Toast.LENGTH_LONG).show();
        }

        flag = 0;
    }
}
