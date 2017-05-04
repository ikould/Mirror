package com.ikould.mirror.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.ikould.mirror.CoreApplication;
import com.ikould.mirror.activity.MainActivity;
import com.peiyu.frame.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 拍照
 * <p>
 * Created by liudong on 2017/5/3.
 */

public class TakePicture {

    public static final class TakePictureCallback implements Camera.PictureCallback {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                matrix.postScale(1, -1);   //镜像垂直翻转
                matrix.postScale(-1, 1);   //镜像垂直翻转
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                String filePath = Environment.getExternalStorageDirectory() + File.separator + "Mirror" + File.separator + "Picture";
                File fileParent = new File(filePath);
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                File file = new File(filePath, System.currentTimeMillis() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(file);
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                camera.stopPreview();
                camera.startPreview();//处理完数据之后可以预览
                ToastUtils.show(CoreApplication.getInstance(), "照片保存已保存到 " + filePath + " 文件夹中");
            } catch (Exception e) {
                Log.d("TakePictureCallback", "onPictureTaken: e = " + e);
            }
        }
    }
}
