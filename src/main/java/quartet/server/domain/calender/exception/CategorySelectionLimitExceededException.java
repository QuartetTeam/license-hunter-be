package quartet.server.domain.calender.exception;

import quartet.server.core.code.CategoryErrorCode;
import quartet.server.domain.category.exception.CategoryException;

public class CategorySelectionLimitExceededException extends CategoryException {
    public CategorySelectionLimitExceededException() {
        super(CategoryErrorCode.CATEGORY_SELECTION_LIMIT_EXCEEDED);
    }
}