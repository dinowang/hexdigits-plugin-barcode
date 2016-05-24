package com.hexdigits.cordova.barcode;

import android.Manifest;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by dino.wang on 2016/3/25.
 */
public class Reader extends CordovaPlugin {

    private static final String TAG = "HexdigitsBarcodeReader";

    private boolean _hasScanPermission = true;

    private FrameLayout _frame;

    private HexdigitsScannerView _scanner;
    private ZXingScannerView.ResultHandler _handler;

    private int _resumeAfterScan = 1000;

    public static final int REQUEST_CODE = 0x0ba7c0de;
    public static final int PERMISSION_DENIED_ERROR = 20;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                final FrameLayout layout = (FrameLayout) webView.getView().getParent();

                Context context = layout.getContext();

                _frame = new FrameLayout(context);
                FrameLayout.LayoutParams _frameLayout = new FrameLayout.LayoutParams(300, 300);
                _frameLayout.setMargins(0, 0, 0, 0);
                _frame.setLayoutParams(_frameLayout);
                _frame.setForegroundGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                _frame.setVisibility(View.INVISIBLE);
                layout.addView(_frame);

                // 因應 Android 6.0 權限系統改變，於執行期間(第一次)要求使用相機時，經由使用者同意才可以使用相機功能
                // 參考: https://github.com/dm77/barcodescanner/issues/177#event-565789808
                // 參考: https://github.com/jnuine/phonegap-plugin-barcodescanner/blob/master/src/android/BarcodeScanner.java#L115-L152
                if (Build.VERSION.SDK_INT >= 6) {
                    boolean hasScanPermission = cordova.hasPermission(CAMERA_PERMISSION);

                    if (!hasScanPermission) {
                        cordova.requestPermission(Reader.this, REQUEST_CODE, CAMERA_PERMISSION);
                    }

                    Reader.this._hasScanPermission = hasScanPermission;
                }
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(TAG, action);

        try {
            if (this._hasScanPermission == false) {
                throw new Exception("do not have android camera permission.");
            }

            if (action.equals("scannerStart")) {
                String scanType = args.getString(0);
                startScan(scanType, callbackContext);
                return true;

            } else if (action.equals("scannerPause")) {
                pauseScan(callbackContext);
                return true;

            } else if (action.equals("scannerResume")) {
                resumeScan(callbackContext);
                return true;

            } else if (action.equals("scannerStop")) {
                stopScan(callbackContext);
                return true;

            } else if (action.equals("scannerMove")) {
                JSONObject param = args.getJSONObject(0);
                move(param);
                return true;

            } else if (action.equals("scannerProfile")) {
                String mode = args.getString(0);
                profile(mode, callbackContext);
                return true;

            } else if (action.equals("setScanAreaPercent")) {
                double percent = args.getDouble(0);
                setScanAreaPercent(percent, callbackContext);
                return true;

            } else if (action.equals("getCameraDistance")) {
                getCameraDistance(callbackContext);
                return true;

            } else if (action.equals("setCameraDistance")) {
                float distance = Float.parseFloat(args.getString(0));
                setCameraDistance(distance, callbackContext);
                return true;

            } else if (action.equals("switchCameraAutofocus")) {
                boolean on = args.getBoolean(0);
                switchCameraAutofocus(on, callbackContext);
                return true;

            } else if (action.equals("switchFlashlight")) {
                boolean on = args.getBoolean(0);
                switchFlashlight(on, callbackContext);
                return true;

            } else if (action.equals("isOpen")) {
                isOpen(callbackContext);
                return true;

            }

        } catch (Exception ex) {
            callbackContext.error(ex.getMessage());
            //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, error));

        }

