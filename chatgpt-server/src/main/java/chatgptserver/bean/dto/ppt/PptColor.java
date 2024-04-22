package chatgptserver.bean.dto.ppt;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Data
public class PptColor {

    /**
     * 主题名
     */
    private String key;

    private String name;

    /**
     * 缩略图
     */
    private String thumbnail;

}
