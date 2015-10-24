package MainLogic;

/**
 * Created by NB on 24.10.2015.
 */
public class SensorObject {

    private int type; // can be 0, 1,2
    private float x, y, z;
    private float time;

    public SensorObject(int _type, float _x, float _y, float _z, float _time) {
        type = _type;
        x = _x;
        y = _y;
        z = _z;
        time = _time;
    }

    public float getTime() {
        return time;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public int getType() {
        return type;
    }

}
