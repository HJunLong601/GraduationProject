package com.hjl.graduationpro.Util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class BitmapUtil {

    public static Bitmap decodeSampleBitmapFromResources(Resources res,int resID,int reqWidth,int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resID,options);

        options.inSampleSize  = calculateInSampleSize(options,reqWidth,reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resID,options);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth){
            final int halfHeight = height/2;
            final int halfWidth = width/2;

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
            }
        }
    return inSampleSize;
    }

    public static Bitmap changeBitmapSize(Bitmap bitmap,int width,int hight){

        Paint p = new Paint();
        Bitmap tempBitmap ;
        tempBitmap = Bitmap.createBitmap(width,hight,Bitmap.Config.ARGB_4444);
        Canvas temp_canvas = new Canvas(tempBitmap);

        temp_canvas.drawBitmap(bitmap,
                new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),
                new Rect(0,0,width,hight),
                p);
        return tempBitmap;
    }

}
