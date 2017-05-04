package com.peiyu.frame.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/**
 * Description
 * Created by chenqiao on 2016/5/14.
 */
public class VolumeUtils {

    private AudioManager manager;

    public static final int SYSTEM = AudioManager.STREAM_SYSTEM;
    public static final int ALARM = AudioManager.STREAM_ALARM;
    public static final int MUSIC = AudioManager.STREAM_MUSIC;
    public static final int NOTIFICATION = AudioManager.STREAM_NOTIFICATION;
    public static final int RING = AudioManager.STREAM_RING;
    public static final int VOICE_CALL = AudioManager.STREAM_VOICE_CALL;

    public VolumeUtils(Context context) {
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取当前音量
     *
     * @param type 音量类型
     * @return 当前音量
     */
    public int getVolume(int type) {
        return manager.getStreamVolume(type);
    }

    /**
     * 获取指定类型最大的音量
     *
     * @param type 音量类型
     * @return 最大音量
     */
    public int getMaxVolume(int type) {
        return manager.getStreamMaxVolume(type);
    }

    /**
     * 增加指定类型的音量
     *
     * @param type 音量类型
     */
    public void addVolume(int type) {
        int current = getVolume(type);
        int max = getMaxVolume(type);
        if (current + 1 <= max) {
            setVolume(type, current + 1);
        }
    }

    /**
     * 减少指定类型的音量
     *
     * @param type 音量类型
     */
    public void decVolume(int type) {
        int current = getVolume(type);
        if (current - 1 >= 0) {
            setVolume(type, current - 1);
        }
    }

    /**
     * 设置音量
     *
     * @param type  音量类型
     * @param value 音量值
     */
    public void setVolume(int type, int value) {
        manager.setStreamVolume(type, value, 0);
    }

    /**
     * 设置静音
     *
     * @param type     静音的音量类型
     * @param isSilent 是否静音
     * @WARNING 设置静音的时候，manager的实例必须是同一个，否则会造成无法恢复静音，推荐使用一个全局的VolumeUtils
     */
    public void setSilent(int type, boolean isSilent) {
        if (Build.VERSION.SDK_INT > 22) {
            if (isSilent) {
                manager.adjustStreamVolume(type, AudioManager.ADJUST_MUTE, 0);
            } else {
                manager.adjustStreamVolume(type, AudioManager.ADJUST_UNMUTE, 0);
            }
        } else {
            manager.setStreamMute(type, isSilent);
        }
    }
}
