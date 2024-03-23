package chatgptserver.bean.dto.XunFeiXingHuo.imageCreate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Choices {
    private Integer status;
    private Integer seq;
    private List<Text> text;

    public Choices(List<Text> text) {

    }
}
