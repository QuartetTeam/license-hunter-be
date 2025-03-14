package quartet.server.utils.fixture.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class PageableFixture {
    public static Pageable pageable(){
        return PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")));
    }
    public static Pageable pageable(int page, int pageSize, Sort sort) {
        return PageRequest.of(page, pageSize, sort);
    }
    public static Pageable pageable(int pageSize) { return PageRequest.of(0, pageSize); }
}
