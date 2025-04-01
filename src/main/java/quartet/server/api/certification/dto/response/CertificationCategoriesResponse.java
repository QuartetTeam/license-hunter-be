package quartet.server.api.certification.dto.response;

public record CertificationCategoriesResponse(
        long id,
        String name,
        CategoryType type
) {
    public enum CategoryType {
        MAIN, SUB
    }
}
