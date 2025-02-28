package quartet.server.domain.category.exception;

import quartet.server.core.code.CategoryErrorCode;

public class SubCategoryNotFoundException extends CategoryException{
    public SubCategoryNotFoundException(){super(CategoryErrorCode.SUB_CATEGORY_NOT_FOUND);}
}
