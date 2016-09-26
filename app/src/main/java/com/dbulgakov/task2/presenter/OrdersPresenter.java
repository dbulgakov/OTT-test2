package com.dbulgakov.task2.presenter;

import android.os.Bundle;

import com.dbulgakov.task2.model.pojo.UserOrder;
import com.dbulgakov.task2.other.App;
import com.dbulgakov.task2.other.Const;
import com.dbulgakov.task2.predicate.ActiveOrderPredicate;
import com.dbulgakov.task2.predicate.OtherOrdersPredicate;
import com.dbulgakov.task2.view.fragments.OrdersView;
import com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;

public class OrdersPresenter extends BasePresenter{

    private OrdersView ordersView;
    private static final String SAVED_USER_ORDERS_KEY = "SAVED_USER_ORDERS_KEY";
    private static final int USER_ID = 1;
    private Predicate<UserOrder> userOrderPredicate;

    @Inject
    @Named(Const.UI_THREAD)
    Scheduler uiThread;

    @Inject
    @Named(Const.IO_THREAD)
    Scheduler ioThread;

    @Inject
    public OrdersPresenter() {
    }

    public OrdersPresenter(OrdersView ordersView) {
        super();
        App.getComponent().inject(this);
        this.ordersView = ordersView;
    }

    private void getUserOrdersFromBackend(){
        Subscription subscription = model.getUserOrders(USER_ID)
                .flatMap(Observable::from)
                .filter(userOrderPredicate::apply)
                .toSortedList((userOrder, userOrder2) -> userOrder.getDepartureAt().compareTo(userOrder2.getDepartureAt()))
                .subscribeOn(ioThread)
                .observeOn(uiThread)
                .subscribe(new Observer<List<UserOrder>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ordersView.showError(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<UserOrder> userOrder) {
                        ordersView.setOrderList(userOrder);
                    }
                });
        addSubscription(subscription);
    }

    public void onSaveInstanceState(Bundle outState){
        List<UserOrder> userOrderList = ordersView.getCurrentUserOrderList();
        if (userOrderList != null && !userOrderList.isEmpty()) {
            outState.putSerializable(SAVED_USER_ORDERS_KEY, new ArrayList<>(userOrderList));
        }
    }

    public void getUserOrders(Bundle savedInstanceState) {
        ordersView.startSwipeRefreshing();
        if (savedInstanceState != null) {
            @SuppressWarnings("unchecked")
            List<UserOrder> userOrderList= (List<UserOrder>) savedInstanceState.getSerializable(SAVED_USER_ORDERS_KEY);
            ordersView.setOrderList(userOrderList);
        } else {
            ordersView.clearOrderList();
            getUserOrdersFromBackend();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt(Const.FRAGMENT_KEY) == Const.FRAGMENT_ACTIVE) {
                userOrderPredicate = new ActiveOrderPredicate();
            } else {
                userOrderPredicate = new OtherOrdersPredicate();
            }
        } else {
            throw new IllegalStateException("Fragment type needs to be passed!");
        }
    }

    public void getUserOrders() {
        getUserOrders(null);
    }
}
