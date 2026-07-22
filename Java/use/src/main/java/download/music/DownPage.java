package download.music;


import download.URLDownLoad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从 一个地址下载网页下来 然后找到 href 和 htm 中间的部分 这是地址 然后用 head 拼接 这是下载地址 带 search 的部分是下一页的请求拼接网址 需要拼接 head
 * <p>
 * 存到 D:\BaiduNetdiskDownload\music
 */
public class DownPage {

    private static final String HEAD = "https://www.hifini.com.cn/search-";
    private static final String PATH = "D:/BaiduNetdiskDownload/music/";

    private static final String FILE_END = ".m4a";

    static ThreadPoolExecutor executor = new ThreadPoolExecutor(32, 32, 1, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        down();
    }

    private static void down() {
        Set<HifiniMusic> musics = new HashSet<>();
        Set<HifiniMusic> fail = new HashSet<>();
        // 获取音乐信息 名称和保存地址
        getMusicInfo(musics, "天路");
        for (HifiniMusic value : musics) {
            doDownload(value, fail);
        }
        while (executor.getCompletedTaskCount() != musics.size()) {
        }
        System.out.println("fail：" + fail.size() + " 条");
        for (HifiniMusic value : fail) {
            System.out.println(value.toString());
        }
        System.out.println("success：" + (musics.size() - fail.size()) + " 条");
        for (HifiniMusic value : musics) {
            if (!fail.contains(value)) {
                System.out.println(value.toString());
            }
        }
        executor.shutdown();
    }

    /**
     * 反转
     */
    private static String revertAuthorName(String name) {
        char[] chars = name.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int size = chars.length, star = 3, index = 0; index < size; index++) {
            try {
                sb.append(chars[star]);
                if (++star >= size) {
                    star = 0;
                    sb.append(" - ");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 获取音乐信息 名称和保存地址
     */
    private static void getMusicInfo(Set<HifiniMusic> musics, String musicName) {
        String curr;
        try {
            curr = URLEncoder.encode(musicName, "UTF-8");
            curr = curr.replace("%", "_");
            // https://www.hifini.com.cn/search-_E5_A4_A9_E8_B7_AF.htm
            // https://www.hifini.com.cn/%E5%A4%A9%E8%B7%AF.htm
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String downLoadUrl = HEAD + curr + ".htm";
        List<String> nextPage = new ArrayList<>();
        // 保存音乐信息
        saveMusicInfo(downLoadUrl, musics, nextPage, true, curr);

        for (String value : nextPage) {
            // 保存音乐信息
            saveMusicInfo(value, musics, nextPage, false, curr);
        }
        System.out.println("一共:" + musics.size() + " 条");
    }

    /**
     * 真正的下载
     */
    private static void doDownload(HifiniMusic music, Set<HifiniMusic> fail) {
        executor.execute(() -> {
            boolean downloadFile = false;
            try {
                String downLoadUrl = music.getDownUrl();
                String page = downPage(downLoadUrl);
                int startIndex = page.indexOf("url");
                int endGenerateParam = page.indexOf("generateParam");
                int endPic = page.indexOf("pic");
                if (startIndex != -1 && endPic != -1) {
                    if (endGenerateParam == -1) {
                        downLoadUrl = page.substring(startIndex + 6, endPic - 16);
                         downloadFile = URLDownLoad.downloadFile(downLoadUrl, music.getSavePath());
                    } else {
                        String bUrl = page.substring(startIndex + 6, endGenerateParam - 4);
                        String param = page.substring(endGenerateParam + 15, endPic - 17);
                        String callParam = JavaCallJS.callJs(param);
                        downLoadUrl = HEAD + bUrl + callParam;
                        // downloadFile = URLDownLoad.downloadFile(getLocation(downLoadUrl), music.getSavePath());
                    }
                    music.setDownUrl(downLoadUrl);
                    downloadFile = true;
                }
            } catch (Exception e) {
                // e.printStackTrace();
            } finally {
                if (!downloadFile) {
                    fail.add(music);
                }
            }
        });
    }

    /**
     * 保存音乐信息
     *
     * @param downLoadUrl  下载地址
     * @param musics       音乐信息保存集合
     * @param nextPage     下页信息
     * @param saveNextPage 是否保存下页信息
     * @param curr         当前搜索加密
     */
    private static void saveMusicInfo(String downLoadUrl, Set<HifiniMusic> musics, List<String> nextPage,
                                      boolean saveNextPage, String curr) {
        String page = downPage(downLoadUrl);
        List<String> matcher = extractSubstringBetweenChars(page, "<a", "/a>");
        List<String> result;
        String name;
        for (String value : matcher) {
            if (value.contains("thread")) {
                // 提取从startChar到endChar之间的内容
                result = extractSubstringBetweenChars(value, "\"", "\"");
                if (!result.isEmpty()) {
                    // 提取中文 反转
                    name = extractChinese(value);
                    if (name.length() <= 3) {
                        name += extractSubstringBetweenChars(value, "《", "》").get(0);
                    } else {
                        name = revertAuthorName(name);
                    }
                    musics.add(new HifiniMusic(name, HEAD + result.get(0).replace("\"", ""), PATH + name + FILE_END));
                }
            } else {
                if (saveNextPage && value.contains(curr)) {
                    // 提取从startChar到endChar之间的内容
                    result = extractSubstringBetweenChars(value, "\"", "\"");
                    if (!result.isEmpty()) {
                        nextPage.add(HEAD + result.get(0).replace("\"", ""));
                    }
                }
            }
        }
    }

    /**
     * 提取从startChar到endChar之间的内容
     *
     * @param input     总内容
     * @param startChar 字符开头
     * @param endChar   字符结尾
     */
    public static List<String> extractSubstringBetweenChars(String input, String startChar, String endChar) {
        List<String> array = new ArrayList<>();
        // String regex = "[\\u4e00-\\u9fa5]+";//匹配中文
        String regex = Pattern.quote(startChar) + "(.*?)" + Pattern.quote(endChar);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            array.add(matcher.group());
        }
        // if (matcher.find()) {
        // //return matcher.group(); //找到第一个就返回
        // }

        return array;
    }

    /**
     * 提取中文
     *
     * @param input 总内容
     */
    public static String extractChinese(String input) {
        StringBuilder sb = new StringBuilder();
        String regex = "[\\u4e00-\\u9fa5]+";// 匹配中文
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            sb.append(matcher.group());
        }
        return sb.toString();
    }

    /**
     * 下载文件
     */
    private static String downPage(String url) {
        String result = "";
        try {
            result = downloadWebPage(url).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从地址 urlString 下载文件保存成文件地址 outputPath
     *
     * @param urlString  下载地址
     * @param outputPath 文件地址
     */
    public static void downloadWebPage(String urlString, String outputPath) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * 从地址 urlString 下载文件保存成String
     *
     * @param urlString 下载地址
     */
    public static String downloadWebPage(String urlString) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        URL url = new URL(urlString);
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        }

        return contentBuilder.toString();
    }

    /**
     * 获取重定向
     */
    public static String getLocation(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置跟随重定向
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            // 发起请求
            connection.connect();

            // 检查响应码
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM) {
                // 获取重定向的URL
                return connection.getHeaderField("Location");
            }
            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
