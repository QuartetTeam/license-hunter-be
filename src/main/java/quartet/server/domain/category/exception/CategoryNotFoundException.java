package quartet.server.domain.category.exception;

import quartet.server.core.code.CategoryErrorCode;

public class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException() {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }
}