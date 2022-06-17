package com.motorola.settings.deviceinfo.hardwareinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Locale;

public class HardwareCameraPreferenceController extends BasePreferenceController {
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String LANG_CODE_CHINESE = "zh";
    private static final String PATTERN_DECIMAL_TENTHS = "#.#";
    private static final String PATTERN_INTEGER = "#";
    private static final String QCFA_DIMENSION = "org.codeaurora.qcamera3.quadra_cfa.qcfa_dimension";
    private static final String QCFA_SENSOR = "org.codeaurora.qcamera3.quadra_cfa.is_qcfa_sensor";
    private static final String QCFA_V2_DIMENSION = "com.lenovo.moto.quadra_cfa.qcfa_dimension";
    private static final String QCFA_V2_SENSOR = "com.lenovo.moto.quadra_cfa.is_qcfa_sensor";
    private static final String SENSOR_INFO_QUAD_PIXEL_NAME = "com.lenovo.moto.sensor.info.quad_pixel";
    private static final String TAG = "HardwareCamera";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 0;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public HardwareCameraPreferenceController(Context context, String str) {
        super(context, str);
    }

    public String getSummary() {
        String str;
        CameraManager cameraManager = (CameraManager) this.mContext.getSystemService("camera");
        try {
            String[] cameraIdList = cameraManager.getCameraIdList();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (String cameraCharacteristics : cameraIdList) {
                CameraCharacteristics cameraCharacteristics2 = cameraManager.getCameraCharacteristics(cameraCharacteristics);
                int intValue = ((Integer) cameraCharacteristics2.get(CameraCharacteristics.LENS_FACING)).intValue();
                String cameraResolution = getCameraResolution(intValue, cameraCharacteristics2);
                if (!TextUtils.isEmpty(cameraResolution)) {
                    if (DEBUG) {
                        Log.d(TAG, "Got resolution " + cameraResolution + " for lensFacing : " + intValue);
                    }
                    if (intValue == 0) {
                        arrayList.add(cameraResolution);
                    } else if (intValue == 1) {
                        arrayList2.add(cameraResolution);
                    }
                } else {
                    Log.e(TAG, "Error getting resolution for lensFacing : " + intValue);
                }
            }
            String str2 = null;
            if (!arrayList.isEmpty()) {
                str = this.mContext.getString(C1992R$string.settings_front_camera_format, new Object[]{arrayList.get(0)});
                if (arrayList.size() > 1) {
                    for (int i = 1; i < arrayList.size(); i++) {
                        str = this.mContext.getString(C1992R$string.concatenate_with_plus, new Object[]{str, arrayList.get(i)});
                    }
                }
            } else {
                str = null;
            }
            if (!arrayList2.isEmpty()) {
                str2 = this.mContext.getString(C1992R$string.settings_rear_camera_format, new Object[]{arrayList2.get(0)});
                if (arrayList2.size() > 1) {
                    for (int i2 = 1; i2 < arrayList2.size(); i2++) {
                        str2 = this.mContext.getString(C1992R$string.concatenate_with_plus, new Object[]{str2, arrayList2.get(i2)});
                    }
                }
            }
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
                return this.mContext.getString(C1992R$string.concatenate_camera_res, new Object[]{str, str2});
            } else if (!TextUtils.isEmpty(str)) {
                return str;
            } else {
                if (!TextUtils.isEmpty(str2)) {
                    return str2;
                }
                return "";
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    private String getCameraResolution(int i, CameraCharacteristics cameraCharacteristics) {
        StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        boolean isOutputSupportedFor = streamConfigurationMap.isOutputSupportedFor(256);
        Size maxSize = isOutputSupportedFor ? getMaxSize(streamConfigurationMap) : null;
        if (isQcfaSupported(cameraCharacteristics)) {
            return getQcfaSize(cameraCharacteristics);
        }
        if (isQcfaV2Supported(cameraCharacteristics)) {
            return getQcfaV2Size(cameraCharacteristics);
        }
        if (isQuadPixelSensor(cameraCharacteristics)) {
            return getQuadPixelDescription(maxSize, cameraCharacteristics);
        }
        if (isOutputSupportedFor) {
            return retrieveSensorResolutionInteger(maxSize != null ? (float) (maxSize.getWidth() * maxSize.getHeight()) : 0.0f);
        }
        Log.e(TAG, "Output not supported for ImageFormat.JPEG");
        return null;
    }

    public String getQuadPixelDescription(Size size, CameraCharacteristics cameraCharacteristics) {
        if (size == null) {
            return "";
        }
        float width = (float) (size.getWidth() * size.getHeight());
        String retrieveSensorResolutionInteger = retrieveSensorResolutionInteger(4.0f * width);
        String retrieveSensorResolutionInteger2 = retrieveSensorResolutionInteger(width);
        String pixelSize = getPixelSize(size, cameraCharacteristics);
        return this.mContext.getString(C1992R$string.settings_quad_pixel_format, new Object[]{retrieveSensorResolutionInteger, pixelSize, retrieveSensorResolutionInteger2});
    }

    public boolean isQuadPixelSensor(CameraCharacteristics cameraCharacteristics) {
        Byte b = (Byte) getCharacteristc(cameraCharacteristics, SENSOR_INFO_QUAD_PIXEL_NAME, Byte.class);
        if (b == null || b.byteValue() != 1) {
            return false;
        }
        return true;
    }

    public String getQcfaV2Size(CameraCharacteristics cameraCharacteristics) {
        int[] iArr = (int[]) getCharacteristc(cameraCharacteristics, QCFA_V2_DIMENSION, int[].class);
        if (iArr != null) {
            return retrieveSensorResolutionInteger((float) (iArr[0] * iArr[1]));
        }
        return null;
    }

    public boolean isQcfaV2Supported(CameraCharacteristics cameraCharacteristics) {
        Integer num = (Integer) getCharacteristc(cameraCharacteristics, QCFA_V2_SENSOR, Integer.class);
        return num != null && num.intValue() > 0;
    }

    public String getQcfaSize(CameraCharacteristics cameraCharacteristics) {
        int[] iArr = (int[]) getCharacteristc(cameraCharacteristics, QCFA_DIMENSION, int[].class);
        if (iArr != null) {
            return retrieveSensorResolutionInteger((float) (iArr[0] * iArr[1]));
        }
        return null;
    }

    public boolean isQcfaSupported(CameraCharacteristics cameraCharacteristics) {
        Integer num = (Integer) getCharacteristc(cameraCharacteristics, QCFA_SENSOR, Integer.class);
        return num != null && num.intValue() > 0;
    }

    public Size getMaxSize(StreamConfigurationMap streamConfigurationMap) {
        Size[] highResolutionOutputSizes = streamConfigurationMap.getHighResolutionOutputSizes(256);
        Size size = null;
        float f = 0.0f;
        if (highResolutionOutputSizes != null && highResolutionOutputSizes.length > 0) {
            for (Size size2 : highResolutionOutputSizes) {
                float width = (float) (size2.getWidth() * size2.getHeight());
                if (width > f) {
                    size = size2;
                    f = width;
                }
            }
        }
        for (Size size3 : streamConfigurationMap.getOutputSizes(256)) {
            float width2 = (float) (size3.getWidth() * size3.getHeight());
            if (width2 > f) {
                size = size3;
                f = width2;
            }
        }
        return size;
    }

    public String retrieveSensorResolutionInteger(float f) {
        DecimalFormat decimalFormat = new DecimalFormat(PATTERN_DECIMAL_TENTHS);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        Number parse = decimalFormat.parse(decimalFormat.format((double) (f / 1000000.0f)), new ParsePosition(0));
        decimalFormat.applyLocalizedPattern(PATTERN_INTEGER);
        decimalFormat.setRoundingMode(RoundingMode.HALF_DOWN);
        String format = decimalFormat.format(parse);
        if (DEBUG) {
            Log.d(TAG, "resolution : " + format + " MP");
        }
        if (LANG_CODE_CHINESE.equals(Locale.getDefault().getLanguage())) {
            try {
                format = new DecimalFormat(PATTERN_INTEGER).format((double) (Float.valueOf(format).floatValue() * 100.0f));
            } catch (NumberFormatException unused) {
            }
        }
        if (DEBUG) {
            Log.d(TAG, "Final computed resolution : " + format + " MP");
        }
        return this.mContext.getString(C1992R$string.settings_megapixel_format, new Object[]{format});
    }

    private String getPixelSize(Size size, CameraCharacteristics cameraCharacteristics) {
        float height = (((SizeF) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)).getHeight() * 1000.0f) / ((float) size.getHeight());
        DecimalFormat decimalFormat = new DecimalFormat(PATTERN_DECIMAL_TENTHS);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return decimalFormat.format(decimalFormat.parse(decimalFormat.format((double) height), new ParsePosition(0)));
    }

    private <T> T getCharacteristc(CameraCharacteristics cameraCharacteristics, String str, Class<T> cls) {
        try {
            return cameraCharacteristics.get(CameraCharacteristics.Key.class.getDeclaredConstructor(new Class[]{String.class, Class.class}).newInstance(new Object[]{str, cls}));
        } catch (Exception unused) {
            return null;
        }
    }
}
