package com.gmail.bishoybasily.media.publisher.utility;

/**
 * Created by bishoy on 8/12/16.
 *
 * @param <T>
 */
public class Data<T> {

    private final T t;

    public Data(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

}
