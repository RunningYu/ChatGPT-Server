package chatgptserver.bean.dto.XunFeiXingHuo.pptCreate;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/15
 */
@Data
public class ChapterContents {

    private String chartFlag = "false";

    private String searchFlag = "false";

    private String fileUrl = "";

    private Integer id;

    private String chapterTitle;

    private Integer fileType = 0;

}
