package com.gamer.data.map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 关卡 Level JSON 文档：与 {@link LevelFileLoader} 相同的 DFS 顺序扁平化；写回时按字节区间局部替换，未修改区域与原文一致。
 */
public final class LevelDocument {

    private File file;
    private JsonNode root;
    /** 与磁盘一致的 UTF-8 原文（局部保存后更新） */
    private byte[] rawUtf8;
    private LevelJsonByteSpans spans;
    private final List<LevelNodeBean> flatBeans = new ArrayList<>();
    private final List<ObjectNode> flatJson = new ArrayList<>();
    /** 与 {@link #flatBeans} 一一对应：自根 {@code nodes} 数组起的下标路径 */
    private final List<ArrayList<Integer>> flatPaths = new ArrayList<>();

    private static final ObjectMapper MAPPER = createMapper();

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    private LevelDocument() {}

    /** 去掉 UTF-8 BOM，避免与 JsonParser 字节偏移不一致导致 span 错位、写坏 JSON */
    private static byte[] stripUtf8Bom(byte[] raw) {
        if (raw != null && raw.length >= 3 && raw[0] == (byte)0xEF && raw[1] == (byte)0xBB && raw[2] == (byte)0xBF) {
            byte[] out = new byte[raw.length - 3];
            System.arraycopy(raw, 3, out, 0, out.length);
            return out;
        }
        return raw;
    }

    /**
     * 从 txt 加载并建立 DFS 扁平列表（与 {@link LevelFileLoader#load(File)} 遍历顺序一致）。
     */
    public static LevelDocument load(File file) throws Exception {
        LevelDocument doc = new LevelDocument();
        doc.file = file;
        doc.rawUtf8 = stripUtf8Bom(Files.readAllBytes(file.toPath()));
        doc.root = MAPPER.readTree(doc.rawUtf8);
        doc.spans = LevelJsonByteSpans.build(doc.rawUtf8, MAPPER);
        doc.rebuildFlat();
        return doc;
    }

    /**
     * 在修改 JSON 树之后重新生成扁平列表（会清空路径缓存并重建）。
     */
    public void rebuildFlat() {
        flatBeans.clear();
        flatJson.clear();
        flatPaths.clear();
        if (root == null) {
            return;
        }
        JsonNode nodesRoot = root;
        if (root.isObject() && root.has("nodes")) {
            nodesRoot = root.get("nodes");
        }
        if (nodesRoot == null || !nodesRoot.isArray()) {
            return;
        }
        ArrayNode arr = (ArrayNode)nodesRoot;
        for (int i = 0; i < arr.size(); i++) {
            JsonNode n = arr.get(i);
            if (n.isObject()) {
                ArrayList<Integer> path = new ArrayList<>();
                path.add(i);
                dfsVisitWithPath((ObjectNode)n, path);
            }
        }
    }

    private void dfsVisitWithPath(ObjectNode obj, ArrayList<Integer> path) {
        flatBeans.add(parseBean(obj));
        flatJson.add(obj);
        flatPaths.add(new ArrayList<>(path));
        JsonNode ch = obj.get("children");
        if (ch != null && ch.isArray()) {
            ArrayNode children = (ArrayNode)ch;
            for (int i = 0; i < children.size(); i++) {
                JsonNode c = children.get(i);
                if (c.isObject()) {
                    ArrayList<Integer> cp = new ArrayList<>(path);
                    cp.add(i);
                    dfsVisitWithPath((ObjectNode)c, cp);
                }
            }
        }
    }

    private static LevelNodeBean parseBean(ObjectNode n) {
        int x = 0;
        int z = 0;
        JsonNode pos = n.get("Position");
        if (pos != null) {
            if (pos.has("x")) {
                x = (int)Math.round(pos.get("x").asDouble());
            }
            if (pos.has("z")) {
                z = (int)Math.round(pos.get("z").asDouble());
            }
        }
        String param = "";
        JsonNode p = n.get("Param");
        if (p != null && !p.isNull()) {
            param = p.asText();
        }
        int type = n.path("Type").asInt(0);
        int dataId = n.path("DataId").asInt(0);
        int belongId = n.path("BelongId").asInt(-1);
        int nextDataId = n.path("NextDataId").asInt(0);
        int textId = n.path("TextId").asInt(0);
        int indexId = n.path("IndexId").asInt(0);
        return new LevelNodeBean(x, z, type, dataId, belongId, nextDataId, textId, indexId, param);
    }

