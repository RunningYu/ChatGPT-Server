package chatgptserver.bean.dto.XunFeiXingHuo.pptCreate;

import lombok.Data;

import java.sql.Blob;
import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/15
 */
@Data
public class Chapters {

    private Boolean chartFlag = false;

    private Boolean searchFlag = false;

    private List<ChapterContents> chapterContents;

    private String fileUrl = "";

    private Integer id = 12345678;

    private String chapterTitle;

    private Integer fileType = 0;

}
