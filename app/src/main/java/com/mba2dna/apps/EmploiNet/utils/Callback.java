package com.mba2dna.apps.EmploiNet.utils;

public interface Callback<T> {

    void onSuccess(T result);

    void onError(String result);

}
