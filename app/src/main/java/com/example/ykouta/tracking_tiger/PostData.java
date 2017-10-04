package com.example.ykouta.tracking_tiger;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Y.Kouta on 2017/08/04.
 */

public class PostData {

    public static boolean PostLocation(String s) {
        HttpURLConnection con = null;
        URL url = null;

        String id = Build.SERIAL;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
        String strDate = sdf.format(date);
        String data = "Tablet_ID=" + id + "&Date=" + strDate;

        String urlSt = "http://pbl.ict.ehime-u.ac.jp/tiger/tiger_gps.php";

        data += s ;


        int length = 0;
        try {
            length = data.getBytes("UTF-8").length;
            Log.d("HTTP_POST", "length: " + length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

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
                    return false;
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }

                final int status = con.getResponseCode();
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
                    return false;
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } /*catch (SocketTimeoutException connected){
            while (connected.equals(false)){
                try {
                    con.connect();
                }catch (IOException e){
                }
            }
        }*/ finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return true;
        }
    }


