package chatgptserver.bean.dto.XunFeiXingHuo.imageCreate;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */

@Data
public class Text {
    private String content;
    private Integer index;
    private String role;

    public Text (String role, String content) {
        this.role = role;
        this.content = content;
    }

}
