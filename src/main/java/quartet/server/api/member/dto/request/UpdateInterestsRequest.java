package quartet.server.api.member.dto.request;

import java.util.List;

public record UpdateInterestsRequest(List<Long> categoryIds) {}