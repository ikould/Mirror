package com.ikould.mirror.task;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

import com.ikould.mirror.CoreApplication;
import com.peiyu.frame.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * 录制视频
 * <p>
 * Created by liudong on 2017/5/3.
 */

public class TakeVideo implements MediaRecorder.OnErrorListener {

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    /*private void initRecord(SurfaceView surfaceView) throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null) {
            mMediaRecorder.setCamera(mCamera);
            mCamera.unlock(); // maybe not for your activity flow
        }
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
        mMediaRecorder.setVideoSize(ScreenUtil.getScreenWidth(CoreApplication.getInstance()),
                ScreenUtil.getScreenHeight(CoreApplication.getInstance()));// 设置分辨率：
        mMediaRecorder.setVideoFrameRate(16);// 这个我把它去掉了，感觉没什么用
        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 512);// 设置帧频率，然后就清晰了
        // mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
        // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
        mMediaRecorder.setOutputFile(getRecordVideoFile() + File.separator + System.currentTimeMillis() + ".mp4");
        Log.d("TakeVideo", "initRecord: setOutputFile");
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 开始录制
     *
     * @param surfaceView
     */
    private void initRecord(SurfaceView surfaceView) {
        mCamera.unlock(); // maybe not for your activity flow
        //1st. Initial state
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        //2nd. Initialized state
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 音频源
        } catch (Exception e) {
            ToastUtils.show(CoreApplication.getInstance(), "请手动打开录音权限");
            return;
        }
        //3rd. config
        mMediaRecorder.setOutputFormat(mProfile.fileFormat);
        mMediaRecorder.setAudioEncoder(mProfile.audioCodec);
        mMediaRecorder.setVideoEncoder(mProfile.videoCodec);
        mMediaRecorder.setOutputFile(getRecordVideoFile() + File.separator + System.currentTimeMillis() + ".mp4");
        mMediaRecorder.setVideoSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
        mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);
        mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
        mMediaRecorder.setAudioEncodingBitRate(mProfile.audioBitRate);
        mMediaRecorder.setAudioChannels(mProfile.audioChannels);
        mMediaRecorder.setAudioSamplingRate(mProfile.audioSampleRate);
        mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     */
    public void record(final OnRecordFinishListener onRecordFinishListener, SurfaceView surfaceView, Camera camera) {
        this.mCamera = camera;
        this.mOnRecordFinishListener = onRecordFinishListener;
        initRecord(surfaceView);
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        if (mOnRecordFinishListener != null)
            mOnRecordFinishListener.onRecordFinish();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
        void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得视频文件地址
     *
     * @return
     */
    public String getRecordVideoFile() {
        String fileVideo = Environment.getExternalStorageDirectory() + File.separator + "Mirror" + File.separator + "Video";
        File file = new File(fileVideo);
        if (!file.exists()) {
            file.mkdirs();
        }
        return fileVideo;
    }
}

