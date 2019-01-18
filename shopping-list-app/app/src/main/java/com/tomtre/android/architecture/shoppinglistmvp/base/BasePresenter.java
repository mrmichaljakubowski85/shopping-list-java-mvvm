package com.tomtre.android.architecture.shoppinglistmvp.base;

public interface BasePresenter<T> {

    /**
     * Binds presenter with a view. If the view is a Fragment, it should be called in onViewCreated().
     * It guarantees onActivityResult will be correctly handled in the Presenter.
     *
     * @param view the view associated with this presenter
     */
    void takeView(T view);

    /**
     * The Presenter will perform initialization here.
     */
    void start();

    /**
     * Drops the reference to the view when destroyed.
     * If the view is a Fragment, it should be called in onDestroyView().
     */
    void dropView();

}
