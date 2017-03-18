package com.wzx.camera.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.wzx.camera.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;


/**
 * Created by wangzhx on 4/7/2016.
 */
public class Picture {
    public static final String TAG = "Picture";
    public static final int MAX = 1280;

    private static final int MAX_FRAME_WIDTH = 1280;
    private static final int MAX_FRAME_HEIGHT = 960;
    private static final int MIN_FRAME_WIDTH = 400;
    private static final int MIN_FRAME_HEIGHT = 300;

    public static Bitmap getBitmap(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = width > height ? width / MAX : height / MAX;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static BitmapFactory.Options getPhotoSize(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            LOG.e(TAG, "scalePhoto photoPath is empty");
            return null;
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
        return bmOptions;
    }

    public static String thumbPhoto(Context context, String photoPath, String name) {
        return scalePhoto(context, photoPath, name, MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT);
    }

    public static String scalePhoto(Context context, String photoPath, String name) {
        return scalePhoto(context, photoPath, name, MAX_FRAME_WIDTH, MAX_FRAME_HEIGHT);
    }

    private static String scalePhoto(Context context, String photoPath, String name, int max_width, int max_height) {
        BitmapFactory.Options bmOptions = getPhotoSize(photoPath);
        if (bmOptions == null) return null;

        float targetW = max_width;
        float targetH = max_height;

        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
        if (photoH * photoW <= targetH * targetW) {
            return photoPath;
        }

        if (photoW < photoH) {
            targetW = max_height;
            targetH = max_width;
        }

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, (int) targetW, (int) targetH);
        bmOptions.inJustDecodeBounds = false;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                photoW = bmOptions.outWidth;
                photoH = bmOptions.outHeight;
                LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
                LOG.d(TAG, "targetW:" + targetW + " targetH:" + targetH);
                Bitmap scaleBitmap = bitmap;
                if (photoW * photoH > targetH * targetW) {
                    float scaleFactor = Math.min(targetW / photoW, targetH / photoH);
                    matrix.postScale(scaleFactor, scaleFactor);
                    scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, false);
                }
                photoPath = Picture.savePicture(context, scaleBitmap, name, 85, Bitmap.CompressFormat.JPEG);

