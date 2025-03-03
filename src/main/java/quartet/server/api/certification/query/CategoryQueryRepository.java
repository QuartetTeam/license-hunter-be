package quartet.server.api.certification.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import quartet.server.domain.category.model.QCategory;
import quartet.server.domain.member.model.QMemberCategory;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Long> getDefaultSubCategoryId(final long categoryId){
        QCategory category = QCategory.category;

        // TODO 최지희: default 서브 카테고리 선정 기준 논의 필요
        return Optional.ofNullable(queryFactory
                    .select(category.id)
                    .from(category)
                    .where(category.parentCategory.id.eq(categoryId))
                    .fetchFirst());
    }

    public List<Long> findInterestedCategoryIds(final long memberId){
        QMemberCategory memberCategory = QMemberCategory.memberCategory;

        return queryFactory
                .select(memberCategory.category.id)
                .from(memberCategory)
                .where(memberCategory.member.id.eq(memberId))
                .fetch();
    }

    public Long getDefaultRecommendedCategoryId(){
        QCategory category = QCategory.category;

        // TODO 최지희: 디폴트 추천 자격증 카테고리 선정 기준 논의 필요
        return queryFactory
                .select(category.id)
                .from(category)
                .fetchFirst();
    }
}