    /**
     * 浅拷贝扁平列表中某一节点的 Bean 快照（用于编辑前对比）。
     */
    public LevelNodeBean copyBeanAt(int flatIndex) {
        if (flatIndex < 0 || flatIndex >= flatBeans.size()) {
            return null;
        }
        return flatBeans.get(flatIndex).copy();
    }

    /**
     * 将内存中的 Bean 写回对应的 JSON 节点。
     */
    public void syncBeanToJson(int index, LevelNodeBean bean) {
        if (index < 0 || index >= flatJson.size()) {
            return;
        }
        applyBeanToJson(bean, flatJson.get(index));
    }

    /**
     * 将 Bean 字段应用到 JSON 对象（用于新建节点或编辑）。
     */
    public static void applyBeanToJson(LevelNodeBean bean, ObjectNode json) {
        json.put("BelongId", bean.getBelongId());
        json.put("Type", bean.getType());
        json.put("TextId", bean.getTextId());
        json.put("DataId", bean.getDataId());
        json.put("IndexId", bean.getIndexId());
        json.put("NextDataId", bean.getNextDataId());
        json.put("Param", bean.getParamRaw() == null ? "" : bean.getParamRaw());
        ObjectNode pos = (ObjectNode)json.get("Position");
        if (pos == null) {
            pos = JsonNodeFactory.instance.objectNode();
            json.set("Position", pos);
        }
        pos.put("x", (double)bean.getX());
        pos.put("y", 0.0);
        pos.put("z", (double)bean.getZ());
    }

    /**
     * 按字段差异做字节级局部替换并写盘，随后从磁盘字节重建树与索引。
     */
    public void savePartialNodeEdit(int flatIndex, LevelNodeBean before, LevelNodeBean after) throws Exception {
        if (file == null) {
            throw new IllegalStateException("file 为空");
        }
        if (rawUtf8 == null || spans == null) {
            throw new IllegalStateException("当前文档未带原文缓存，无法局部保存");
        }
        if (flatIndex < 0 || flatIndex >= flatPaths.size()) {
            throw new IllegalArgumentException("flatIndex 越界");
        }
        if (before == null || after == null) {
            throw new IllegalArgumentException("before/after 不能为空");
        }
        String base = LevelJsonByteSpans.pathKey(flatPaths.get(flatIndex));
        List<LevelJsonByteSpans.Replacement> reps = new ArrayList<>();
        if (before.getBelongId() != after.getBelongId()) {
            addIntReplacement(base + "/BelongId", after.getBelongId(), reps);
        }
        if (before.getType() != after.getType()) {
            addIntReplacement(base + "/Type", after.getType(), reps);
        }
        if (before.getTextId() != after.getTextId()) {
            addIntReplacement(base + "/TextId", after.getTextId(), reps);
        }
        if (before.getDataId() != after.getDataId()) {
            addIntReplacement(base + "/DataId", after.getDataId(), reps);
        }
        if (before.getIndexId() != after.getIndexId()) {
            addIntReplacement(base + "/IndexId", after.getIndexId(), reps);
        }
        if (before.getNextDataId() != after.getNextDataId()) {
            addIntReplacement(base + "/NextDataId", after.getNextDataId(), reps);
        }
        if (before.getX() != after.getX()) {
            addAxisReplacement(base + "/Position/x", after.getX(), reps);
        }
        if (before.getZ() != after.getZ()) {
            addAxisReplacement(base + "/Position/z", after.getZ(), reps);
        }
        String bp = before.getParamRaw() == null ? "" : before.getParamRaw();
        String ap = after.getParamRaw() == null ? "" : after.getParamRaw();
        if (!bp.equals(ap)) {
            LevelJsonByteSpans.Span sp = spans.getScalarSpan(base + "/Param");
            if (sp == null) {
                throw new java.io.IOException("找不到 Param 字段 span: " + base);
            }
            reps.add(new LevelJsonByteSpans.Replacement(sp.start, sp.end,
                LevelJsonByteSpans.encodeJsonStringToken(ap, MAPPER)));
        }
        if (reps.isEmpty()) {
            return;
        }
        byte[] next = LevelJsonByteSpans.applyReplacements(rawUtf8, reps);
        MAPPER.readTree(next);
        Files.write(file.toPath(), next);
        reloadFromWrittenBytes(next);
    }

