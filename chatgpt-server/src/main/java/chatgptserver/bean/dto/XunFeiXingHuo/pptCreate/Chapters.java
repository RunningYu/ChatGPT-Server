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

    private Boolean chartFlag;

    private Boolean searchFlag;

    private List<ChapterContents> chapterContents;

    private String fileUrl;

    private Integer id;

    private String chapterTitle;

    private Integer fileType;

}
