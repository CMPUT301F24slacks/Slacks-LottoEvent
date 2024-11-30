package com.example.slacks_lottoevent;

/**
 * Callback interface for asynchronous tasks.
 *
 * @param <T> The type of the result of the asynchronous task
 */
public interface Callback<T> {
    void onComplete(T result);
}
