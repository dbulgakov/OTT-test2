package com.dbulgakov.task2.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbulgakov.task2.R;
import com.dbulgakov.task2.model.pojo.UserOrder;
import com.dbulgakov.task2.other.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<UserOrder> userOrderList;
    private DateFormat shortDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    private DateFormat fullDate = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());

    public OrderListAdapter(List<UserOrder> orderList) {
        userOrderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Стандартная практика - передать context в конструктор и один раз проинициализировать
        // layoutInflater, сделав его членом класса.
        // В onCreate уже тогда можно использовать созданный инфлейтер и не создавать его каждый раз
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserOrder userOrder = userOrderList.get(position);
        setOrderInfoIntoTextViews(userOrder, holder);
    }

    @Override
    public int getItemCount() {
        return userOrderList.size();
    }

    public List<UserOrder> getUserOrderList() {
        return userOrderList;
    }

    public void addUserOrder(UserOrder userOrder) {
        userOrderList.add(userOrder);
        notifyDataSetChanged();
    }

    public void setUserOrderList(List<UserOrder> userOrderList) {
        this.userOrderList = userOrderList;
        notifyDataSetChanged();
    }

    public void clearData() {
        userOrderList.clear();
        notifyDataSetChanged();
    }

    private void setOrderInfoIntoTextViews(UserOrder userOrder, ViewHolder holder) {
        holder.orderDateTextView.setText(getOrderDateString(userOrder));
        holder.tripDirectionTextView.setText(String.format("%s - %s", userOrder.getOrigin(), userOrder.getDestination()));
        holder.flightNumberTextView.setText(userOrder.getFlightNumber());
        holder.flightTakeoffTextView.setText(fullDate.format(userOrder.getDepartureAt()));
        holder.flightArrivalTextView.setText(fullDate.format(userOrder.getArrivalAt()));
        holder.flightDurationTextView.setText(userOrder.getFlightDuration());
        holder.flightStopNumberTextView.setText(String.format(Locale.getDefault(), "%d", userOrder.getStopNumber()));
    }

    private String getOrderDateString(UserOrder userOrder) {
        String orderDateString = shortDate.format(userOrder.getOrderDate());
        if (userOrder.getIfUserCancel()) {
            orderDateString = orderDateString.concat(App.getContext().getString(R.string.order_cancelled_substring));
        }
        return orderDateString;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView orderDateTextView;
        final TextView tripDirectionTextView;
        final TextView flightNumberTextView;
        final TextView flightTakeoffTextView;
        final TextView flightArrivalTextView;
        final TextView flightDurationTextView;
        final TextView flightStopNumberTextView;

        ViewHolder(View v) {
            super(v);
            // Вы здесь решили Butterknife не использовать?
            orderDateTextView = (TextView) v.findViewById(R.id.order_date_text_view);
            tripDirectionTextView = (TextView) v.findViewById(R.id.order_trip_direction_text_view);
            flightNumberTextView = (TextView) v.findViewById(R.id.order_flight_number_text_view);
            flightTakeoffTextView = (TextView) v.findViewById(R.id.order_flight_takeoff_time_text_view);
            flightArrivalTextView = (TextView) v.findViewById(R.id.order_flight_landing_time_text_view);
            flightDurationTextView = (TextView) v.findViewById(R.id.order_flight_duration_time_text_view);
            flightStopNumberTextView = (TextView) v.findViewById(R.id.order_flight_stop_number_text_view);
        }
    }
}