        return false;
    }

    private void startScan(final String scanType, final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (_scanner == null) {
                    Context context = _frame.getContext();

                    _scanner = new HexdigitsScannerView(context);
                    _scanner.setScanType(scanType);

                    FrameLayout.LayoutParams _scannerLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    _scannerLayout.setMargins(0, 0, 0, 0);
                    _scanner.setLayoutParams(_scannerLayout);
                    _frame.addView(_scanner);

                    _handler = new ZXingScannerView.ResultHandler() {
                        @Override
                        public void handleResult(Result data) {
                            String text = data.getText();
                            BarcodeFormat format = data.getBarcodeFormat();

                            PluginResult result = new PluginResult(PluginResult.Status.OK, text);
                            result.setKeepCallback(true);

                            callbackContext.sendPluginResult(result);

                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone ring = RingtoneManager.getRingtone(_frame.getContext(), notification);
                            ring.play();

                            if (_resumeAfterScan > 0) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (_scanner != null) {
                                          _scanner.resumeCameraPreview(_handler);
                                        }
                                    }
                                }, _resumeAfterScan);
                            }
                            else {
                                _scanner.resumeCameraPreview(_handler);
                            }
                        }
                    };

                    _scanner.setResultHandler(_handler);
                    _scanner.startCamera();
                }

                _frame.setVisibility(View.VISIBLE);
            }
        });
    }

    private void pauseScan(final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    _scanner.stopCamera();

                    callbackContext.success();

                } catch (Exception ex) {
                    callbackContext.error("pauseScan: " + ex.getMessage());

                }
            }
        });
    }
    private void resumeScan(final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    _scanner.startCamera();

                    callbackContext.success();

                } catch (Exception ex) {
                    callbackContext.error("resumeScan: " + ex.getMessage());

                }
            }
        });
    }

    private void stopScan(final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                if (_scanner != null) {
                    _scanner.setResultHandler(null);
                    _scanner.stopCamera();

                    _frame.removeView(_scanner);
                    _scanner = null;
                }

                _frame.setVisibility(View.INVISIBLE);

                callbackContext.success();
            }
        });
    }
    private void isOpen(final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    boolean isOpen = _scanner != null;
                    callbackContext.success(isOpen+"");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("isOpen: " + ex.getMessage());
                }
            }
        });
    }

    private void getCameraDistance(final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    callbackContext.success("-1");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("getCameraDistance: " + ex.getMessage());

                }
            }
        });
    }

    private void setCameraDistance(final float distance, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    throw new Exception("not supported.");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("setCameraDistance: " + ex.getMessage());

                }
            }
        });
    }

    private void switchCameraAutofocus(final boolean on, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    _scanner.setAutoFocus(on);

                    callbackContext.success();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("switchCameraAutofocus: " + ex.getMessage());

                }
            }
        });
    }

    private void switchFlashlight(final boolean on, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    _scanner.setFlash(on);

                    callbackContext.success();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("switchFlashlight: " + ex.getMessage());

                }
            }
        });
    }

    private void setScanAreaPercent(final double percent, final CallbackContext callbackContext) {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
//                    BarcodeDecoder decoder = _scanner.getDecoder();
//                    decoder.setScanAreaPercent(percent);
//
//                    callbackContext.success();

                    throw new Exception("not supported.");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callbackContext.error("setScanAreaPercent: " + ex.getMessage());

                }
            }
        });
    }

    private void profile(final String mode, final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                if (_scanner == null) {
                    if (callbackContext != null) {
                        callbackContext.error("Open camera first.");
                    }
                    return;
                }

                if (mode.equalsIgnoreCase("save")) {
                    _resumeAfterScan = 5000;

                }
                else if (mode.equalsIgnoreCase("packaging")) {
                    _resumeAfterScan = 5000;


                }
                else if (mode.equalsIgnoreCase("quick")) {
                    _resumeAfterScan = 1000;

                }
                else if (mode.equalsIgnoreCase("default")) {
                    _resumeAfterScan = 5000;

                }
                else {
                    if (callbackContext != null) {
                        callbackContext.error("Mode not found.");
                    }
                    return;
                }

                if (callbackContext != null) {
                    callbackContext.success();
                }
            }
        });
    }

    private void ajdustHud() {

    }


    private void move(final JSONObject position) throws JSONException {
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    int x = position.getInt("x");
                    int y = position.getInt("y");
                    int width = position.getInt("width");
                    int height = position.getInt("height");

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                    params.setMargins(x, y, 0, 0);
                    _frame.setLayoutParams(params);

                } catch (JSONException ex) {
                    ex.printStackTrace();

                }
            }
        });
    }
}
