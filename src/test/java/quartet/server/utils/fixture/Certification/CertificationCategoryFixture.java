package quartet.server.utils.fixture.Certification;

import quartet.server.api.certification.dto.response.CertificationCategoriesResponse;
import quartet.server.domain.category.model.MainCategory;
import quartet.server.domain.category.model.SubCategory;
import java.util.List;

public class CertificationCategoryFixture {
    public static MainCategory mainCategory() {
        MainCategory mainCategory = MainCategory.of("IT", true);
        return mainCategory;
    }

    public static SubCategory subCategory(MainCategory mainCategory) {
        SubCategory subCategory = SubCategory.of("데이터분석", mainCategory);
        return subCategory;
    }

    public static List<CertificationCategoriesResponse> categoryResList() {
        return List.of(
                new CertificationCategoriesResponse(1L, "IT", CertificationCategoriesResponse.CategoryType.MAIN),
                new CertificationCategoriesResponse(2L, "데이터분석", CertificationCategoriesResponse.CategoryType.SUB)
        );
    }
}
