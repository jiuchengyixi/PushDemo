package com.common.android.utils.maputil;

/**
 * 将百度坐标转换成GPS坐标工具类
 * bd09ll  表示百度经纬度坐标，

 gcj02   表示经过国测局加密的坐标，

 wgs84   表示gps获取的坐标。

 其中最坑的是百度的坐标，百度坐标在原有的基础上自己做了一层加密，也就是bd0911，而且理论上来讲这个加密的过程是不可逆的，

 百度地图api置提供了其他的坐标类型转换成百度坐标的方法，但是这个过程并不可逆，但是确实被我找到一种办法进行转换，直接上代码
 * */
public class GlobalTool {
    public final static double a = 6378245.0;
    public final static double ee = 0.00669342162296594323;

    // 判断坐标是否在中国
    public static boolean outOfChina(BDLocation bdLocation) {
        double lat = bdLocation.getLatitude();
        double lon = bdLocation.getLongitude();
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        if ((119.962 < lon && lon < 121.750) && (21.586 < lat && lat < 25.463))
            return true;

        return false;
    }

    public final static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    public static BDLocation BAIDU_to_WGS84(BDLocation bdLocation) {
        if (outOfChina(bdLocation)) {
            return bdLocation;
        }
        double x = bdLocation.getLongitude() - 0.0065;
        double y = bdLocation.getLatitude() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        bdLocation.setLongitude(z * Math.cos(theta));
        bdLocation.setLatitude(z * Math.sin(theta));
        return GCJ02_to_WGS84(bdLocation);
    }

    public static BDLocation GCJ02_to_WGS84(BDLocation bdLocation) {
        if (outOfChina(bdLocation)) {
            return bdLocation;
        }
        BDLocation tmpLocation = new BDLocation();
        tmpLocation.setLatitude(bdLocation.getLatitude());
        tmpLocation.setLongitude(bdLocation.getLongitude());
        BDLocation tmpLatLng = WGS84_to_GCJ02(tmpLocation);
        double tmpLat = 2 * bdLocation.getLatitude() - tmpLatLng.getLatitude();
        double tmpLng = 2 * bdLocation.getLongitude()
                - tmpLatLng.getLongitude();
        for (int i = 0; i < 0; ++i) {
            tmpLocation.setLatitude(bdLocation.getLatitude());
            tmpLocation.setLongitude(bdLocation.getLongitude());
            tmpLatLng = WGS84_to_GCJ02(tmpLocation);
            tmpLat = 2 * tmpLat - tmpLatLng.getLatitude();
            tmpLng = 2 * tmpLng - tmpLatLng.getLongitude();
        }
        bdLocation.setLatitude(tmpLat);
        bdLocation.setLongitude(tmpLng);
        return bdLocation;
    }

    public static BDLocation WGS84_to_GCJ02(BDLocation bdLocation) {
        if (outOfChina(bdLocation)) {
            return bdLocation;
        }
        double dLat = transformLat(bdLocation.getLongitude() - 105.0,
                bdLocation.getLatitude() - 35.0);
        double dLon = transformLon(bdLocation.getLongitude() - 105.0,
                bdLocation.getLatitude() - 35.0);
        double radLat = bdLocation.getLatitude() / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0)
                / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        bdLocation.setLatitude(bdLocation.getLatitude() + dLat);
        bdLocation.setLongitude(bdLocation.getLongitude() + dLon);
        return bdLocation;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y
                * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x
                * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0
                * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x
                / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    public static class BDLocation {
        private double latitude;
        private double longitude;

        public BDLocation() {
        }

        public BDLocation(double longitude, double latitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

}
