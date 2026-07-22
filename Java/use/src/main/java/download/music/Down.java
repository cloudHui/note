package download.music;

import stu.FileUtils;

/**
 * 下载 http://p.hh.hi.cn/%E9%9F%B3%E9%A2%91/%E7%BD%91%E6%98%93%E4%BA%91%E8%8B%B1%E6%96%87%E6%AD%8CTOP100%20mp3
 */
public class Down {

    private static final String URL = "http://p.hh.hi.cn/%E9%9F%B3%E9%A2%91/%E7%BD%91%E6%98%93%E4%BA%91%E8%8B%B1%E6%96%87%E6%AD%8CTOP100%20mp3";

    public static void main(String[] args) {

        String page = downPage();
        FileUtils.writeFileAppendsUtf8(System.getProperty("user.dir") + "/page.html", page, false);
    }

    /**
     * 下载文件
     */
    private static String downPage() {
        String result = "";
        try {
            result = DownPage.downloadWebPage(Down.URL).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
