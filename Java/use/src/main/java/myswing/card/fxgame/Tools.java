package myswing.card.fxgame;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * @author admin
 * @className Tools
 * @description
 * @createDate 2025/2/21 3:20
 */
public class Tools {

	// 判断线段是否与矩形相交
	public static boolean doesLineIntersectRectangle(Line line, Rectangle rect) {
		double x1 = line.getStartX();
		double y1 = line.getStartY();
		double x2 = line.getEndX();
		double y2 = line.getEndY();

		double rectX = rect.getX();
		double rectY = rect.getY();
		double rectWidth = rect.getWidth();
		double rectHeight = rect.getHeight();

		// 矩形的四个顶点
		double[] rectPointsX = { rectX, rectX + rectWidth, rectX + rectWidth, rectX };
		double[] rectPointsY = { rectY, rectY, rectY + rectHeight, rectY + rectHeight };

		// 检查线段是否与矩形的每条边相交
		for (int i = 0; i < 4; i++) {
			double rx1 = rectPointsX[i];
			double ry1 = rectPointsY[i];
			double rx2 = rectPointsX[(i + 1) % 4];
			double ry2 = rectPointsY[(i + 1) % 4];

			if (doLinesIntersect(x1, y1, x2, y2, rx1, ry1, rx2, ry2)) {
				return true;
			}
		}

		return false;
	}

	// 判断两条线段是否相交
	private static boolean doLinesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		// 计算方向
		double d1 = direction(x3, y3, x4, y4, x1, y1);
		double d2 = direction(x3, y3, x4, y4, x2, y2);
		double d3 = direction(x1, y1, x2, y2, x3, y3);
		double d4 = direction(x1, y1, x2, y2, x4, y4);

		// 如果两个方向相反，则线段相交
		if ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0) &&
				(d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0)) {
			return true;
		}

		// 检查特殊情况：共线且有重叠
		if (d1 == 0 && isPointOnSegment(x3, y3, x4, y4, x1, y1)) {
			return true;
		}
		if (d2 == 0 && isPointOnSegment(x3, y3, x4, y4, x2, y2)) {
			return true;
		}
		if (d3 == 0 && isPointOnSegment(x1, y1, x2, y2, x3, y3)) {
			return true;
		}
		return d4 == 0 && isPointOnSegment(x1, y1, x2, y2, x4, y4);
	}

	// 计算三点的方向（顺时针、逆时针或共线）
	private static double direction(double ax, double ay, double bx, double by, double cx, double cy) {
		return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
	}

	// 判断点是否在线段上
	private static boolean isPointOnSegment(double x1, double y1, double x2, double y2, double px, double py) {
		return Math.min(x1, x2) <= px && px <= Math.max(x1, x2) && Math.min(y1, y2) <= py && py <= Math.max(y1, y2);
	}
}
