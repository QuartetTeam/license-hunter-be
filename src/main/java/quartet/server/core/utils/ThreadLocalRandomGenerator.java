package quartet.server.core.utils;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ThreadLocalRandomGenerator implements RandomGenerator {

    @Override
    public int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @Override
    public <T> T getRandomItem(List<T> items){
        if (items == null || items.isEmpty()) {
            return null;
        }

        List<T> nullRemovedList = items.stream()
            .filter(Objects::nonNull)
            .toList();

        if (nullRemovedList.isEmpty()) return null;

        int randomIndex = ThreadLocalRandom.current().nextInt(items.size());
        return items.get(randomIndex);
    }
}
