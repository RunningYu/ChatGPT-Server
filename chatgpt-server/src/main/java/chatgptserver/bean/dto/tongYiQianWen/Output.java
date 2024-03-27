package chatgptserver.bean.dto.tongYiQianWen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Output {

    private List<Choices> choices;

}
