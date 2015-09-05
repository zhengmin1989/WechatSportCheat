package com.example.xposedwechat;


import static de.robv.android.xposed.XposedHelpers.findClass;

import java.lang.reflect.Field;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.SparseArray;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class wechat implements IXposedHookLoadPackage {

	static int WechatStepCount=1;
	
    @SuppressWarnings("null")
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals("com.tencent.mm"))
            return;
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        try {
                    final Class<?> sensorEL = findClass("android.hardware.SystemSensorManager$SensorEventQueue",
                    lpparam.classLoader);
            // onSensorChanged
            XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new
                    XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws
                                Throwable {
                             XposedBridge.log(" mzheng  Hooked method: " +  param.method);
                              
                             ((float[]) param.args[1])[0]=((float[]) param.args[1])[0]+1000*WechatStepCount;
                             WechatStepCount+=1;
                             
                             XposedBridge.log("   SensorEvent: handle=" + param.args[0]);
                             XposedBridge.log("   SensorEvent: x=" + ((float[]) param.args[1])[0]);
                             XposedBridge.log("   SensorEvent: y=" + ((float[]) param.args[1])[1]);
                             XposedBridge.log("   SensorEvent: z=" + ((float[]) param.args[1])[2]);
                             XposedBridge.log("   SensorEvent: accuracy=" + param.args[2]);
                             XposedBridge.log("   SensorEvent: timestamp=" + param.args[3]);
                             XposedBridge.log("   Class: " + param.thisObject.getClass());
                             XposedBridge.log("   Enclosing Class: " + param.thisObject.getClass().getEnclosingClass());

                             Field field = param.thisObject.getClass().getEnclosingClass().getDeclaredField("sHandleToSensor");
                             field.setAccessible(true);
                             XposedBridge.log("   Field: " + field.toString());
                             int handle = (Integer) param.args[0];
                             Sensor ss = ((SparseArray<Sensor>) field.get(0)).get(handle);                            
                             XposedBridge.log("   SensorEvent: sensor=" + ss);                  
                        }
                    });
            
        } catch (Throwable t) {
            throw t;
        }
    }
}
