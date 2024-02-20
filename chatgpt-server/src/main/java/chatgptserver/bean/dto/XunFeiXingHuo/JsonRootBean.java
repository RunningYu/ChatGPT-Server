package chatgptserver.bean.dto.XunFeiXingHuo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JsonRootBean {

    private Header header;

    private Parameter parameter;

    private Payload payload;

}
