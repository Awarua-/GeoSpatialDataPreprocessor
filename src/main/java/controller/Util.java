package controller;

/**
 * Created by wooll on 28-Dec-15.
 */
public class Util {

    public static Double radiansToDegreesMultiplier = 180 / Math.PI;
    public static Double degreesToRadiansMultiplier = Math.PI / 180;

    public static Double radiansToDegrees(Double radian) {
        if (radian == null) {
            return null;
        }
        return radian * radiansToDegreesMultiplier;
    }

    public static Double degreesToRadians(Double degree) {
        if (degree == null) {
            return null;
        }
        return degree * degreesToRadiansMultiplier;
    }
}
