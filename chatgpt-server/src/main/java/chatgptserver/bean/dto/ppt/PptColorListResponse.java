package chatgptserver.bean.dto.ppt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PptColorListResponse {

    private Boolean flag;

    private Integer code;

    private String desc;

    private String count;

    private List<PptColor> data;

}
