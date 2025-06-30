package m3.eventplanner.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MultiDotDrawable extends Drawable {
    private final List<Integer> colors;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float dotRadius;

    public MultiDotDrawable(List<Integer> eventColors, float dotRadius) {
        this.colors = eventColors;
        this.dotRadius = dotRadius;
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (colors.isEmpty()) return;

        float totalWidth = (colors.size() - 1) * dotRadius * 3; // spacing between dots
        float startX = (getBounds().width() - totalWidth) / 2f;
        float centerY = getBounds().height() / 2f;

        for (int i = 0; i < colors.size(); i++) {
            paint.setColor(colors.get(i));
            float x = startX + i * dotRadius * 3;
            canvas.drawCircle(x, centerY, dotRadius, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
