package chatgptserver.bean.ao.ppt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PptKindResponseAO {

    private String firstKind;

    private List<String> secondKinds;

    public PptKindResponseAO(String firstKind) {
        this.firstKind = firstKind;
    }


}
