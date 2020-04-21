package com.example.marsplay.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity <V extends MvpView, P extends MvpPresenter<V>> extends AppCompatActivity
{
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Object lastPresenter = getLastCustomNonConfigurationInstance();
        if (lastPresenter != null) {
            mPresenter = (P) lastPresenter;
        } else {
            mPresenter = createMvpPresenter();
        }
    }


    /**
     * Binding view to presenter
     */
    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.bindView(getMvpView());
    }

    /**
     * Unbinding view from presenter
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unbindView();
    }

    /**
     * If activity is going to be destroyed we will save mPresenter and get it in recreated activity
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    /**
     * @return new instance of presenter. This is fabric method. We need it because in MvpActivity
     * we don't know exactly class of Presenter and we cannot create instance of generic class "P"
     */
    protected abstract P createMvpPresenter();

    /**
     * @return instance of MvpView interface. In MvpActivity class we bind View to presenter (in
     * onResume method) and caused this we need to have ability to get instance of MvpView
     */
    protected abstract V getMvpView();
}
