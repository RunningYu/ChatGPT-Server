package chatgptserver.bean.ao.ppt;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/5/19
 */
@Data
public class PptScoreRequestAO {

    private String pptCode;

    private Double score1 = 0.0;

    private Double score2 = 0.0;

    private Double score3 = 0.0;

}
