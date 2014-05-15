package k12.revere.frc.dashboard.util;

/**
 *
 * @author Vince
 */
public class MathUtil {
    
    public static double clamp(double min, double max, double val) {
        return Math.min(max, Math.max(min, val));
    }
    
    public static float clamp(float min, float max, float val) {
        return Math.min(max, Math.max(min, val));
    }

    public static int clamp(int min, int max, int val) {
        return Math.min(max, Math.max(min, val));
    }
    
}