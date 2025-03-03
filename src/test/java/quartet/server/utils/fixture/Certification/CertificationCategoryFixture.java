package quartet.server.utils.fixture.Certification;

import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.domain.category.model.Category;
import java.util.List;

public class CertificationCategoryFixture {
    public static Category parentCategory(){
        return Category.of("IT", null);
    }

    public static Category subCategory(){
        return Category.of("데이터분석",parentCategory());
    }
    public static List<CertificationCategoriesResponse> categoryResList(){
        return List.of(
                new CertificationCategoriesResponse(1L,"정보통신"),
                new CertificationCategoriesResponse(2L, "데이터분석")
        );
    }
}
