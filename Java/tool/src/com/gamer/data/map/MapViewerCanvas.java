package com.gamer.data.map;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 地图画布： 1. 坐标系：左下角为原点 (0,0)，对应世界坐标。 2. 逻辑分层：像素坐标 (Pixel) <-> 格子坐标 (Tile) <-> 世界坐标 (World)。 3.
 * 渲染：自动计算比例居中，支持节点着色与标记点显示；支持拖拽平移视口（与点击通过位移阈值区分）。
 */
public class MapViewerCanvas extends JPanel {

    /**
     * 地图区域点击回调（仅在未触发拖拽平移时触发）。
     */
    public interface MapClickListener {

        /**
         * 在画布坐标系下的点击位置。
         *
         * @param pixelX
         *            画布 X
         * @param pixelY
         *            画布 Y
         */
        void onMapCanvasClicked(int pixelX, int pixelY);
    }

    /**
     * 右键弹出菜单（与拖拽平移独立；需在 {@link #isWorldInPaintedLevel(int, int)} 为 true 时由外部决定是否展示）。
     */
    public interface MapPopupListener {

        /**
         * @param pixelX
         *            画布 X
         * @param pixelY
         *            画布 Y
         */
        void onMapPopup(int pixelX, int pixelY);
    }

    private static final long serialVersionUID = 1L;
    private static final int MARKER_NONE = -999;
    private static final Color COLOR_GRID = new Color(180, 180, 180);// 网格线颜色
    private static final Color COLOR_MARKER = Color.RED;// 标记点颜色
    private static final Color COLOR_SELECTED_NODE = new Color(255, 80, 80);// 选中关卡节点颜色
    private static final int NODE_HIT_RADIUS = 2;// 关卡节点点击范围半径

    /**
     * 选中节点说明框中心相对节点中心的偏移（屏幕像素，向右下为正），可按观感自行调整。
     */
    private static final int SELECTED_NODE_LABEL_OFFSET_X_PX = 36;

    private static final int SELECTED_NODE_LABEL_OFFSET_Y_PX = -28;

    /** 节点说明文字内边距（像素） */
    private static final int SELECTED_NODE_LABEL_PADDING_PX = 4;

    private static final Color COLOR_SELECTED_NODE_LABEL_BG = new Color(255, 248, 220, 230);

    private static final Color COLOR_SELECTED_NODE_LABEL_BORDER = new Color(160, 120, 40);

    private MapData mapData;// 地图数据
    private List<LevelNodeBean> levelNodes = new ArrayList<>();// 关卡节点列表
    private int markerWorldX = MARKER_NONE;// 标记点世界X坐标
    private int markerWorldZ = MARKER_NONE;// 标记点世界Z坐标
    private LevelNodeBean selectedLevelNode;// 选中关卡节点
    private double zoomScale = 1.0D;// 缩放比例

    /** 地图格子颜色配置 */
    private static final Color[] CELL_COLORS = {new Color(200, 255, 200), new Color(200, 220, 255),
        new Color(255, 230, 200), new Color(230, 200, 255), new Color(255, 255, 200), new Color(200, 255, 255),
        new Color(255, 210, 210), new Color(210, 255, 210), new Color(210, 210, 255), new Color(255, 220, 255)};

    /** 地图与视口边缘留白（像素），便于看到坐标轴标签 */
    private static final int MAP_VIEW_MARGIN_PX = 40;

    /** 挂到 JViewport 上：视口尺寸变化时重算 preferredSize，保证缩放后出现滚动条 */
    private ComponentAdapter viewportResizeAdapter;

    /** 缩放或视口变化时通知外部更新「放大 / 还原」按钮状态 */
    private Runnable zoomStateListener;

    /** 未拖拽时的地图点击 */
    private MapClickListener mapClickListener;

    /** 右键弹出（平台相关触发点在 press/release 之一） */
    private MapPopupListener mapPopupListener;

    /** 拖拽平移：按下时在画布上的坐标 */
    private int panPressCanvasX;

    private int panPressCanvasY;

    /** 拖拽平移：按下时视口 viewPosition */
    private int panStartViewX;

    private int panStartViewY;

    /** 本次按下后是否已判定为拖拽平移（超过位移阈值） */
    private boolean panGestureActive;

    /** 按下至当前是否移动超过阈值，用于 mouseReleased 时区分点击 */
    private boolean panMovedBeyondThreshold;

