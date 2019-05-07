package com.common.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 网络连接的一些工具类
 */
public class NetWorkUtil {
    // 当前网络连接
    public static final int CONNECTION_TYPE_NO_CONNECT = 0;
    public static final int CONNECTION_TYPE_WAP = 1;
    public static final int CONNECTION_TYPE_CMNET = 2;
    public static final int CONNECTION_TYPE_WIFI = 3;
    private static final String TAG = "NetWorkUtil";

    /**
     * 枚举网络状态 NET_NO：没有网络； NET_2G：2g网络； NET_3G : 3g网络； NET_4G：4g网络；
     * NET_WIFI：wifi； NET_UNKNOWN：未知网络
     */
    public enum NetState {
        NET_NO, NET_2G, NET_3G, NET_4G, NET_WIFI, NET_UNKNOWN
    }

    /**
     * 判断WIFI是否使用
     */
    public static boolean isWIFIActivate(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .isWifiEnabled();
    }


    /**
     * 判断当前网络是否可用
     */
    public static boolean isNetAvailable(NetState state) {
        return state != NetState.NET_UNKNOWN && state != NetState.NET_NO;
    }

    /**
     * 修改WIFI状态
     *
     * @param status true为开启WIFI，false为关闭WIFI
     */
    public static void changeWIFIStatus(Context context, boolean status) {
        ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .setWifiEnabled(status);
    }


