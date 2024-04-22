package chatgptserver.utils;

import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.ChapterContents;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.Chapters;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import chatgptserver.enums.CharacterConstants;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
public class PptUtils {

    public static String buildOutLineMD(PptOutlineResponse outline) {
        StringBuffer outlineStr = new StringBuffer();
        String tile = outline.getTitle();
        String subTile = outline.getSubTitle();
        outlineStr.append("**主标题：**" + tile + "\n");
        outlineStr.append("**副标题：**" + subTile + "\n");
        outlineStr.append(CharacterConstants.BOUNDARY_LINE + "\n");
        List<Chapters> chapters = outline.getChapters();
        for (int i = 0; i < chapters.size(); i ++) {
            Chapters chapter = chapters.get(i);
            String chapterTitle = chapter.getChapterTitle();
            outlineStr.append(chapterTitle + "\n");
            List<ChapterContents> chapterContents = chapter.getChapterContents();
            for (ChapterContents chapterContent : chapterContents) {
                outlineStr.append(CharacterConstants.BLACK_SPOTS + chapterContent.getChapterTitle() + "\n");
            }
            if (i < chapters.size() - 1) {
                outlineStr.append("\n\n\n");
            }
        }
        System.out.println("————————————————————————————");
        System.out.println(outlineStr);
        System.out.println("————————————————————————————");
        return outlineStr + "";
    }

}
