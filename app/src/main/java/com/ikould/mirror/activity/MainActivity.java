package com.ikould.mirror.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.ikould.mirror.CoreApplication;
import com.ikould.mirror.R;
import com.ikould.mirror.task.TakePicture;
import com.ikould.mirror.task.TakeVideo;
import com.ikould.mirror.utils.MirrorUtil;
import com.ikould.mirror.utils.PermissionUtil;
import com.peiyu.frame.activity.FrameBaseActivity;
import com.peiyu.frame.utils.ToastUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FrameBaseActivity {

    //权限回调code
    private final static int CAMERA_OK = 110;
    private int cameraIndex;
    private boolean isFontCamera;
    private Camera camera;
    // 视频是否开始录制
    private boolean isRecordStart;
    private TakeVideo takeVideo;

    @BindView(R.id.surfaceview)
    SurfaceView surfaceView;
    @BindView(R.id.iv_camera_trans)
    ImageView mCameraTrans;
    @BindView(R.id.iv_camera)
    ImageView mCamera;
    @BindView(R.id.iv_record)
    CheckBox mRecord;
    @BindView(R.id.iv_camera_more)
    ImageView mMore;

    @Override
    protected boolean setToolbarAsActionbar() {
        return false;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        initConfig();
        setMyContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        Window window = getWindow();//得到窗口
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置高亮
    }

    /**
     * 初始化控件
     */
    private void initView() {
        surfaceView.setRotation(180);
        //没有前置摄像头隐藏转换按钮
        if (MirrorUtil.findFrontCamera() == -1) {
            mCameraTrans.setVisibility(View.GONE);
        }
        getCameraPermission();
        getRecordPermission();
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mCameraTrans.setOnClickListener(v -> {
            if (isFontCamera) {
                cameraIndex = MirrorUtil.findBackCamera();
            } else {
                cameraIndex = MirrorUtil.findFrontCamera();
            }
            isFontCamera = !isFontCamera;
            surfaceView.setVisibility(View.GONE);
            surfaceView.setVisibility(View.VISIBLE);
        });
        mCamera.setOnClickListener(v -> {
            if (camera != null)
                camera.takePicture(null, null, new TakePicture.TakePictureCallback()); //将拍到的照片给第三个对象中，这里的TakePictureCallback()是自己定义的，在下面的代码中
        });
        mRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("MainActivity", "initListener: isChecked = " + isChecked);
            if (takeVideo == null)
                takeVideo = new TakeVideo();
            if (isChecked && !isRecordStart) {
                // 开始录制
                takeVideo.record(() -> {
                    // no do
                }, surfaceView, camera);
                isRecordStart = true;
            } else if (!isChecked && isRecordStart) {
                // 停止录制
                takeVideo.stop();
                if (takeVideo != null)
                    ToastUtils.show(MainActivity.this, "录制文件 " + takeVideo.getRecordVideoFile());
                isRecordStart = false;
            }
        });
        mMore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoreActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 获取摄像头权限
     */
    private void getCameraPermission() {
        if (PermissionUtil.checkOrRequestPermission(MainActivity.this, android.Manifest.permission.CAMERA, CAMERA_OK)) {
            initCamera();
        }
    }

    /**
     * 请求录音权限
     */
    private void getRecordPermission() {
        PermissionUtil.checkOrRequestPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO, CAMERA_OK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("MainActivity", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    initCamera();
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(MainActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        //默认打开前置摄像头
        cameraIndex = MirrorUtil.findBackCamera();
        Log.d("MainActivity", "initCamera: ");
        surfaceView.getHolder().setFixedSize(1920, 1080);
        //surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //为surfaceView加入回调方法(callBack)
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }

    /**
     * 自动对焦
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (!success) {
                CoreApplication.getInstance().handler.postDelayed(runnable, 500);
            }
        }
    };

    /**
     * 对焦
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            camera.autoFocus(autoFocusCallback);
        }
    };

    /**
     * SurfaceView的绘制回调
     */
    private class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //获取camera对象
            camera = Camera.open(cameraIndex);
            try {
                //设置预览监听
                camera.setPreviewDisplay(holder);
                Camera.Parameters parameters = camera.getParameters();
                if (MainActivity.this.getResources().getConfiguration().orientation
                        != Configuration.ORIENTATION_LANDSCAPE) {
                    parameters.set("orientation", "portrait");
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                } else {
                    parameters.set("orientation", "landscape");
                    camera.setDisplayOrientation(0);
                    parameters.setRotation(0);
                }
                camera.setParameters(parameters);
                //启动摄像头预览
                camera.startPreview();
                camera.autoFocus(autoFocusCallback);
            } catch (IOException e) {
                e.printStackTrace();
                camera.release();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                //停止录制
                if (isRecordStart)
                    mRecord.setChecked(false);
                camera.stopPreview();
                camera.release();
            }
        }
    }
}
