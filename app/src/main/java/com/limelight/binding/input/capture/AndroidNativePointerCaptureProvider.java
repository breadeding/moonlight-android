package com.limelight.binding.input.capture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;


// We extend AndroidPointerIconCaptureProvider because we want to also get the
// pointer icon hiding behavior over our stream view just in case pointer capture
// is unavailable on this system (ex: DeX, ChromeOS)
@TargetApi(Build.VERSION_CODES.O)
public class AndroidNativePointerCaptureProvider extends AndroidPointerIconCaptureProvider implements InputManager.InputDeviceListener {
    private final InputManager inputManager;
    private final View targetView;

    public AndroidNativePointerCaptureProvider(Activity activity, View targetView) {
        super(activity, targetView);
        this.inputManager = activity.getSystemService(InputManager.class);
        this.targetView = targetView;
    }

    public static boolean isCaptureProviderSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    // We only capture the pointer if we have a compatible InputDevice
    // present. This is a workaround for an Android 12 regression causing
    // incorrect mouse input when using the SPen.
    // https://github.com/moonlight-stream/moonlight-android/issues/1030
    private boolean hasCaptureCompatibleInputDevice() {
//        Log.d("debug", "len: " + InputDevice.getDeviceIds().length);
        // 平板为42，手机为6
        int threshold = 42;
        for (int id : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(id);
            if (device == null) {
                continue;
            }
            // 华为手机的id为6的设备不是外置设备但可能认为是鼠标
            if(id == 0)
                threshold = 6;
            // 华为平板的id为42的设备不是外置设备但可能认为是鼠标
            if(id <= threshold)
                continue;

//            Log.d("debug", "id：" + id);
//            Log.d("debug", "InputDevice.SOURCE_MOUSE：" + device.supportsSource(InputDevice.SOURCE_MOUSE));
//            Log.d("debug", "InputDevice.SOURCE_MOUSE_RELATIVE：" + device.supportsSource(InputDevice.SOURCE_MOUSE_RELATIVE));
//            Log.d("debug", "InputDevice.SOURCE_TOUCHPAD：" + device.supportsSource(InputDevice.SOURCE_TOUCHPAD));
//            Log.d("debug", "InputDevice.SOURCE_BLUETOOTH_STYLUS：" + device.supportsSource(InputDevice.SOURCE_BLUETOOTH_STYLUS));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_BUTTON：" + device.supportsSource(InputDevice.SOURCE_CLASS_BUTTON));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_JOYSTICK：" + device.supportsSource(InputDevice.SOURCE_CLASS_JOYSTICK));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_MASK：" + device.supportsSource(InputDevice.SOURCE_CLASS_MASK));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_NONE：" + device.supportsSource(InputDevice.SOURCE_CLASS_NONE));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_POINTER：" + device.supportsSource(InputDevice.SOURCE_CLASS_POINTER));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_POSITION：" + device.supportsSource(InputDevice.SOURCE_CLASS_POSITION));
//            Log.d("debug", "InputDevice.SOURCE_CLASS_TRACKBALL：" + device.supportsSource(InputDevice.SOURCE_CLASS_TRACKBALL));
//            Log.d("debug", "InputDevice.SOURCE_DPAD：" + device.supportsSource(InputDevice.SOURCE_DPAD));
//            Log.d("debug", "InputDevice.SOURCE_GAMEPAD：" + device.supportsSource(InputDevice.SOURCE_GAMEPAD));
//            Log.d("debug", "InputDevice.SOURCE_HDMI：" + device.supportsSource(InputDevice.SOURCE_HDMI));
//            Log.d("debug", "InputDevice.SOURCE_JOYSTICK：" + device.supportsSource(InputDevice.SOURCE_JOYSTICK));
//            Log.d("debug", "InputDevice.SOURCE_KEYBOARD：" + device.supportsSource(InputDevice.SOURCE_KEYBOARD));
//            Log.d("debug", "InputDevice.SOURCE_ROTARY_ENCODER：" + device.supportsSource(InputDevice.SOURCE_ROTARY_ENCODER));
//            Log.d("debug", "InputDevice.SOURCE_SENSOR：" + device.supportsSource(InputDevice.SOURCE_SENSOR));
//            Log.d("debug", "InputDevice.SOURCE_STYLUS：" + device.supportsSource(InputDevice.SOURCE_STYLUS));
//            Log.d("debug", "InputDevice.SOURCE_TOUCHPAD：" + device.supportsSource(InputDevice.SOURCE_TOUCHPAD));
//            Log.d("debug", "InputDevice.SOURCE_TOUCHSCREEN：" + device.supportsSource(InputDevice.SOURCE_TOUCHSCREEN));
//            Log.d("debug", "InputDevice.SOURCE_TOUCH_NAVIGATION：" + device.supportsSource(InputDevice.SOURCE_TOUCH_NAVIGATION));
//            Log.d("debug", "InputDevice.SOURCE_TRACKBALL：" + device.supportsSource(InputDevice.SOURCE_TRACKBALL));
//            Log.d("debug", "InputDevice.SOURCE_UNKNOWN：" + device.supportsSource(InputDevice.SOURCE_UNKNOWN));
            // Skip touchscreens when considering compatible capture devices.
            // Samsung devices on Android 12 will report a sec_touchpad device
            // with SOURCE_TOUCHSCREEN, SOURCE_KEYBOARD, and SOURCE_MOUSE.
            // Upon enabling pointer capture, that device will switch to
            // SOURCE_KEYBOARD and SOURCE_TOUCHPAD.
            // Only skip on non ChromeOS devices cause the ChromeOS pointer else
            // gets disabled removing relative mouse capabilities
            // on Chromebooks with touchscreens
            if (device.supportsSource(InputDevice.SOURCE_TOUCHSCREEN) && !targetView.getContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management")) {
                continue;
            }

            if (device.supportsSource(InputDevice.SOURCE_MOUSE) ||
                    device.supportsSource(InputDevice.SOURCE_MOUSE_RELATIVE) ||
                    device.supportsSource(InputDevice.SOURCE_TOUCHPAD)) {
                return true;
            }
        }

        return false;
    }

