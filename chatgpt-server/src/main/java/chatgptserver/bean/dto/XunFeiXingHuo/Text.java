package chatgptserver.bean.dto.XunFeiXingHuo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:29
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Text {

    private String role;
    private String content;
    private String content_type;

    public Text (String role) {
        this.role = role;
    }

}
