package mr.lmd.personal.sensorapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * 提供Module算盘重置功能消息接口
 * Created by LinMingDao on 2015/12/1.
 */
public class OrientationSensorListener implements SensorEventListener {

    private boolean mIsFirstInRange = false;//true:表示第一次进入重置范围
    private static final int UPPER_LIMIT = 90;

    private int mFloorLevel = 45;

    private final static String ROTATION_TYPE_POSITIVE = "POSITIVE";//顺时针方向旋转
    private final static String ROTATION_TYPE_NEGATIVE = "NEGATIVE";//逆时针方向旋转

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {//方向传感器

            int rotationAngle = (int) event.values[1];
            if (rotationAngle > -12 && rotationAngle < 12) {//pad为水平位置，重置mIsFirstInRange
                mIsFirstInRange = true;
            }

            if ((rotationAngle < -mFloorLevel && rotationAngle > -UPPER_LIMIT) || (rotationAngle < UPPER_LIMIT && rotationAngle > mFloorLevel)) {
                if (mIsFirstInRange) {//第一次进入该范围，需要发送消息
                    //Log.d("ELSeed", "is First in Range ? " + mIsFirstInRange);
                    String rotation_type;
                    if (rotationAngle < 0) {
                        rotation_type = ROTATION_TYPE_NEGATIVE;
                        //Log.d("ELSeed", "反向 角度 = " + rotationAngle);
                    } else {
                        rotation_type = ROTATION_TYPE_POSITIVE;
                        //Log.d("ELSeed", "正向 角度 = " + rotationAngle);
                    }
                    getMessage(rotation_type);
                    //Log.d("ELSeed", "return = " + type);
                    mIsFirstInRange = false;
                }
            }
        } else {
            Log.d("ELSeed", "Sensor.TYPE is not TYPE_ACCELEROMETER");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 发送重置算盘状态消息
     *
     * @param r_type 旋转的类型
     * @return 返回旋转的类型
     */
    public String getMessage(String r_type) {
        //Log.d("ELSeed", "getMessage()! " + r_type);
        return r_type;
    }

    /**
     * 设置灵敏度
     *
     * @param floorLevel 范围:[15,60],越小越灵敏
     */
    public void setFloorLevel(int floorLevel) {
        this.mFloorLevel = floorLevel;
    }
}
