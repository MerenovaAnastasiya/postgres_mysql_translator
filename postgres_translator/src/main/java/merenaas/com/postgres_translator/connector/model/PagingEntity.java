package merenaas.com.postgres_translator.connector.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@ToString
@RequiredArgsConstructor
public class PagingEntity<T> {

    @Getter
    private final List<T> result;
    private final boolean hasNext;

    public boolean hasNext() {
        return hasNext;
    }

    public boolean isEmpty() {
        return result.isEmpty();
    }

    public Optional<T> last() {
        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(result.size() - 1));
    }

    public Optional<T> first() {
        return result.isEmpty()
                ? Optional.empty()
                : Optional.of(result.get(0));
    }
}
