package com.lativ.cordova.barcode;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageView;
import android.widget.FrameLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import io.ionic.starter.R;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import eu.livotov.labs.android.camview.CameraLiveView;
//import eu.livotov.labs.android.camview.ScannerLiveView;
//import eu.livotov.labs.android.camview.camera.CameraController;
//import eu.livotov.labs.android.camview.camera.CameraManager;
//import eu.livotov.labs.android.camview.scanner.decoder.BarcodeDecoder;
//import eu.livotov.labs.android.camview.scanner.decoder.zxing.LZXDecoder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by dino.wang on 2016/3/25.
 */
public class Reader extends CordovaPlugin {

    private static final String TAG = "LativBarcodeReader";

    private FrameLayout _frame;

    //private ScannerLiveView _scanner;

    private ZXingScannerView _scanner;
    private ZXingScannerView.ResultHandler _handler;

//    private ImageView _aimImage;
//    private int _aimImageWidth;

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
//
//                _frame.addOnLayoutChangeListener(new OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                      _aimImageWidth = Math.min( (int)((right - left) * 0.7), (int)((bottom - top) * 0.7) );
//
//                      if (_aimImage != null) {
//                        FrameLayout.LayoutParams imageLayout = new FrameLayout.LayoutParams(_aimImageWidth, _aimImageWidth);
//                        imageLayout.setMargins(0, 0, 0, 0);
//                        imageLayout.gravity = Gravity.CENTER;
//                        _aimImage.setLayoutParams(imageLayout);
//                      }
//                    }
//                });
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(TAG, action);

        try {
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



                    _scanner = new ZXingScannerView(context);
                    FrameLayout.LayoutParams _scannerLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                    _scannerLayout.setMargins(0, 0, 0, 0);
                    _scanner.setLayoutParams(_scannerLayout);
                    _frame.addView(_scanner);

//                    if (_aimImage != null) {
//                      _frame.removeView(_aimImage);
//                    }
//
//                    _aimImage = new ImageView(context);
//                    FrameLayout.LayoutParams imageLayout = new FrameLayout.LayoutParams(_aimImageWidth, _aimImageWidth);
//                    imageLayout.gravity = Gravity.CENTER;
//                    imageLayout.setMargins(0, 0, 0, 0);
//                    _aimImage.setLayoutParams(imageLayout);
//                    _aimImage.setBackgroundResource(R.drawable.focus);
//                    _aimImage.setVisibility(View.VISIBLE);
//                    _frame.addView(_aimImage);

//                    _scanner.setHudImageResource(0);
//                    _scanner.setDecoder(new LZXDecoder(scanType));

//                    for (eu.livotov.labs.android.camview.camera.CameraInfo cameraInfo : CameraManager.getAvailableCameras(context)) {
//                        Log.d(TAG, "Camera: " + cameraInfo.getCameraId() + ", Front: " + cameraInfo.isFrontFacingCamera());
//                    }
                }

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

                        _scanner.resumeCameraPreview(this);
                    }
                };

                _scanner.setResultHandler(_handler);
                _scanner.startCamera();

//                _scanner.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
//                    @Override
//                    public void onScannerStarted(ScannerLiveView scanner) {
//                        Log.i(TAG, "onScannerStarted");
//                    }
//
//                    @Override
//                    public void onScannerStopped(ScannerLiveView scanner) {
//                        Log.i(TAG, "onScannerStopped");
//                    }
//
//                    @Override
//                    public void onScannerError(Throwable err) {
//                        Log.i(TAG, "onScannerError: " + err.getMessage());
//
//                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, err.getMessage());
//                        result.setKeepCallback(true);
//
//                        callbackContext.sendPluginResult(result);
//                    }
//
//                    @Override
//                    public void onCodeScanned(String data) {
//                        Log.i(TAG, "onCodeScanned: " + data);
//
//                        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
//                        result.setKeepCallback(true);
//
//                        callbackContext.sendPluginResult(result);
//                    }
//                });

//                _scanner.startScanner();
                _frame.setVisibility(View.VISIBLE);
            }
        });
    }

    private void pauseScan(final CallbackContext callbackContext) {

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                try {
                    //_scanner.setScannerViewEventListener(null);
                    // _scanner.getCamera().pauseDisplay();
                    //_scanner.stopScanner();
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
                    //_scanner.setScannerViewEventListener(null);
                    //_scanner.getCamera().resumeDisplay();
//                    _scanner.startScanner();
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
//
//                    _scanner.setScannerViewEventListener(null);
//                    _scanner.stopScanner();
                    _scanner.setResultHandler(null);
                    _scanner.stopCamera();

                    _frame.removeView(_scanner);
//                    _frame.removeView(_aimImage);
                    _scanner = null;
//                    _aimImage = null;
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
                  //CameraLiveView camera = _scanner.getCamera();
                  //CameraController controller = camera.getController();

                  //callbackContext.success(controller.isReady()+"");

                    boolean isOpen = _scanner != null;// && _scanner.;

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
//                    CameraLiveView camera = _scanner.getCamera();
//                    float distance = camera.getCameraDistance();
//
//                    callbackContext.success("" + distance);
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
//                    CameraLiveView camera = _scanner.getCamera();
//                    camera.setCameraDistance(distance);
//
//                    callbackContext.success("" + distance);

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
//                    CameraLiveView camera = _scanner.getCamera();
//                    CameraController controller = camera.getController();
//
//                    controller.switchAutofocus(on);

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
//                    CameraLiveView camera = _scanner.getCamera();
//                    CameraController controller = camera.getController();
//                    controller.switchFlashlight(on);

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

//                if (mode.equalsIgnoreCase("save")) {
//                    _scanner.setDecodeThrottleMillis(1000);
//                    _scanner.setSameCodeRescanProtectionTime(5000);
//
//                }
//                else if (mode.equalsIgnoreCase("packaging")) {
//                    _scanner.setDecodeThrottleMillis(50);
//                    _scanner.setSameCodeRescanProtectionTime(5000);
//
//                }
//                else if (mode.equalsIgnoreCase("quick")) {
//                    _scanner.setDecodeThrottleMillis(0);
//                    _scanner.setSameCodeRescanProtectionTime(1000);
//
//                }
//                else if (mode.equalsIgnoreCase("default")) {
//                    _scanner.setDecodeThrottleMillis(300);
//                    _scanner.setSameCodeRescanProtectionTime(5000);
//
//                }
//                else {
//                    if (callbackContext != null) {
//                        callbackContext.error("Mode not found.");
//                    }
//                    return;
//                }

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
