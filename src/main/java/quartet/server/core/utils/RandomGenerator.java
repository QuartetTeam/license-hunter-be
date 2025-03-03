package quartet.server.core.utils;

import java.util.List;

public interface RandomGenerator {
    // 주어진 정수 범위에서 임의의 정수를 반환
    int nextInt(final int origin, final int bound);

    // 주어진 리스트에서 임의의 요소 1개를 골라 반환
    <T> T getRandomItem(final List<T> items);
}