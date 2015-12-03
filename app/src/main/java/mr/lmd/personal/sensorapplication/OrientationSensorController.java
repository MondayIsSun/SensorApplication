package mr.lmd.personal.sensorapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * 提供Module算盘重置功能消息接口
 * Created by LinMingDao on 2015/12/1.
 */
public class OrientationSensorController implements SensorEventListener {

    private Context mContext;

    private boolean mIsFirstInRange = false;//true:表示第一次进入重置范围
    private static final int UPPER_LIMIT = 90;

    private int mFloorLevel = 45;

    private final static String ROTATION_TYPE_POSITIVE = "POSITIVE";//顺时针方向旋转
    private final static String ROTATION_TYPE_NEGATIVE = "NEGATIVE";//逆时针方向旋转

    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;

    private static OrientationSensorController mInstance;

    private OrientationSensorController(Context context) {
        this.mContext = context;
    }

    public static OrientationSensorController getInstance(Context context) {
        if (null == mInstance) {
            synchronized (OrientationSensorController.class) {
                if (null == mInstance) {
                    mInstance = new OrientationSensorController(context);
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {//方向传感器
            int rotationAngle = (int) event.values[1];
            if (rotationAngle > -12 && rotationAngle < 12) {//pad为水平位置，重置mIsFirstInRange
                mIsFirstInRange = true;
            }
            if ((rotationAngle < -mFloorLevel && rotationAngle > -UPPER_LIMIT) || (rotationAngle < UPPER_LIMIT && rotationAngle > mFloorLevel)) {
                if (mIsFirstInRange) {//第一次进入该范围，需要发送消息
                    String rotation_type;
                    if (rotationAngle < 0) {
                        rotation_type = ROTATION_TYPE_NEGATIVE;
                    } else {
                        rotation_type = ROTATION_TYPE_POSITIVE;
                    }
                    //内部push给h5 , rotation_type : POSITIVE , NEGATIVE
                    sendMessage(rotation_type);
                    String eventData = "{\"rotation_type\":\"" + rotation_type + "\"}";
                    Log.d("ELSeed", "eventData = " + eventData);
                    //NativeH5Utils.nativeSendMessageToH5(SensorConstants.ORIENTATION_SENSOR_EVENT_NAME, eventData);
                    mIsFirstInRange = false;
                }
            }
        } else {
            Log.d("ELSeed", "Sensor.TYPE is not TYPE_ACCELEROMETER");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void sendMessage(String rotation_type) {
        Log.d("ELSeed", "sendMessage()! " + rotation_type);
    }

    /**
     * 设置灵敏度
     *
     * @param floorLevel 范围:[15,60],越小越灵敏
     */
    public void setFloorLevel(int floorLevel) {
        if (floorLevel >= 15 && floorLevel <= 60) {
            this.mFloorLevel = floorLevel;
        } else {
            Log.d("ELSeed", "floorLevel is out of range ! ");
        }
    }

    /**
     * 激活方向传感器
     */
    public void openOrientationSensor(int floorLevel) {
        Log.d("ELSeed", "openOrientationSensor! ");
        if (-1 != floorLevel) {
            if (floorLevel >= 15 && floorLevel <= 60) {
                this.mFloorLevel = floorLevel;
            } else {
                Log.d("ELSeed", "floorLevel is out of range ! ");
            }
        } else {
            Log.d("ELSeed", "floorLevel is null ! ");
        }
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mInstance, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 注销方向传感器
     */
    public void closeOrientationSensor() {
        if (null != mSensorManager) {
            if (null != mInstance) {
                Log.d("ELSeed", "closeOrientationSensor! ");
                mSensorManager.unregisterListener(mInstance);
                mInstance = null;
            }
        }
    }
}
