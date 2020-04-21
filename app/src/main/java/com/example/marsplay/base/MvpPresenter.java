package com.example.marsplay.base;

public interface MvpPresenter<ViewClass extends MvpView>
{

    void bindView(ViewClass view);

    void unbindView();
}
