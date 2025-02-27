package quartet.server.utils.fixture;

import quartet.server.api.certification.dto.response.CertificationCategoriesRes;
import quartet.server.domain.category.model.Category;
import java.util.List;

public class CertificationCategoryFixture {

    public static List<CertificationCategoriesRes> categoryResList(){
        return List.of(
                new CertificationCategoriesRes(1L,"정보통신"),
                new CertificationCategoriesRes(2L, "데이터분석")
        );
    }
}
