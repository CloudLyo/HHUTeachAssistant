package com.cloudlyo.Utiles;

/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系; BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。 chenhua
 */
public class PositionUtil {
    // a = 6369000.0, 1/f = 298.3
// f = 0.0033523298692591
// 1 - f = 0.9966476701307409
// b = a * (1 - f)
// b = 6347649.011062689
// ee = (a^2 - b^2) / a^2;
// a^2 = 40564161000000
// b^2 = 40292647967645.13
    public static double PI = 3.1415926535897932384626;
    public static double R = 6369000.0;// 地球半径
    public static double EE = 0.006693421622966021;
    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lng
     * @param lng
     * @return Gps
     */
    public static Gps gps84ToGcj02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return null;
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((R * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (R / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        return new Gps(mgLon, mgLat);
    }
    /**
     * * 火星坐标系 (GCJ-02) to 84 * *
     *
     * @param lng
     * @param lat
     * @return Gps
     */
    public static Gps gcj02ToGps84(double lng, double lat) {
        Gps gps = transform(lng, lat);
        double lontitude = lng * 2 - gps.getLng();
        double latitude = lat * 2 - gps.getLat();
        return new Gps(lontitude, latitude);
    }
    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param lng
     * @param lat
     * @return Gps
     */
    public static Gps gcj02ToBd09(double lng, double lat) {
        double x = lng, y = lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Gps(bd_lon, bd_lat);
    }
    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * *
     *
     * @param bd_lat
     * @param bd_lon
     * @return Gps
     */
    public static Gps bd09ToGcj02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Gps(gg_lon, gg_lat);
    }
    /**
     * (BD-09)-->84
     *
     * @param lng
     * @param lat
     * @return Gps
     */
    public static Gps bd09ToGps84(double lng, double lat) {
        Gps gcj02 = bd09ToGcj02(lng, lat);
        Gps map84 = gcj02ToGps84(gcj02.getLng(), gcj02.getLat());
        return map84;
    }
    /**
     * 是否在中国境内
     *
     * @param lng
     * @param lat
     * @return
     */
    public static boolean outOfChina(double lng, double lat) {
        if (lng < 72.004 || lng > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
    public static Gps transform(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new Gps(lng, lat);
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((R * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (R / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        return new Gps(mgLon, mgLat);
    }


    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }


    public static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }


    /**
     * 计算两个经纬(WGS84)度直接的距离
     *
     */
    public static double distance_wgs84(Gps gps1,Gps gps2) {
        double lng1=gps1.getLng(),lat1=gps1.getLat(),lng2=gps2.getLng(),lat2=gps2.getLat();
        double distance = 0;
        try {
            distance = Math.round(R * 2
                    * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 * PI / 180 - lat2 * PI / 180) / 2), 2)
                    + Math.cos(lat1 * PI / 180) * Math.cos(lat2 * PI / 180)
                    * Math.pow(Math.sin((lng1 * PI / 180 - lng2 * PI / 180) / 2), 2))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distance;
    }

    /**
     * 计算两个GCJ-02经纬的距离
     */
    public  static double distance_gcj02(double lng1, double lat1, double lng2, double lat2){
        Gps gps1 = gcj02ToGps84(lng1,lat1);
        Gps gps2 = gcj02ToGps84(lng2,lat2);
        return distance_wgs84(gps1,gps2);
    }

}