                if (!bitmap.isRecycled()) bitmap.recycle();
                if (scaleBitmap != null && !scaleBitmap.isRecycled()) scaleBitmap.recycle();
            }
        } catch (OutOfMemoryError e) {
            LOG.e(TAG, e.toString());
        }
        return photoPath;
    }

    public static String scalePhoto(Context context, byte[] data, String name) {
        if (data == null) return null;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);

        float targetW = MAX_FRAME_WIDTH;
        float targetH = MAX_FRAME_HEIGHT;

        float photoW = bmOptions.outWidth;
        float photoH = bmOptions.outHeight;
        LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
        if (photoH * photoW <= targetH * targetW) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return Picture.scalePhoto(App.getInstance(), bitmap, name);
        }

        String photoPath = null;

        if (photoW < photoH) {
            targetW = MAX_FRAME_HEIGHT;
            targetH = MAX_FRAME_WIDTH;
        }

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, (int) targetW, (int) targetH);
        bmOptions.inJustDecodeBounds = false;
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                photoW = bmOptions.outWidth;
                photoH = bmOptions.outHeight;
                LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
                LOG.d(TAG, "targetW:" + targetW + " targetH:" + targetH);
                Bitmap scaleBitmap = bitmap;
                if (photoW * photoH > targetH * targetW) {
                    float scaleFactor = Math.min(targetW / photoW, targetH / photoH);
                    matrix.postScale(scaleFactor, scaleFactor);
                    scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmOptions.outWidth, bmOptions.outHeight, matrix, false);
                }
                photoPath = Picture.savePicture(context, scaleBitmap, name, 85, Bitmap.CompressFormat.JPEG);

                if (!bitmap.isRecycled()) bitmap.recycle();
                if (scaleBitmap != null && !scaleBitmap.isRecycled()) scaleBitmap.recycle();
            }
        } catch (OutOfMemoryError e) {
            LOG.e(TAG, e.toString());
        }
        return photoPath;
    }

    public static String scalePhoto(Context context, Bitmap photo, String name) {
        if (photo == null) return null;

        float targetW = MAX_FRAME_WIDTH;
        float targetH = MAX_FRAME_HEIGHT;

        float photoW = photo.getWidth();
        float photoH = photo.getHeight();
        LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
        if (photoH * photoW <= targetH * targetW) {
            return Picture.savePicture(App.getInstance(), photo,
                    name, Bitmap.CompressFormat.JPEG);
        }

        if (photoW < photoH) {
            targetW = MAX_FRAME_HEIGHT;
            targetH = MAX_FRAME_WIDTH;
        }
        String photoPath = null;
        try {
            Matrix matrix = new Matrix();
            LOG.d(TAG, "photoW:" + photoW + " photoH:" + photoH);
            LOG.d(TAG, "targetW:" + targetW + " targetH:" + targetH);

            float scaleFactor = Math.min(targetW / photoW, targetH / photoH);
            matrix.postScale(scaleFactor, scaleFactor);
            Bitmap scaleBitmap = Bitmap.createBitmap(photo, 0, 0, (int) photoW, (int) photoH, matrix, false);
            photoPath = Picture.savePicture(context, scaleBitmap, name, 85, Bitmap.CompressFormat.JPEG);

            if (scaleBitmap != null && !scaleBitmap.isRecycled()) scaleBitmap.recycle();
        } catch (OutOfMemoryError e) {
            LOG.e(TAG, e.toString());
        }
        return photoPath;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static File createFile(Context context, String name) {
        File file = context.getExternalFilesDir(null);
        File save = new File(file, name);
        try {
            save.createNewFile();
        } catch (IOException e) {
            LOG.e(TAG, "createFile ExternalFilesDir IOException");
            file = context.getCacheDir();
            save = new File(file, name);
            try {
                save.createNewFile();
            } catch (IOException e1) {
                LOG.e(TAG, "createFile CacheDir IOException");
                return null;
            }
        }
        return save;
    }

    public static String savePicture(Context context, Bitmap bitmap, String name, Bitmap.CompressFormat format) {
        return savePicture(context, bitmap, name, 85, format);
    }

    public static String savePicture(Context context, Bitmap bitmap, String name, int quality, Bitmap.CompressFormat format) {
        File file = context.getExternalFilesDir(null);
        File save = new File(file, name);
        try {
            save.createNewFile();
        } catch (IOException e) {
            LOG.e(TAG, "savePicture ExternalFilesDir IOException");
            file = context.getCacheDir();
            save = new File(file, name);
            try {
                save.createNewFile();
            } catch (IOException e1) {
                LOG.e(TAG, "savePicture CacheDir IOException");
                return null;
            }
        }
        FileOutputStream mFileOutputStream = null;
        try {
            mFileOutputStream = new FileOutputStream(save);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOG.e(TAG, "savePicture FileNotFoundException");
            return null;
        }
        if (bitmap == null) return null;
        bitmap.compress(format, quality, mFileOutputStream);
        try {
            mFileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.e(TAG, "savePicture flush IOException");
        }
        try {
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.e(TAG, "savePicture close IOException");
        }
        return save.getAbsolutePath();
    }

    public static String saveImage(Context context, byte[] data, String name) {
        File file = context.getExternalFilesDir(null);
        File save = new File(file, name);
        try {
            save.createNewFile();
        } catch (IOException e) {
            LOG.e(TAG, "savePicture ExternalFilesDir IOException");
            file = context.getCacheDir();
            save = new File(file, name);
            try {
                save.createNewFile();
            } catch (IOException e1) {
                LOG.e(TAG, "savePicture CacheDir IOException");
                return null;
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(save);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return save.getAbsolutePath();
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.e(TAG, "getBitmapDegree IOException");
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            LOG.e(TAG, "rotateBitmapByDegree OutOfMemoryError");
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static boolean CheckFileExist(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static boolean copyAssetsFile(Context context, String from, String to) {
        boolean ret;
        try {
            int byteRead = 0;
            InputStream inStream = context.getResources().getAssets().open(from);
            File file = new File(to);
            OutputStream fs = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
            fs.close();
            ret = true;
        } catch (Exception e) {
            ret = false;
            LOG.e(TAG, "copyAssetsFile Exception " + e.toString());
        }
        return ret;
    }

    public static boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options.outWidth != -1;
    }

    public static boolean DeleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.delete();
    }

    public static String queryImageThumbnailById(Context context, int id) {
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] { MediaStore.Images.Thumbnails.DATA };
        String selection = MediaStore.Images.Thumbnails.IMAGE_ID + " = ? ";
        String[] selectionArgs = new String[] { String.valueOf(id) };

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        String thumbnail = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idxData = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                thumbnail = cursor.getString(idxData);
            }
            cursor.close();
        }
        return thumbnail;
    }

    public static File getLatestPicture(Context context) {
        File latestImg = null;
        File dir = context.getExternalFilesDir(null);
        if (dir != null) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                Arrays.sort(files, (file1, file2) -> (int) (file1.lastModified() - file2.lastModified()));
                latestImg = files[files.length - 1];
            }
        }
        return latestImg;
    }

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

}
