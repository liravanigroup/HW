package pl.com.bottega.commons.math.utilits.func;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public interface FunList<T> {
    FunList<T> add(T el);

    boolean contains(T el);

    int size();

    T find(Predicate<T> predicate);

    T get(int i);

    boolean isEmpty();

    <R> FunList<R> map(Function<T, R> mapper);

    <R> R reduce(R initial, BiFunction<R, T, R> reducer);

    static <T> FunList<T> create() {
        return new EmptyList<>();
    }

    FunList<T> remove(T el);

    FunList<T> filter(Predicate<T> predicate);

    void each(Consumer<T> consumer);

    FunList<T> concat(FunList<T> other);

    FunList<T> sublist(int startIndex, int endIndex);
}
