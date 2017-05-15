package com.application.services.views;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.services.R;
import com.application.services.data.Booking;
import com.application.services.utils.CommonLib;
import com.application.services.utils.RequestWrapper;

import java.util.ArrayList;
import java.util.List;

public class Home extends Activity {

    private ListView mList;
    private boolean destroyed;
    private Context mContext;
    private BookingAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init views
        mContext = this;
        mList = (ListView) findViewById(R.id.book_list);

        new GetOfflineBookings().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    private class GetOfflineBookings extends AsyncTask<Object, Void, Object> {

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progress_container).setVisibility(View.VISIBLE);
            findViewById(R.id.content).setAlpha(1f);
            findViewById(R.id.content).setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            super.onPreExecute();
        }

        // execute the api
        @Override
        protected Object doInBackground(Object... params) {
            try {
                CommonLib.ZLog("API RESPONSER", "CALLING GET WRAPPER");
                String url = CommonLib.SERVER + "booking/offlineBookings?";
                Object info = RequestWrapper.RequestHttp(url, RequestWrapper.MY_BOOKINGS, RequestWrapper.FAV);
                CommonLib.ZLog("url", url.toString());
                return info;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (destroyed)
                return;

            findViewById(R.id.progress_container).setVisibility(View.GONE);

            if (result != null) {
                findViewById(R.id.content).setVisibility(View.VISIBLE);
                if (result instanceof ArrayList<?>) {
                    setBookings((ArrayList<Booking>) result);
                    if (((ArrayList<Booking>) result).size() == 0) {
                        findViewById(R.id.content).setVisibility(View.GONE);
                        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.empty_view_text)).setText("Nothing here yet");
                    } else {
                        findViewById(R.id.content).setVisibility(View.VISIBLE);
                        findViewById(R.id.empty_view).setVisibility(View.GONE);
                    }
                }
            } else {
                if (CommonLib.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, getResources().getString(R.string.error_try_again),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_internet_message), Toast.LENGTH_SHORT)
                            .show();

                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);

                    findViewById(R.id.content).setVisibility(View.GONE);
                }
            }
        }
    }

    private void setBookings (ArrayList<Booking> bookings) {
        mAdapter = new BookingAdapter(mContext, R.layout.list_layout, bookings);
        mList.setAdapter(mAdapter);
    }

    protected static class ViewHolder {
        TextView bookingId, email, status, time;
    }

    private class BookingAdapter extends ArrayAdapter<Booking> {

        private List<Booking> wishes;

        public BookingAdapter(Context context, int resourceId, ArrayList<Booking> wishes) {
            super(context.getApplicationContext(), resourceId, wishes);
            this.wishes = wishes;
        }

        @Override
        public int getCount() {
            if (wishes == null) {
                return 0;
            } else {
                return wishes.size();
            }
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(Home.this).inflate(R.layout.list_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.bookingId = (TextView)convertView.findViewById(R.id.bookingId);
                viewHolder.email = (TextView)convertView.findViewById(R.id.email);
                viewHolder.status = (TextView)convertView.findViewById(R.id.status);
                viewHolder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Booking wish = wishes.get(position);
            viewHolder.bookingId.setText(wish.getBookingId());
            viewHolder.email.setText(wish.getEmail());
            viewHolder.status.setText(wish.getStatus());
            viewHolder.time.setText(wish.getTime());
            return convertView;
        }
    }
}