    /**
     * 获取联网的类型，区分Wifi，CMNET，CMWAP
     *
     * @param context
     * @return
     */
    public static int getConnectType(Context context) {
        ConnectivityManager lcm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo.State wifi = lcm.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();

        if (wifi == NetworkInfo.State.CONNECTED
                || wifi == NetworkInfo.State.CONNECTING) {
            return CONNECTION_TYPE_WIFI;
        } else {
            NetworkInfo.State mobile = lcm.getNetworkInfo(
                    ConnectivityManager.TYPE_MOBILE).getState();
            if (mobile == NetworkInfo.State.CONNECTED
                    || mobile == NetworkInfo.State.CONNECTING) {

                // 主apn的uri
                Uri uri = Uri.parse("content://telephony/carriers/preferapn");
                Cursor mCursor = null;
                try {
                    mCursor = context.getContentResolver().query(uri, null,
                            null, null, null);
                    if (mCursor != null) {

                        // 游标移至第一条记录，当然也只有一条
                        mCursor.moveToNext();
                        String apn = mCursor.getString(
                                mCursor.getColumnIndex("apn")).toLowerCase();
                        if (apn != null
                                && (apn.equals("3gnet") || apn.equals("cmnet") || apn
                                .equals("uninet"))) {
                            return CONNECTION_TYPE_CMNET;
                        } else {
                            String proxyStr = mCursor.getString(mCursor
                                    .getColumnIndex("proxy"));
                            if (proxyStr != null
                                    && proxyStr.trim().length() > 0) {
                                return CONNECTION_TYPE_WAP;
                            } else {
                                return CONNECTION_TYPE_CMNET;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mCursor != null) {
                        mCursor.close();
                    }
                }
                return CONNECTION_TYPE_WAP;
            }
        }
        return CONNECTION_TYPE_NO_CONNECT;
    }

    /**
     * 判断当前是否网络连接类型
     *
     * @param context
     * @return 网络类型
     * @see NetWorkUtil.NetState
     */
    public static NetState getConnectState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return getConnectState(ni);
    }

    /**
     * 判断当前是否网络连接类型
     *
     * @param ni
     * @return 网络类型
     * @see NetWorkUtil.NetState
     */
    public static NetState getConnectState(NetworkInfo ni) {
        NetState stateCode = NetState.NET_NO;
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    stateCode = NetState.NET_WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            stateCode = NetState.NET_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            stateCode = NetState.NET_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            stateCode = NetState.NET_4G;
                            break;
                        default:
                            stateCode = NetState.NET_UNKNOWN;
                    }
                    break;
                default:
                    stateCode = NetState.NET_UNKNOWN;
            }

        }
        return stateCode;
    }




    /**
     * 判断联网
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有网络连接
     */
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断MOBILE网络是否可用
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获得IP地址
     */
    public static String getIP() {
        String IP = null;
        StringBuilder IPStringBuilder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration
                        .nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface
                        .getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration
                            .nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        IPStringBuilder.append(inetAddress.getHostAddress()
                                .toString() + " ");
                    }
                }
            }
        } catch (SocketException ex) {

        }

        IP = IPStringBuilder.toString();
        return IP;
    }

    /**
     * 获得本地Mac地址，需要内置busybox工具
     */
    public static String getLocalMacAddress(Context mc) {
        String defmac = "00:00:00:00:00:00";
        InputStream input = null;
        String wifimac = getWifiMacAddress(mc);
        if (null != wifimac) {
            if (!wifimac.equals(defmac))
                return wifimac;
        }
        try {
            ProcessBuilder builder = new ProcessBuilder("busybox", "ifconfig");
            Process process = builder.start();
            input = process.getInputStream();

            byte[] b = new byte[1024];
            StringBuffer buffer = new StringBuffer();
            while (input.read(b) > 0) {
                buffer.append(new String(b));
            }
            String value = buffer.substring(0);
            String systemFlag = "HWaddr ";
            int index = value.indexOf(systemFlag);
            // List <String> address = new ArrayList <String> ();
            if (0 < index) {
                value = buffer.substring(index + systemFlag.length());
                // address.add(value.substring(0,18));
                defmac = value.substring(0, 17);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defmac;
    }

    /**
     * 获得WIFI MAC地址
     */
    public static String getWifiMacAddress(Context mc) {
        WifiManager wifi = (WifiManager) mc
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 判断 IP 地址在当前局域网中是否可连接
     */
    public synchronized static boolean isUsedIPAddress(String ip) {
        Process process = null;
        BufferedReader bufReader = null;
        String bufReadLineString = null;
        try {
            process = Runtime.getRuntime().exec("ping " + ip + " -w 100 -n 1");
            bufReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            for (int i = 0; i < 6 && bufReader != null; i++) {
                bufReader.readLine();
            }
            bufReadLineString = bufReader.readLine();
            if (bufReadLineString == null) {
                process.destroy();
                return false;
            }
            if (bufReadLineString.indexOf("timed out") > 0
                    || bufReadLineString.length() < 17
                    || bufReadLineString.indexOf("invalid") > 0) {
                process.destroy();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
        return true;

    }

    private static SSLSocketFactory sslSocketFactory;

    /**
     * 信任所有链接
     */
    public static void trustAllHttpsURLConnection() {
        // Create a trust manager that does not validate certificate chains
        if (sslSocketFactory == null) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            }};
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (Throwable e) {
                Logger.e(TAG, e.getMessage());
            }
        }

        if (sslSocketFactory != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
            HttpsURLConnection
                    .setDefaultHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
    }

    public static String GetNetIp() {
        String IP = "";
        try {
            String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setUseCaches(false);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();

                // 将流转化为字符串
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String tmpString = "";
                StringBuilder retJSON = new StringBuilder();
                while ((tmpString = reader.readLine()) != null) {
                    retJSON.append(tmpString + "\n");
                }

                JSONObject jsonObject = new JSONObject(retJSON.toString());
                String code = jsonObject.getString("code");
                if (code.equals("0")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    IP = data.getString("ip") + "(" + data.getString("country")
                            + data.getString("area") + "区"
                            + data.getString("region") + data.getString("city")
                            + data.getString("isp") + ")";

                    Log.e("提示", "您的IP地址是：" + IP);
                } else {
                    IP = "";
                    Log.e("提示", "IP接口异常，无法获取IP地址！");
                }
            } else {
                IP = "";
                Log.e("提示", "网络连接异常，无法获取IP地址！");
            }
        } catch (Exception e) {
            IP = "";
            Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
        }
        return IP;
    }

    /**
     * 移动数据开启和关闭
     */
    public static void setMobileDataStatus(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        //ConnectivityManager类中的字段
        Field iConMgrField = null;
        //IConnectivityManager类的引用
        Object iConMgr = null;
        //IConnectivityManager类
        Class<?> iConMgrClass = null;
        //setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try {
            //取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            //取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            //设置mService可访问
            iConMgrField.setAccessible(true);
            //取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            //取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            //取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

            //设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            //调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取移动数据开关状态
     */
    public static boolean getMobileDataStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Class cmClass = cm.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod("getMobileDataEnabled", argClasses);
            isOpen = (Boolean) method.invoke(cm, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }
}
