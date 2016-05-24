package com.hexdigits.cordova.barcode;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;

import me.dm7.barcodescanner.core.CameraPreview;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by dino.wang on 2016/4/14.
 */
public class HexdigitsScannerView extends ZXingScannerView {

    public final String TAG = "HexdigitsScannerView";

    private RelativeLayout _relative;
    private CameraPreview _cameraPreview;
    private Camera _camera;


    public HexdigitsScannerView(Context context) {
        super(context);
        this.init();
    }

    public HexdigitsScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.init();
    }

    public void init() {
        this.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // 調整視窗成全景
                if (_cameraPreview != null && _camera != null) {
                    int width = LativScannerView.this.getWidth();
                    int height = LativScannerView.this.getHeight();

                    Camera.Parameters cameraParam = _camera.getParameters();
                    Camera.Size previewSize = cameraParam.getPreviewSize();

                    int previewWidth = previewSize.height;
                    int previewHeight = previewSize.width;
//                    int previewWidth = previewSize.width;
//                    int previewHeight = previewSize.height;

                    double ratio = Math.max((double)width / (double)previewWidth, (double)height / (double)previewHeight);

                    int l = (int)((width - (previewWidth * ratio)) / 2);
                    int t = (int)((height - (previewHeight * ratio)) / 2);
                    int r = (int)(previewWidth * ratio);
                    int b = (int)(previewHeight * ratio);

//                    Log.d(TAG, "*** " + width + ", " + height + "; " +
//                                        previewWidth + ", " + previewHeight + "; " +
//                                        ratio + "; " +
//                                        l + ", " + t + ", " + (l + width) + ", " + (t + height));

                    _cameraPreview.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                    _cameraPreview.layout(l, t, r, b);
                }
            }
        });
    }

    @Override
    public void setupCameraPreview(Camera camera) {
        super.setupCameraPreview(camera);

        _camera = camera;
    }

    @Override
    protected IViewFinder createViewFinderView(Context context) {
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);

            if (view instanceof RelativeLayout) {
                RelativeLayout layout = (RelativeLayout) view;

                if (layout.getChildCount() == 1) {
                    CameraPreview preview = (CameraPreview) layout.getChildAt(0);
                    _relative = layout;

                    _cameraPreview = preview;

                    _relative.removeAllViews();
                    this.addView(_cameraPreview);


                    int width = LativScannerView.this.getWidth();
                    int height = LativScannerView.this.getHeight();

                    _cameraPreview.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                    _cameraPreview.layout(0, 0, width, height);
                    break;
                }
            }
        }

        return new LargeViewFinderView(context);
    }

    public void setScanType(String scanType) {
        ALL_FORMATS.clear();

        try {
            for (String format : scanType.split(",")) {
                if (format.equals("qrcode")) {
                    ALL_FORMATS.add(BarcodeFormat.QR_CODE);
                }
                else if (format.equals("code39")) {
                    ALL_FORMATS.add(BarcodeFormat.CODE_39);
                }
            }
            if (ALL_FORMATS.size() == 0) {
                throw new Exception();
            }
        }
        catch (Exception ex) {
            ALL_FORMATS.clear();
            ALL_FORMATS.add(BarcodeFormat.UPC_A);
            ALL_FORMATS.add(BarcodeFormat.UPC_E);
            ALL_FORMATS.add(BarcodeFormat.EAN_13);
            ALL_FORMATS.add(BarcodeFormat.EAN_8);
            ALL_FORMATS.add(BarcodeFormat.RSS_14);
            ALL_FORMATS.add(BarcodeFormat.CODE_39);
            ALL_FORMATS.add(BarcodeFormat.CODE_93);
            ALL_FORMATS.add(BarcodeFormat.CODE_128);
            ALL_FORMATS.add(BarcodeFormat.ITF);
            ALL_FORMATS.add(BarcodeFormat.CODABAR);
            ALL_FORMATS.add(BarcodeFormat.QR_CODE);
            ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
            ALL_FORMATS.add(BarcodeFormat.PDF_417);
        }
    }
}
