package pl.com.bottega.commons.math.utilits.func;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;


public class NonEmptyList<T> implements FunList<T> {

    private T head;
    private FunList<T> tail;

    public NonEmptyList(T element) {
        head = element;
        tail = new EmptyList<>();
    }

    public NonEmptyList(T head, FunList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public FunList<T> add(T element) {
        return new NonEmptyList<>(head, tail.add(element));
    }

    @Override
    public FunList<T> remove(T element) {
        return head.equals(element) ? tail : new NonEmptyList<>(head, tail.remove(element));
    }

    @Override
    public FunList<T> filter(Predicate<T> predicate) {
        return predicate.test(head) ? new NonEmptyList<>(head, tail.filter(predicate)) : tail.filter(predicate);
    }

    @Override
    public void each(Consumer<T> consumer) {
        consumer.accept(head);
        tail.each(consumer);
    }

    @Override
    public FunList<T> concat(FunList<T> source) {
        return new NonEmptyList<>(head, tail.concat(source));
    }

    @Override
   public FunList<T> sublist(int startIndex, int endIndex) {
        if(endIndex <= 0 || startIndex < 0) return new EmptyList<>();
        return startIndex == 0 ? new NonEmptyList<>(head, tail.sublist(startIndex, --endIndex)) : tail.sublist(--startIndex, --endIndex);
    }

    @Override
    public boolean contains(T element) {
        return head.equals(element) || tail.contains(element);
    }

    @Override
    public int size() {
        return 1 + tail.size();
    }

    @Override
    public T find(Predicate<T> predicate) {
        return predicate.test(head) ? head : tail.find(predicate);
    }

    @Override
    public T get(int i) {
        return i == 0 ? head : getElementFromTail(i);
    }

    private T getElementFromTail(int i) {
        return tail.get(i - 1);
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public <R> FunList<R> map(Function<T, R> mapper) {
        return new NonEmptyList<R>(mapper.apply(head), tail.map(mapper));
    }

    @Override
    public <R> R reduce(R initial, BiFunction<R, T, R> reducer) {
        R partialResult = reducer.apply(initial, head);
        return tail.reduce(partialResult, reducer);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NonEmptyList)) return false;
        NonEmptyList funList = (NonEmptyList) object;
        return head.equals(funList.head) && tail.equals(funList.tail);
    }

    public String toString() {
        return tail.isEmpty() ? head.toString() : head.toString() + ", " + tail.toString();
    }

}
