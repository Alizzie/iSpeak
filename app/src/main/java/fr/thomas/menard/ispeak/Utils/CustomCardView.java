package fr.thomas.menard.ispeak.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;


public class CustomCardView extends CardView {

    private List<Point> points;


    public CustomCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize any necessary settings or variables
        points = new ArrayList<>();
        // Add sample points (you can replace this with your data)
        points.add(new Point(50, 100));
        points.add(new Point(150, 200));
        points.add(new Point(250, 150));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLineAndPoints(canvas);
    }

    private void drawLineAndPoints(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(50);

        // Draw line
        for (int i = 1; i < points.size(); i++) {
            Point startPoint = points.get(i - 1);
            Point endPoint = points.get(i);
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
        }

        // Draw points
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 50, paint);
        }
    }
}
