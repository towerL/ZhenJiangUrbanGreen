package com.nju.urbangreen.zhenjiangurbangreen.util;

import android.text.TextUtils;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lxs on 17-8-6.
 */

public class GeoJsonUtil {

    public static String GeoPoint2WKTString(double longitude, double latitude) {
        Point p = WGSTOZhenjiang.WGS2ZJ(latitude, longitude);
        return "POINT (" + p.getX() + " " + p.getY() + ")";
    }

    public static String ZJPoint2WKTString(double x, double y) {
        return "POINT (" + x + " " + y + ")";
    }

    public static String Geometry2String(Geometry geometry) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String, Object> resMap = new HashMap<>();
        Geometry.Type geoType= geometry.getType();
        if (geoType == Geometry.Type.POINT) {
            resMap.put("type", "Point");
            Point point=(Point) geometry;
            resMap.put("coordinates", pointToCoordinate(point));
        }
        return gson.toJson(resMap);
    }
    public static Geometry String2Geometry(String str) {
        if(TextUtils.isEmpty(str)) {
            return null;
        }
        SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR);
        Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String,Object> geometryMap=gson.fromJson(str, new TypeToken<Map<String, Object>>(){}.getType());
        String gcType=(String)geometryMap.get("type");
        if (gcType.equals("Point")) {
            //单点
            ArrayList<Double> coordinate=(ArrayList<Double>) geometryMap.get("coordinates");
            Point point=pointFromCoordinate(coordinate);
            return point;
        }
        else if (gcType.equals("LineString")) {
            //单线
            ArrayList<ArrayList<Double>> coordinates=(ArrayList<ArrayList<Double>>) geometryMap.get("coordinates");
            Polyline polyline=pathFromCoordinates(coordinates);
            return  polyline;
        }
        else if (gcType.equals("Polygon")) {
            //单个多边形
            ArrayList<ArrayList<ArrayList<Double>>> coordinatess=(ArrayList<ArrayList<ArrayList<Double>>>) geometryMap.get("coordinates");
            Polygon polygon=polygonFromCoordinatess(coordinatess);
            return polygon;
        }
        else if (gcType.equals("MultiPoint")) {
            ArrayList<ArrayList<Double>> coordinates=(ArrayList<ArrayList<Double>>) geometryMap.get("coordinates");
            MultiPoint multiPoint=new MultiPoint();
            for (ArrayList<Double> coordinate : coordinates) {
                multiPoint.add(pointFromCoordinate(coordinate));
            }
            return multiPoint;
        }
        else if (gcType.equals("MultiLineString")) {
            ArrayList<ArrayList<ArrayList<Double>>> coordinatess=(ArrayList<ArrayList<ArrayList<Double>>>) geometryMap.get("coordinates");
            Polyline polyline=new Polyline();
            for (ArrayList<ArrayList<Double>> coordinates : coordinatess) {
                polyline.add(pathFromCoordinates(coordinates), false);
            }
            return polyline;
        }
        else if (gcType.equals("MultiPolygon")) {
            ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> coordinatesss= (ArrayList<ArrayList<ArrayList<ArrayList<Double>>>>) geometryMap.get("coordinates");
            Polygon polygons=new Polygon();
            for (ArrayList<ArrayList<ArrayList<Double>>> coordinatess : coordinatesss) {
                polygons.add(polygonFromCoordinatess(coordinatess), false);
            }
            return polygons;
        }

        return null;
    }
    private static Point pointFromCoordinate(ArrayList<Double> coordinate)
    {
        return new Point(coordinate.get(0).intValue(), coordinate.get(1).intValue());
    }
    private static ArrayList<Double> pointToCoordinate(Point point)
    {
        ArrayList<Double> coordinate= new ArrayList<>(2);
        coordinate.add(point.getX());
        coordinate.add(point.getY());
        return coordinate;
    }
    private static Polyline pathFromCoordinates(ArrayList<ArrayList<Double>> coordinates)
    {
        Polyline polyline=new Polyline();
        Line startLine= new Line();
        startLine.setStart(pointFromCoordinate(coordinates.get(0)));
        startLine.setEnd(pointFromCoordinate(coordinates.get(1)));
        polyline.addSegment(startLine, false);
        for (int i = 2; i < coordinates.size(); i++) {
            polyline.lineTo(pointFromCoordinate(coordinates.get(i)));
        }
        return polyline;

    }
    private static Polygon polygonFromCoordinatess(ArrayList<ArrayList<ArrayList<Double>>> coordinatess)
    {
        Polygon polygon=new Polygon();
        Point startP = null, endP = null;
        for (int i = 0; i < coordinatess.size(); i++) {
            ArrayList<ArrayList<Double>> coordinates = coordinatess.get(i);
            //多边形顺时针逆时针控制
            if ((i==0)!=ringIsCloskWise(coordinates)){
                Collections.reverse(coordinates);
            }
            for(int j = 0, n = coordinates.size() - 1; j < n; j++) {
                startP = pointFromCoordinate(coordinates.get(j));
                endP = pointFromCoordinate(coordinates.get(j + 1));
                Line line = new Line();
                line.setStart(startP);
                line.setEnd(endP);
                polygon.addSegment(line, false);
            }
        }
        return polygon;
    }
    private static boolean ringIsCloskWise(ArrayList<ArrayList<Double>> coordinates)
    {
        int total = 0,i = 0,rLength = coordinates.size();
        ArrayList<Double> pt1 = coordinates.get(i),pt2;
        for (i=0; i < rLength - 1; i++) {
            pt2 = coordinates.get(i+1);
            total += (pt2.get(0) - pt1.get(0)) * (pt2.get(1) + pt1.get(1));
            pt1 = pt2;
        }
        return total >= 0;
    }
    public static Envelope getEnvelop(ArrayList<Geometry> geometries)
    {
        Envelope envelope=new Envelope();
        for (Iterator<Geometry> iterator = geometries.iterator(); iterator.hasNext();) {
            Envelope tmp=new Envelope();
            Geometry geometry = (Geometry) iterator.next();
            geometry.queryEnvelope(tmp);
            envelope.merge(tmp);
        }
        return envelope;
    }
}
