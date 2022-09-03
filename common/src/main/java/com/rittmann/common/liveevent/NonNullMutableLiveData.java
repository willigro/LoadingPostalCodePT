package com.rittmann.common.liveevent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class NonNullMutableLiveData<T> extends MutableLiveData<T> {

    private final @NonNull T initialValue;

    public NonNullMutableLiveData(@NonNull T initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public void postValue(@NonNull T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(@NonNull T value) {
        super.setValue(value);
    }

    @NonNull
    @Override
    public T getValue() {
        //the only way value can be null is if the value hasn't been set yet.
        //for the other cases the set and post methods perform nullability checks.
        T value = super.getValue();
        return value != null ? value : initialValue;
    }

    //convenience method
    //call this method if T is a collection and you modify it's content
    public void notifyContentChanged() {
        setValue(getValue());
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, observer);
    }
}