    public interface OnCaptureDeviceStatusListener {
        void onDeviceStatusChanged(boolean hasCompatibleDevice);
    }

    private OnCaptureDeviceStatusListener statusListener;

    public void setOnCaptureDeviceStatusListener(OnCaptureDeviceStatusListener listener) {
        this.statusListener = listener;
    }

    @Override
    public void enableCapture() {
        super.enableCapture();

        // Listen for device events to enable/disable capture
        inputManager.registerInputDeviceListener(this, null);

        // Capture now if we have a capture-capable device
        if (hasCaptureCompatibleInputDevice()) {
            targetView.requestPointerCapture();
            // 通知外部：添加了外部设备
            if (statusListener != null) {
//                Log.d("debug", "enableCapture: ");
                statusListener.onDeviceStatusChanged(true);
            }
        }
    }

    @Override
    public void disableCapture() {
        super.disableCapture();

        // It is important to unregister the listener *before* releasing pointer capture,
        // because releasing pointer capture can cause an onInputDeviceChanged() callback
        // for devices with a touchpad (like a DS4 controller).
        inputManager.unregisterInputDeviceListener(this);
        targetView.releasePointerCapture();
    }

    @Override
    public void onWindowFocusChanged(boolean focusActive) {
        // NB: We have to check cursor visibility here because Android pointer capture
        // doesn't support capturing the cursor while it's visible. Enabling pointer
        // capture implicitly hides the cursor.
//        if (!focusActive || !isCapturing || isCursorVisible) {
//            return;
//        }
        if (!focusActive || !isCapturing) {
            return;
        }

        // Recapture the pointer if focus was regained. On Android Q,
        // we have to delay a bit before requesting capture because otherwise
        // we'll hit the "requestPointerCapture called for a window that has no focus"
        // error and it will not actually capture the cursor.
        Handler h = new Handler();
        h.postDelayed(() -> {
            if (hasCaptureCompatibleInputDevice()) {
                targetView.requestPointerCapture();
                // 通知外部：添加了外部设备
                if (statusListener != null) {
//                    Log.d("debug", "onWindowFocusChanged: ");
                    statusListener.onDeviceStatusChanged(true);
                }
            }
        }, 500);
    }

    @Override
    public boolean eventHasRelativeMouseAxes(MotionEvent event) {
        // SOURCE_MOUSE_RELATIVE is how SOURCE_MOUSE appears when our view has pointer capture.
        // SOURCE_TOUCHPAD will have relative axes populated iff our view has pointer capture.
        // See https://developer.android.com/reference/android/view/View#requestPointerCapture()
        int eventSource = event.getSource();
        return (eventSource == InputDevice.SOURCE_MOUSE_RELATIVE && event.getToolType(0) == MotionEvent.TOOL_TYPE_MOUSE) ||
                (eventSource == InputDevice.SOURCE_TOUCHPAD && targetView.hasPointerCapture());
    }

    @Override
    public float getRelativeAxisX(MotionEvent event) {
        int axis = (event.getSource() == InputDevice.SOURCE_MOUSE_RELATIVE) ?
                MotionEvent.AXIS_X : MotionEvent.AXIS_RELATIVE_X;
        float x = event.getAxisValue(axis);
        for (int i = 0; i < event.getHistorySize(); i++) {
            x += event.getHistoricalAxisValue(axis, i);
        }
        return x;
    }

    @Override
    public float getRelativeAxisY(MotionEvent event) {
        int axis = (event.getSource() == InputDevice.SOURCE_MOUSE_RELATIVE) ?
                MotionEvent.AXIS_Y : MotionEvent.AXIS_RELATIVE_Y;
        float y = event.getAxisValue(axis);
        for (int i = 0; i < event.getHistorySize(); i++) {
            y += event.getHistoricalAxisValue(axis, i);
        }
        return y;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        // Check if we've added a capture-compatible device
        if (!targetView.hasPointerCapture() && hasCaptureCompatibleInputDevice()) {
//            Log.d("debug", "onCompatibleInputDeviceAdded: ");
            targetView.requestPointerCapture();
            // 通知外部：添加了外部设备
            if (statusListener != null) {
                statusListener.onDeviceStatusChanged(true);
            }
        }
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        // Check if the capture-compatible device was removed
        if (targetView.hasPointerCapture() && !hasCaptureCompatibleInputDevice()) {
//            Log.d("debug", "onCompatibleInputDeviceRemoved: ");
            targetView.releasePointerCapture();
            // 通知外部：移除了外部设备
            if (statusListener != null) {
                statusListener.onDeviceStatusChanged(false);
            }
        }
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        // Emulating a remove+add should be sufficient for our purposes.
        //
        // Note: This callback must be handled carefully because it can happen as a result of
        // calling requestPointerCapture(). This can cause trackpad devices to gain SOURCE_MOUSE_RELATIVE
        // and re-enter this callback.
        onInputDeviceRemoved(deviceId);
        onInputDeviceAdded(deviceId);
    }
}
