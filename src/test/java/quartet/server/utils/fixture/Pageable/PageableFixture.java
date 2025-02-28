package quartet.server.utils.fixture.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


public class PageableFixture {
    public static Pageable pageable(){
        return PageRequest.of(0, 10);
    }
    public static Pageable pageable(int page, int pageSize) {
        return PageRequest.of(page, pageSize);
    }
}
