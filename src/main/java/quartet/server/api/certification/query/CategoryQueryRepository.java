package quartet.server.api.certification.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static quartet.server.domain.category.model.QMainCategory.mainCategory;
import static quartet.server.domain.category.model.QSubCategory.subCategory;
import static quartet.server.domain.member.model.QMemberCategory.memberCategory;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Long> getDefaultSubCategoryId(final long mainCategoryId) {
        return Optional.ofNullable(queryFactory
                .select(subCategory.id)
                .from(subCategory)
                .where(subCategory.mainCategory.id.eq(mainCategoryId))
                .fetchFirst());
    }

    public List<Long> findInterestedCategoryIds(final Long memberId) {
        return queryFactory
                .select(memberCategory.mainCategory.id)
                .from(memberCategory)
                .where(memberCategory.member.id.eq(memberId))
                .fetch();
    }

    public Long getDefaultRecommendedCategoryId() {
        return queryFactory
                .select(subCategory.id)
                .from(subCategory)
                .join(subCategory.mainCategory, mainCategory)
                .where(mainCategory.id.eq(23L))
                .orderBy(subCategory.id.asc())
                .limit(1)
                .fetchOne();
    }
}
