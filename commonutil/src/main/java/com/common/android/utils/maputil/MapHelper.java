package com.common.android.utils.maputil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.common.android.R;
import com.common.android.utils.Logger;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MapHelper {
    //百度地图：com.baidu.BaiduMap
    //高德地图：com.autonavi.minimap
    //腾讯地图：com.tencent.map
    //谷歌地图：com.google.android.apps.maps

    private static final String Autonavi_Map = "com.autonavi.minimap";
    private static final String Baidu_Map = "com.baidu.BaiduMap";
    private static final String Tencent_Map = "com.tencent.map";
    private static final String Google_Map = "com.google.android.apps.maps";

    private static int gaode_navi_type = 1; //高德   公交:1, 驾车:2 ,步行:4
    private static String baidu_navi_type = "driving"; //百度   transit:公交、driving:驾车、walking:步行
    private static String tenxun_navi_type = "drive"; //腾讯    bus:公交, :drive: 步行:walk
    private static String google_navi_type = "d"; //google    d:行车  w:步行  b:骑行

    private static String Source_App = "左象";

    private static boolean needConvert = true; //是否需要将百度坐标进行转换

    public static void setNeedConvert(boolean needConvert) {
        MapHelper.needConvert = needConvert;
    }

    private static List<PackageInfo> initComponentInfo(Context context) {
        List<String> maps = new ArrayList<String>();
        maps.add(Autonavi_Map);
        maps.add(Baidu_Map);
        maps.add(Tencent_Map);
        maps.add(Google_Map);
        return getComponentInfo(context, maps);
    }

    private static List<PackageInfo> getComponentInfo(Context context,
                                                      List<String> maps) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        List<PackageInfo> available = new ArrayList<PackageInfo>();
        if ((infos != null) && (infos.size() > 0))
            for (PackageInfo info : infos) {
                String packName = info.packageName;
                if (!TextUtils.isEmpty(packName) && maps.contains(packName)) {
                    if (packName.equals(Autonavi_Map)) {
                        if (info.versionCode >= 161)
                            available.add(info);
                    } else {
                        available.add(info);
                    }
                }
            }
        return available;
    }

    public static void navigate(Context context, PackageInfo info, MapLocation origin, MapLocation des) {
        Intent intent = null;
        if (info == null) {
            intent = intentForAmapWeb(origin, des);
        } else {
            if (Autonavi_Map.equalsIgnoreCase(info.applicationInfo.packageName)) {
                intent = intentForAmap(origin, des);
            } else if (Baidu_Map.equalsIgnoreCase(info.applicationInfo.packageName)) {
                intent = intentForBaidu(origin, des);
            } else if (Tencent_Map.equalsIgnoreCase(info.applicationInfo.packageName)) {
                intent = intentForTencent(origin, des);
            } else if (Google_Map.equalsIgnoreCase(info.applicationInfo.packageName)) {
                intent = intentForGoogle(origin, des);
            }
        }
        if (intent != null) {
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Logger.e("mapHelper", "navigate error");
                Toast.makeText(context, R.string.location_open_map_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private static Intent intentForAmapWeb(MapLocation origin, MapLocation des) {
        Object[] arrayOfObject = new Object[4];

        arrayOfObject[0] = origin.getLongitude();
        arrayOfObject[1] = origin.getLatitude();
        arrayOfObject[2] = des.getLongitude();
        arrayOfObject[3] = des.getLatitude();

        if (needConvert) {
            LocationUtils.LngLat originLngLat = new LocationUtils.LngLat(origin.getLongitude(), origin.getLatitude());
            LocationUtils.LngLat originResult = LocationUtils.bd_decrypt(originLngLat);
            arrayOfObject[0] = originResult.getLatitude();
            arrayOfObject[1] = originResult.getLongitude();

            LocationUtils.LngLat desLngLat = new LocationUtils.LngLat(des.getLongitude(), des.getLatitude());
            LocationUtils.LngLat desResult = LocationUtils.bd_decrypt(desLngLat);
            arrayOfObject[2] = desResult.getLatitude();
            arrayOfObject[3] = desResult.getLongitude();
        }

        String str = String.format("http://uri.amap.com/navigation?from=%f,%f&to=%f,%f&mode=car&policy=0&src=" + Source_App + "&coordinate=gaode&callnative=0", arrayOfObject);
        Logger.d("Map", str);
        Intent intent;
        try {
            Uri url = Uri.parse(str);
            intent = new Intent(Intent.ACTION_VIEW, url);
        } catch (Exception e) {
            e.printStackTrace();
            intent = null;
        }
        return intent;
    }


    private static Intent intentForAmap(MapLocation origin, MapLocation des) {
        Intent intentForAmap;
        Object[] arrayOfObject = new Object[4];

        arrayOfObject[0] = origin.getLatitude();
        arrayOfObject[1] = origin.getLongitude();
        arrayOfObject[2] = des.getLatitude();
        arrayOfObject[3] = des.getLongitude();
        if (needConvert) {
            LocationUtils.LngLat originLngLat = new LocationUtils.LngLat(origin.getLongitude(), origin.getLatitude());
            LocationUtils.LngLat originResult = LocationUtils.bd_decrypt(originLngLat);
            arrayOfObject[0] = originResult.getLatitude();
            arrayOfObject[1] = originResult.getLongitude();

            LocationUtils.LngLat desLngLat = new LocationUtils.LngLat(des.getLongitude(), des.getLatitude());
            LocationUtils.LngLat desResult = LocationUtils.bd_decrypt(desLngLat);
            arrayOfObject[2] = desResult.getLatitude();
            arrayOfObject[3] = desResult.getLongitude();
        }


        String str = String.format("androidamap://route?sourceApplication=" + Source_App + "&slat=%f&slon=%f&sname=起点&dlat=%f&dlon=%f&dname=终点&dev=0&m=0&t=0&showType=1", arrayOfObject);
        Intent intent;
        try {
            intent = Intent.parseUri(str, 0);
            intent.setPackage(Autonavi_Map);
            intentForAmap = intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            intentForAmap = null;
        }
        return intentForAmap;
    }

    private static Intent intentForBaidu(MapLocation origin, MapLocation des) {
        Intent intent = null;
        try {
            intent = new Intent();
            intent.setData(Uri.parse("baidumap://map/direction?origin=name:起点|latlng:" + origin.getLatitude() + "," + origin.getLongitude()
                    + "&destination=" + des.getLatitude() + "," + des.getLongitude() + "&mode=" + baidu_navi_type));
            intent.setPackage(Baidu_Map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    private static Intent intentForTencent(MapLocation origin, MapLocation des) {
        Intent intent = null;
        try {
            if (needConvert) {
                LocationUtils.LngLat originLngLat = new LocationUtils.LngLat(origin.getLongitude(), origin.getLatitude());
                LocationUtils.LngLat originResult = LocationUtils.bd_decrypt(originLngLat);
                origin.setLatitude(originResult.getLatitude());
                origin.setLongitude(originResult.getLongitude());

                LocationUtils.LngLat desLngLat = new LocationUtils.LngLat(des.getLongitude(), des.getLatitude());
                LocationUtils.LngLat desResult = LocationUtils.bd_decrypt(desLngLat);
                des.setLatitude(desResult.getLatitude());
                des.setLongitude(desResult.getLongitude());
            }

            intent = new Intent();
            intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&from=" + origin.getDistrictName() + "&fromcoord=" + origin.getLatitude() + "," + origin.getLongitude()
                    + "&to=" + des.getDistrictName() + "&tocoord=" + des.getLatitude() + "," + des.getLongitude() + "&referer=问安"));
            intent.setPackage(Tencent_Map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    private static Intent intentForGoogle(MapLocation origin, MapLocation des) {
        return null;
    }

    public static List<PackageInfo> getAvailableMaps(Context context) {
        return initComponentInfo(context);
    }

    public static class MapLocation {
        private double latitude;
        private double longitude;
        private String province;
        private String city;
        private String district;
        private String town;
        private String districtName;
        private String addrStr;

        public MapLocation() {
        }

        public MapLocation(double latitude, double longitude) {
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

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }


        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getDistrictName() {
            return districtName;
        }

        public void setDistrictName(String districtName) {
            this.districtName = districtName;
        }

        public String getAddrStr() {
            return addrStr;
        }

        public void setAddrStr(String addrStr) {
            this.addrStr = addrStr;
        }
    }


    /**
     * 导航
     */
    public static void doNavigate(Context context, final MapHelper.MapLocation origin, final MapHelper.MapLocation des) {
        List<PackageInfo> infos = MapHelper.getAvailableMaps(context);
        List<IconListItem> items = new ArrayList<>();
        String title;
        if (infos.size() >= 1) {
            for (PackageInfo info : infos) {
                String name = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
                Drawable icon = info.applicationInfo.loadIcon(context.getPackageManager());
                IconListItem item = new IconListItem(name, icon, info);
                items.add(item);
            }
            title = context.getString(R.string.tools_selected);

        } else {
            IconListItem item = new IconListItem(context.getResources().getString(R.string.friends_map_navigation_web), null, null);
            items.add(item);
            title = context.getString(R.string.tools_selected);
        }

        MapSelectDialog dialog = new MapSelectDialog(context, title, items, origin, des);
        dialog.show();
    }

}
