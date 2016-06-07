package pl.com.bottega.commons.math.utilits.func;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public class EmptyList<T> implements FunList<T> {
    @Override
    public FunList<T> add(T el) {
        return new NonEmptyList<>(el);
    }

    @Override
    public FunList<T> remove(T el) {
        return this;
    }

    @Override
    public FunList<T> filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public void each(Consumer<T> consumer) {
        return;
    }

    @Override
    public FunList<T> concat(FunList<T> source) {
        return source;
    }

    @Override
    public FunList<T> sublist(int startIndex, int endIndex) {
        return new EmptyList<>();
    }

    @Override
    public boolean contains(T element) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public T find(Predicate<T> predicate) {
        return null;
    }

    @Override
    public T get(int i) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    public String toString() {
        return "";
    }

    @Override
    public <R> FunList<R> map(Function<T, R> mapper) {
        return new EmptyList<>();
    }

    @Override
    public <R> R reduce(R initial, BiFunction<R, T, R> reducer) {
        return initial;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof FunList && ((FunList) object).isEmpty();
    }

}