    private void addIntReplacement(String pathKey, int newValue, List<LevelJsonByteSpans.Replacement> reps)
        throws java.io.IOException {
        LevelJsonByteSpans.Span sp = spans.getScalarSpan(pathKey);
        if (sp == null) {
            throw new java.io.IOException("找不到字段 span: " + pathKey);
        }
        byte[] nv = LevelJsonByteSpans.formatIntToken(newValue, rawUtf8, sp);
        reps.add(new LevelJsonByteSpans.Replacement(sp.start, sp.end, nv));
    }

    private void addAxisReplacement(String pathKey, int newValue, List<LevelJsonByteSpans.Replacement> reps)
        throws java.io.IOException {
        LevelJsonByteSpans.Span sp = spans.getScalarSpan(pathKey);
        if (sp == null) {
            throw new java.io.IOException("找不到字段 span: " + pathKey);
        }
        byte[] nv = LevelJsonByteSpans.formatAxisToken(newValue, rawUtf8, sp);
        reps.add(new LevelJsonByteSpans.Replacement(sp.start, sp.end, nv));
    }

    /**
     * 在父节点 {@code children} 数组中插入子对象（字节级拼接），写盘后重建树与索引
     */
    public void saveInsertChild(int parentFlatIndex, int insertIndex, ObjectNode newChildJson, MapViewerPanel panel)
        throws Exception {
        if (file == null) {
            throw new IllegalStateException("file 为空");
        }
        if (rawUtf8 == null || spans == null) {
            throw new IllegalStateException("当前文档未带原文缓存，无法局部保存");
        }
        if (parentFlatIndex < 0 || parentFlatIndex >= flatPaths.size()) {
            throw new IllegalArgumentException("parentFlatIndex 越界");
        }
        String parentKey = LevelJsonByteSpans.pathKey(flatPaths.get(parentFlatIndex));
        LevelJsonByteSpans.ChildrenMeta meta = spans.getChildrenMeta(parentKey);
        if (meta == null) {
            throw new java.io.IOException("找不到父节点 children 区域: " + parentKey);
        }
        String eol = LevelJsonByteSpans.detectEol(rawUtf8);
        String bracePrefix;
        String fieldPrefix;
        if (meta.childObjectSpans.isEmpty()) {
            String lineInd = LevelJsonByteSpans.lineIndentBefore(rawUtf8, meta.openBracket);
            bracePrefix = lineInd + "    ";
            fieldPrefix = bracePrefix + "    ";
        } else if (insertIndex <= 0) {
            LevelJsonByteSpans.Span first = meta.childObjectSpans.get(0);
            bracePrefix = LevelJsonByteSpans.braceLineIndentOfObject(rawUtf8, first.start);
            fieldPrefix = bracePrefix + "    ";
        } else if (insertIndex >= meta.childObjectSpans.size()) {
            LevelJsonByteSpans.Span last = meta.childObjectSpans.get(meta.childObjectSpans.size() - 1);
            bracePrefix = LevelJsonByteSpans.braceLineIndentOfObject(rawUtf8, last.start);
            fieldPrefix = bracePrefix + "    ";
        } else {
            LevelJsonByteSpans.Span ref = meta.childObjectSpans.get(insertIndex);
            bracePrefix = LevelJsonByteSpans.braceLineIndentOfObject(rawUtf8, ref.start);
            fieldPrefix = bracePrefix + "    ";
        }
        byte[] newObj = LevelJsonByteSpans.formatNewNodeObject(newChildJson, eol, bracePrefix, fieldPrefix, MAPPER);
        byte[] next = LevelJsonByteSpans.insertChildObject(rawUtf8, meta, insertIndex, newObj, eol);
        try {
            // 写盘后重建树与索引
            MAPPER.readTree(next);
            Files.write(file.toPath(), next);
            reloadFromWrittenBytes(next);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "保存失败: " + e.getMessage());
        }
    }

    /**
     * 按扁平下标删除节点：从父级的 {@code nodes} 或 {@code children} 数组中移除该 JSON 对象（含整棵子树），字节级写回后重建树与索引。
     */
    public void saveRemoveNode(int flatIndex) throws Exception {
        if (file == null) {
            throw new IllegalStateException("file 为空");
        }
        if (rawUtf8 == null || spans == null) {
            throw new IllegalStateException("当前文档未带原文缓存，无法局部保存");
        }
        if (flatIndex < 0 || flatIndex >= flatPaths.size()) {
            throw new IllegalArgumentException("flatIndex 越界");
        }
        ArrayList<Integer> path = flatPaths.get(flatIndex);
        if (path.isEmpty()) {
            throw new IllegalArgumentException("节点路径为空");
        }
        String parentKey;
        int removeIndex;
        if (path.size() == 1) {
            parentKey = LevelJsonByteSpans.ROOT_NODES_ARRAY_META_KEY;
            removeIndex = path.get(0);
        } else {
            ArrayList<Integer> parentPath = new ArrayList<>(path.subList(0, path.size() - 1));
            parentKey = LevelJsonByteSpans.pathKey(parentPath);
            removeIndex = path.get(path.size() - 1);
        }
        LevelJsonByteSpans.ChildrenMeta meta = spans.getChildrenMeta(parentKey);
        if (meta == null) {
            throw new java.io.IOException("找不到父级数组 meta: " + parentKey);
        }
        if (removeIndex < 0 || removeIndex >= meta.childObjectSpans.size()) {
            throw new java.io.IOException("子节点索引与 meta 不一致: " + removeIndex);
        }
        byte[] next = LevelJsonByteSpans.removeChildObject(rawUtf8, meta, removeIndex);
        MAPPER.readTree(next);
        Files.write(file.toPath(), next);
        reloadFromWrittenBytes(next);
    }

    private void reloadFromWrittenBytes(byte[] next) throws Exception {
        this.rawUtf8 = next;
        this.root = MAPPER.readTree(next);
        this.spans = LevelJsonByteSpans.build(next, MAPPER);
        rebuildFlat();
    }

    public File getFile() {
        return file;
    }

    public JsonNode getRoot() {
        return root;
    }

    /**
     * 与 DFS 顺序一致的节点列表（可修改 Bean 字段，再通过 {@link #syncBeanToJson(int, LevelNodeBean)} 写回 JSON）。
     */
    public List<LevelNodeBean> getFlatBeans() {
        return flatBeans;
    }

    public List<ObjectNode> getFlatJson() {
        return flatJson;
    }

    /**
     * 全树中最大的 DataId，用于分配新节点 id。
     */
    public int findMaxDataId() {
        int max = 0;
        for (ObjectNode jsonNodes : flatJson) {
            int d = jsonNodes.path("DataId").asInt(0);
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    /**
     * 创建子节点 JSON：包含 Position、业务字段与从模板复制的 Scale/Rotation；若模板为 null 则使用默认 1/0。
     */
    public static ObjectNode createDefaultChildNode(LevelNodeBean bean, ObjectNode templateFrom) {
        ObjectNode n = JsonNodeFactory.instance.objectNode();
        applyBeanToJson(bean, n);
        if (templateFrom != null) {
            JsonNode sc = templateFrom.get("Scale");
            if (sc != null) {
                n.set("Scale", sc.deepCopy());
            } else {
                n.set("Scale", defaultScale());
            }
            JsonNode rot = templateFrom.get("Rotation");
            if (rot != null) {
                n.set("Rotation", rot.deepCopy());
            } else {
                n.set("Rotation", defaultRotation());
            }
        } else {
            n.set("Scale", defaultScale());
            n.set("Rotation", defaultRotation());
        }
        n.set("children", JsonNodeFactory.instance.arrayNode());
        return n;
    }

    private static ObjectNode defaultScale() {
        ObjectNode s = JsonNodeFactory.instance.objectNode();
        s.put("x", 1.0);
        s.put("y", 1.0);
        s.put("z", 1.0);
        return s;
    }

    private static ObjectNode defaultRotation() {
        ObjectNode r = JsonNodeFactory.instance.objectNode();
        r.put("x", 0.0);
        r.put("y", 0.0);
        r.put("z", 0.0);
        return r;
    }

    /**
     * 从同一 BelongId 的已有节点中取一条作为模板，填充 TextId、IndexId、NextDataId、Param（不含坐标与类型）。
     */
    public void fillBeanFromSameBelongId(LevelNodeBean target, int belongId) {
        for (LevelNodeBean o : flatBeans) {
            if (o.getBelongId() == belongId) {
                target.setTextId(o.getTextId());
                target.setIndexId(o.getIndexId());
                target.setNextDataId(o.getNextDataId());
                target.setParamRaw(o.getParamRaw() == null ? "" : o.getParamRaw());
                return;
            }
        }
    }

    /**
     * 父节点下已有子节点数量。
     */
    public static int getChildCount(ObjectNode parent) {
        JsonNode ch = parent.get("children");
        if (ch == null || !ch.isArray()) {
            return 0;
        }
        return ch.size();
    }
}