    /** 拖拽平移与点击区分的位移阈值（像素，平方距离用该值平方） */
    private static final int PAN_CLICK_THRESHOLD_PX = 5;

    public MapViewerCanvas() {
        setBackground(Color.WHITE);// 设置背景颜色
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (panGestureActive) {
                    return;
                }
                LevelNodeBean node = getLevelNodeAt(e.getX(), e.getY());
                if (node == null) {
                    setToolTipText(null);
                    return;
                }
                setToolTipText(node.toString());// 设置工具提示文本
            }
        });
        installPanAndClickHandler();
        installPopupHandler();
    }

    /**
     * 设置缩放状态监听（用于更新工具栏按钮启用/禁用）。
     *
     * @param listener
     *            可为 null
     */
    public void setZoomStateListener(Runnable listener) {
        this.zoomStateListener = listener;
    }

    /**
     * 设置地图点击监听（与拖拽平移互斥）。
     *
     * @param listener
     *            可为 null
     */
    public void setMapClickListener(MapClickListener listener) {
        this.mapClickListener = listener;
    }

    /**
     * 设置右键弹出回调（可为 null）。
     *
     * @param listener
     *            监听
     */
    public void setMapPopupListener(MapPopupListener listener) {
        this.mapPopupListener = listener;
    }

    /**
     * 当前缩放是否已达算法上限（再提高 zoomScale 单格像素不再变大）。
     *
     * @return 无地图时视为已达上限（不可再放大）
     */
    public boolean canBigger() {
        if (mapData == null || mapData.getMap() == null) {
            return false;
        }
        Dimension vp = resolveViewportSize();
        int cols = mapData.getWidth();
        int rows = mapData.getHeight();
        double c0 = computeCellSize(vp.width, vp.height, cols, rows, zoomScale);
        double c1 = computeCellSize(vp.width, vp.height, cols, rows, zoomScale + 0.001D);
        return c1 > c0 + 1e-9D;
    }

    /**
     * 是否还能缩小
     *
     * @return 无地图时视为已达下限（不可再缩小）
     */
    public boolean canSmaller() {
        return zoomScale > 1.0D;
    }

    /**
     * 是否为默认缩放（1.0）。
     *
     * @return true 表示与「还原」目标一致
     */
    public boolean canReset() {
        return Math.abs(zoomScale - 1.0D) > 0.01d;
    }

    private void fireZoomStateChanged() {
        if (zoomStateListener != null) {
            zoomStateListener.run();
        }
    }

    /**
     * 将视口滚动位置限制在合法范围内。
     *
     * @param vp
     *            视口
     * @param x
     *            期望 viewPosition.x
     * @param y
     *            期望 viewPosition.y
     * @return 裁剪后的位置
     */
    private static Point clampViewPosition(JViewport vp, int x, int y) {
        Component view = vp.getView();
        if (view == null) {
            return new Point(0, 0);
        }
        Dimension extent = vp.getExtentSize();
        int vw = view.getWidth();
        int vh = view.getHeight();
        int maxX = Math.max(0, vw - extent.width);
        int maxY = Math.max(0, vh - extent.height);
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > maxX) {
            x = maxX;
        }
        if (y > maxY) {
            y = maxY;
        }
        return new Point(x, y);
    }

    /**
     * 安装左键拖拽平移（JScrollPane 视口）与点击区分逻辑。
     */
    private void installPanAndClickHandler() {
        MouseInputAdapter panAdapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                panPressCanvasX = e.getX();
                panPressCanvasY = e.getY();
                panGestureActive = false;
                panMovedBeyondThreshold = false;
                Container parent = getParent();
                if (parent instanceof JViewport) {
                    Point p = ((JViewport)parent).getViewPosition();
                    panStartViewX = p.x;
                    panStartViewY = p.y;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
                    return;
                }
                int dx = e.getX() - panPressCanvasX;
                int dy = e.getY() - panPressCanvasY;
                if (dx * dx + dy * dy >= PAN_CLICK_THRESHOLD_PX * PAN_CLICK_THRESHOLD_PX) {
                    panMovedBeyondThreshold = true;
                }
                if (!panGestureActive) {
                    if (!panMovedBeyondThreshold) {
                        return;
                    }
                    panGestureActive = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
                Container parent = getParent();
                if (!(parent instanceof JViewport)) {
                    return;
                }
                JViewport vp = (JViewport)parent;
                int nx = panStartViewX - (e.getX() - panPressCanvasX);
                int ny = panStartViewY - (e.getY() - panPressCanvasY);
                vp.setViewPosition(clampViewPosition(vp, nx, ny));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (panGestureActive) {
                    setCursor(Cursor.getDefaultCursor());
                }
                if (!panGestureActive && !panMovedBeyondThreshold && mapClickListener != null) {
                    mapClickListener.onMapCanvasClicked(e.getX(), e.getY());
                }
                panGestureActive = false;
                panMovedBeyondThreshold = false;
            }
        };
        addMouseListener(panAdapter);
        addMouseMotionListener(panAdapter);
    }

    /**
     * 弹出菜单触发：兼容 Windows（release）与部分平台（press）。
     */
    private void installPopupHandler() {
        MouseAdapter popupAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tryFirePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tryFirePopup(e);
            }

            private void tryFirePopup(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                if (mapPopupListener != null) {
                    mapPopupListener.onMapPopup(e.getX(), e.getY());
                }
            }
        };
        addMouseListener(popupAdapter);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        Container p = getParent();
        if (p instanceof JViewport) {
            viewportResizeAdapter = new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    syncCanvasSizeToMap();
                }
            };
            p.addComponentListener(viewportResizeAdapter);
            syncCanvasSizeToMap();
        }
    }

    @Override
    public void removeNotify() {
        Container p = getParent();
        if (p instanceof JViewport && viewportResizeAdapter != null) {
            p.removeComponentListener(viewportResizeAdapter);
            viewportResizeAdapter = null;
        }
        super.removeNotify();
    }

    // --- 数据操作接口 ---
    public void setMapData(MapData data) {
        this.mapData = data;
        syncCanvasSizeToMap();
        repaint();
    }

    /**
     * 设置关卡节点
     * 
     * @param nodes
     *            关卡节点列表
     */
    public void setLevelNodes(List<LevelNodeBean> nodes) {
        this.levelNodes = (nodes != null) ? nodes : new ArrayList<>();
        this.selectedLevelNode = null;
        repaint();
    }

    /**
     * 设置标记点
     * 
     * @param worldX
     *            世界X坐标
     * @param worldZ
     *            世界Z坐标
     */
    public void setMarkerPoint(int worldX, int worldZ) {
        this.markerWorldX = worldX;
        this.markerWorldZ = worldZ;
        repaint();
    }

    /**
     * 清除标记点
     */
    public void clearMarkerPoint() {
        setMarkerPoint(MARKER_NONE, MARKER_NONE);
    }

    /**
     * 设置选中关卡节点（高亮 + 旁注说明；手动「显示」红点仍由 {@link #setMarkerPoint} 控制）。
     *
     * @param node
     *            关卡节点，可为 null 表示取消选中
     */
    public void setSelectedLevelNode(LevelNodeBean node) {
        this.selectedLevelNode = node;
        repaint();
    }

    public void setZoomScale(double zoomScale) {
        if (Math.abs(zoomScale - this.zoomScale) < 0.001D) {
            return;
        }
        if (zoomScale <= 0D) {
            this.zoomScale = 1.0D;
        } else {
            this.zoomScale = zoomScale;
        }
        syncCanvasSizeToMap();
        repaint();
    }

    public double getZoomScale() {
        return zoomScale;
    }

    // --- 核心绘制逻辑 ---
        @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapData == null || mapData.getMap() == null) {
            g.drawString("请先选择地图文件", 20, 30);
            return;
        }

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. 初始化渲染上下文（统一布局计算；缩放按视口计，画布可大于视口以配合滚动条）
        ViewContext ctx = new ViewContext();

        // 2. 绘制地图格子
        for (int col = 0; col < ctx.cols; col++) {
            for (int row = 0; row < ctx.rows; row++) {
                int val = mapData.getMap()[col][row];
                if (val != 0) {
                    g2.setColor(colorForCellValue(val));
                    fillTile(g2, col, row, ctx);
                }
            }
        }

        // 3. 绘制网格线
        g2.setColor(COLOR_GRID);
        for (int i = 0; i <= ctx.cols; i++) { // 纵线
            int x = (int)(ctx.mapLeftX + i * ctx.cellSize);
            g2.drawLine(x, (int)ctx.mapTopY, x, (int)ctx.mapBottomY);
        }
        for (int i = 0; i <= ctx.rows; i++) { // 横线
            int y = (int)(ctx.mapTopY + i * ctx.cellSize);
            g2.drawLine((int)ctx.mapLeftX, y, (int)ctx.mapRightX, y);
        }

        // 4. 绘制 Level 节点
        for (LevelNodeBean node : levelNodes) {
            drawCircleAtWorld(g2, node.getX(), node.getZ(), ctx, NodeType.getColor(node.getType()), 8);
        }

        // 4.1 选中节点：旁注说明
        if (selectedLevelNode != null) {
            drawSelectedNodeLabel(g2, ctx, selectedLevelNode);
        }

        // 4.2 绘制当前选中节点（突出显示，叠在说明框之上）
        if (selectedLevelNode != null) {
            drawCircleAtWorld(g2, selectedLevelNode.getX(), selectedLevelNode.getZ(), ctx, COLOR_SELECTED_NODE, 3);
        }

        // 5. 绘制 Marker 标记点
        if (markerWorldX != MARKER_NONE) {
            drawCircleAtWorld(g2, markerWorldX, markerWorldZ, ctx, COLOR_MARKER, 3);
        }

        // 6. 绘制坐标轴辅助
        drawAxisLabels(g2, ctx);
    }

    // --- 内部辅助工具 ---

    /** 将屏幕像素坐标转为世界坐标 */
    public int[] pixelToWorld(int px, int py) {
        if (mapData == null) {
            return null;
        }
        ViewContext ctx = new ViewContext();
        if (px < ctx.mapLeftX || py < ctx.mapTopY || px > ctx.mapRightX || py > ctx.mapBottomY) {
            return null;
        }

        double fx = (px - ctx.mapLeftX) / ctx.cellSize;
        double fz = (ctx.mapBottomY - py) / ctx.cellSize;
        int worldX = (int)Math.round(fx * MapRingUtil.WORLD_PIXEL_PER_CELL);
        int worldZ = (int)Math.round(fz * MapRingUtil.WORLD_PIXEL_PER_CELL);
        return new int[] {worldX, worldZ};
    }

    /**
     * 世界坐标是否落在地图中「已着色」的关卡格内（与绘制非 0 格一致），用于限制右键编辑/新建。
     *
     * @param worldX
     *            世界 X
     * @param worldZ
     *            世界 Z
     * @return 在地图范围内且格子值非 0 时为 true
     */
    public boolean isWorldInPaintedLevel(int worldX, int worldZ) {
        if (mapData == null || mapData.getMap() == null) {
            return false;
        }
        int w = mapData.getWidth();
        int h = mapData.getHeight();
        int col = worldX / MapRingUtil.WORLD_PIXEL_PER_CELL;
        int row = worldZ / MapRingUtil.WORLD_PIXEL_PER_CELL;
        if (col < 0 || col >= w || row < 0 || row >= h) {
            return false;
        }
        return mapData.getMap()[col][row] != 0;
    }

    /**
     * 世界坐标转画布像素（节点中心）
     *
     * @param worldX
     *            世界 X
     * @param worldZ
     *            世界 Z
     * @param ctx
     *            视图上下文
     * @return [px, py]
     */
    private static int[] worldToPixel(int worldX, int worldZ, ViewContext ctx) {
        double tx = (double)worldX / MapRingUtil.WORLD_PIXEL_PER_CELL;
        double tz = (double)worldZ / MapRingUtil.WORLD_PIXEL_PER_CELL;
        int cx = (int)Math.round(ctx.mapLeftX + tx * ctx.cellSize);
        int cy = (int)Math.round(ctx.mapBottomY - tz * ctx.cellSize);
        return new int[] {cx, cy};
    }

    /**
     * 绘制选中节点旁的说明框
     *
     * @param g2
     *            画笔
     * @param ctx
     *            视图上下文
     * @param node
     *            节点
     */
    private void drawSelectedNodeLabel(Graphics2D g2, ViewContext ctx, LevelNodeBean node) {
        int[] tipPt = worldToPixel(node.getX(), node.getZ(), ctx);
        int tipX = tipPt[0];
        int tipY = tipPt[1];

        String label = node.toString();
        if (label == null) {
            label = "";
        }
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label);
        int th = fm.getHeight();
        int boxW = tw + SELECTED_NODE_LABEL_PADDING_PX * 2;
        int boxH = th + SELECTED_NODE_LABEL_PADDING_PX * 2;
        int boxCx = tipX + SELECTED_NODE_LABEL_OFFSET_X_PX;
        int boxCy = tipY + SELECTED_NODE_LABEL_OFFSET_Y_PX;
        int boxX = boxCx - boxW / 2;
        int boxY = boxCy - boxH / 2;

        int pw = getWidth();
        int ph = getHeight();
        if (boxX < 2) {
            boxX = 2;
        }
        if (boxY < 2) {
            boxY = 2;
        }
        if (boxX + boxW > pw - 2) {
            boxX = pw - 2 - boxW;
        }
        if (boxY + boxH > ph - 2) {
            boxY = ph - 2 - boxH;
        }

        g2.setColor(COLOR_SELECTED_NODE_LABEL_BG);
        g2.fill(new RoundRectangle2D.Double(boxX, boxY, boxW, boxH, 6.0D, 6.0D));
        g2.setColor(COLOR_SELECTED_NODE_LABEL_BORDER);
        g2.draw(new RoundRectangle2D.Double(boxX, boxY, boxW, boxH, 6.0D, 6.0D));
        g2.setColor(Color.BLACK);
        int textBaselineY = boxY + SELECTED_NODE_LABEL_PADDING_PX + fm.getAscent();
        g2.drawString(label, boxX + SELECTED_NODE_LABEL_PADDING_PX, textBaselineY);
    }

    /** 填充一个格子 */
    private void fillTile(Graphics2D g2, int col, int row, ViewContext ctx) {
        int x = (int)(ctx.mapLeftX + col * ctx.cellSize);
        int y = (int)(ctx.mapTopY + (ctx.rows - 1 - row) * ctx.cellSize);
        g2.fillRect(x, y, (int)ctx.cellSize + 1, (int)ctx.cellSize + 1);
    }

    /**
     * 在世界坐标系中绘制一个圆形
     * 
     * @param g2
     *            图形上下文
     * @param worldX
     *            世界X坐标
     * @param worldZ
     *            世界Z坐标
     * @param ctx
     *            视图上下文
     * @param color
     *            圆形颜色
     * @param diam
     *            圆形直径
     */
    private void drawCircleAtWorld(Graphics2D g2, int worldX, int worldZ, ViewContext ctx, Color color, int diam) {
        int[] p = worldToPixel(worldX, worldZ, ctx);
        drawCircle(g2, p[0], p[1], color, diam);
    }

    /**
     * 绘制圆形
     * 
     * @param g2
     *            图形上下文
     * @param cx
     *            圆形X坐标
     * @param cy
     *            圆形Y坐标
     * @param color
     *            圆形颜色
     * @param diam
     *            圆形直径
     */
    private void drawCircle(Graphics2D g2, int cx, int cy, Color color, int diam) {
        int r = diam / 2;
        g2.setColor(color);
        g2.fillOval(cx - r, cy - r, diam, diam);
        g2.setColor(Color.BLACK);
        g2.drawOval(cx - r, cy - r, diam, diam);
    }

    /**
     * 绘制坐标轴标签
     * 
     * @param g2
     *            图形上下文
     * @param ctx
     *            视图上下文
     */
    private void drawAxisLabels(Graphics2D g2, ViewContext ctx) {
        g2.setColor(Color.BLACK);
        int baseY = (int)ctx.mapBottomY;
        g2.drawString("原点(左下) (0,0)", (int)ctx.mapLeftX, baseY + 15);
        g2.drawString("X→", (int)ctx.mapRightX - 15, baseY + 15);
        g2.drawString("Y↑", (int)ctx.mapLeftX - 18, (int)ctx.mapTopY + 12);
    }

    /**
     * 获取格子颜色
     * 
     * @param val
     *            格子值
     * @return 格子颜色
     */
    private static Color colorForCellValue(int val) {
        return val <= 0 ? Color.GRAY : CELL_COLORS[(val - 1) % CELL_COLORS.length];
    }

    /**
     * 获取关卡节点
     * 
     * @param px
     *            像素X坐标
     * @param py
     *            像素Y坐标
     * @return 关卡节点
     */
    public LevelNodeBean getLevelNodeAt(int px, int py) {
        if (mapData == null) {
            return null;
        }
        ViewContext ctx = new ViewContext();
        LevelNodeBean hit = null;
        double minDist2 = Double.MAX_VALUE;

        for (LevelNodeBean node : levelNodes) {// 遍历关卡节点列表
            double tx = (double)node.getX() / MapRingUtil.WORLD_PIXEL_PER_CELL;
            double tz = (double)node.getZ() / MapRingUtil.WORLD_PIXEL_PER_CELL;
            double cx = ctx.mapLeftX + tx * ctx.cellSize;
            double cy = ctx.mapBottomY - tz * ctx.cellSize;
            double dx = px - cx;
            double dy = py - cy;
            double hitPx = Math.max(NODE_HIT_RADIUS, ctx.cellSize * 0.12D);
            double dist2 = dx * dx + dy * dy;
            if (dist2 <= hitPx * hitPx && dist2 < minDist2) {
                minDist2 = dist2;
                hit = node;
            }
        }
        return hit;
    }

    /**
     * 解析当前用于计算缩放的视口尺寸（置于 JScrollPane 内时为视口大小，避免用整块画布宽高导致缩放失真）。
     *
     * @return 视口宽高，至少为 1
     */
    private Dimension resolveViewportSize() {
        Rectangle vr = getVisibleRect();
        int vw = vr.width;
        int vh = vr.height;
        if (vw <= 0 || vh <= 0) {
            Container p = getParent();
            if (p instanceof JViewport) {
                Dimension ext = ((JViewport)p).getExtentSize();
                vw = Math.max(1, ext.width);
                vh = Math.max(1, ext.height);
            } else {
                vw = Math.max(1, getWidth());
                vh = Math.max(1, getHeight());
            }
        }
        if (vw == 1 && vh == 1) {
            vw = 400;
            vh = 300;
        }
        return new Dimension(vw, vh);
    }

    /**
     * 单格像素边长：与原先逻辑一致，但短边取自视口而非画布总宽高。
     *
     * @param vw
     *            视口宽
     * @param vh
     *            视口高
     * @param cols
     *            地图列数
     * @param rows
     *            地图行数
     * @param zoom
     *            缩放系数
     * @return 单元格边长（像素）
     */
    private static double computeCellSize(int vw, int vh, int cols, int rows, double zoom) {
        double squareSize = Math.min(vw, (double)vh) * zoom;
        squareSize = Math.max(100D, Math.min(squareSize, Math.min(vw, (double)vh) * 3D));
        return squareSize / (double)Math.max(cols, rows);
    }

    /**
     * 根据地图与缩放更新画布 preferredSize，使 JScrollPane 在地图大于视口时出现横向/纵向滚动条。
     */
    private void syncCanvasSizeToMap() {
        if (mapData == null || mapData.getMap() == null) {
            Dimension d = new Dimension(400, 300);
            Dimension cur = getPreferredSize();
            if (cur == null || cur.width != d.width || cur.height != d.height) {
                setPreferredSize(d);
                revalidate();
            }
            fireZoomStateChanged();
            return;
        }
        Dimension vp = resolveViewportSize();
        int cols = mapData.getWidth();
        int rows = mapData.getHeight();
        double cellSize = computeCellSize(vp.width, vp.height, cols, rows, zoomScale);
        int mapW = (int)Math.ceil(cols * cellSize);
        int mapH = (int)Math.ceil(rows * cellSize);
        int prefW = Math.max(vp.width, mapW + MAP_VIEW_MARGIN_PX);
        int prefH = Math.max(vp.height, mapH + MAP_VIEW_MARGIN_PX);
        Dimension pref = new Dimension(prefW, prefH);
        Dimension cur = getPreferredSize();
        if (cur == null || cur.width != pref.width || cur.height != pref.height) {
            setPreferredSize(pref);
            if (getParent() != null) {
                revalidate();
            }
        }
        fireZoomStateChanged();
    }

    /** 渲染上下文：封装画布布局参数，避免重复计算 */
    private class ViewContext {
        final double cellSize;
        final double mapLeftX;
        final double mapTopY;
        final double mapRightX;
        final double mapBottomY;
        final int cols;
        final int rows;

        ViewContext() {
            this.cols = mapData.getWidth();
            this.rows = mapData.getHeight();
            Dimension vp = resolveViewportSize();
            this.cellSize = computeCellSize(vp.width, vp.height, cols, rows, zoomScale);
            double mapWidth = cols * cellSize;
            double mapHeight = rows * cellSize;
            int cw = Math.max(1, getWidth());
            int ch = Math.max(1, getHeight());
            this.mapLeftX = ((double)cw - mapWidth) / 2D;
            this.mapTopY = ((double)ch - mapHeight) / 2D;
            this.mapRightX = mapLeftX + mapWidth;
            this.mapBottomY = mapTopY + mapHeight;
        }
    }
}