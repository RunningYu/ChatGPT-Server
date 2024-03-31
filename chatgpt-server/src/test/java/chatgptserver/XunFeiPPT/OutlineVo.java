package chatgptserver.XunFeiPPT;

import java.util.List;

public class OutlineVo {
    // 标题
    private String title;
    // 副标题
    private String subTitle;
    // 章节
    private List<Chapter> chapters;
    // 结尾（约定为空）
    private String end = "";

    // 章节结构体
    public static class Chapter {
        // 章节名
        String chapterTitle;
        // 章节二级标题
        List<String> chapterContent;
    }
}