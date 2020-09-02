package mx.com.pendulum.olintareas.camera;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class MyRoundedBitmapDisplayer implements BitmapDisplayer {


    protected final int cornerRadius;
    protected final int margin;


    /**
     * @param cornerRadiusPixels radio de la imagen circular
     */
    public MyRoundedBitmapDisplayer(int cornerRadiusPixels) {
        this(cornerRadiusPixels, 0);
    }


    /**
     * @param cornerRadiusPixels radio de la imagen circular
     * @param marginPixels       margen del contorno
     */
    public MyRoundedBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {
        this.cornerRadius = cornerRadiusPixels;
        this.margin = marginPixels;
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware,
                        LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException(
                    "ImageAware should wrap ImageView. ImageViewAware is expected.");
        }
        bitmap = getCenterBitmap(bitmap);
        imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius,
                margin));
    }

    /**
     * @param bmp imagen a cortar desde el centro
     * @return imagen cortada
     */

    private Bitmap getCenterBitmap(Bitmap bmp) {

        Bitmap bmp2;
        if (bmp.getWidth() >= bmp.getHeight()) {
            bmp2 = Bitmap.createBitmap(
                    bmp,
                    bmp.getWidth() / 2 - bmp.getHeight() / 2,
                    0,
                    bmp.getHeight(),
                    bmp.getHeight()
            );

        } else {
            bmp2 = Bitmap.createBitmap(
                    bmp,
                    0,
                    bmp.getHeight() / 2 - bmp.getWidth() / 2,
                    bmp.getWidth(),
                    bmp.getWidth()
            );
        }
        // bmp.recycle();

        return bmp2;

    }

    public static class RoundedDrawable extends Drawable {
        protected final float cornerRadius;
        protected final int margin;
        protected final RectF mRect = new RectF(), mBitmapRect;
        protected final BitmapShader bitmapShader;
        protected final Paint paint;

        public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
            this.cornerRadius = cornerRadius;
            this.margin = margin;
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            mBitmapRect = new RectF(margin, margin, bitmap.getWidth() - margin,
                    bitmap.getHeight() - margin);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(margin, margin, bounds.width() - margin, bounds.height()
                    - margin);
            // Resize the original bitmap to fit the new bound
            Matrix shaderMatrix = new Matrix();
            shaderMatrix.setRectToRect(mBitmapRect, mRect,
                    Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(shaderMatrix);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }
    }
}