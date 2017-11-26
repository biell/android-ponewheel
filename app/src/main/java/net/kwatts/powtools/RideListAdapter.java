package net.kwatts.powtools;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import net.kwatts.powtools.database.RideRow;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideViewHolder> {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    private final Context context;
    private final List<RideRow> rideRows;

    RideListAdapter(Context context, List<RideRow> rideRows) {
        this.context = context;
        this.rideRows = rideRows;
    }
    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ridesListRow = LayoutInflater.from(context).inflate(R.layout.rides_list_row, parent, false);

        return new RideViewHolder(ridesListRow);
    }

    @Override
    public void onBindViewHolder(RideViewHolder holder, int position) {
        RideRow rideRow = rideRows.get(position);

        holder.bind(rideRow);
    }

    @Override
    public int getItemCount() {
        return rideRows.size();
    }

    public List<RideRow> getRideList() {
        return rideRows;
    }

    class RideViewHolder extends RecyclerView.ViewHolder {

        private final TextView dateView;
        private final TextView rideLengthView;

        RideViewHolder(View itemView) {
            super(itemView);

            dateView = itemView.findViewById(R.id.rides_row_date);
            rideLengthView = itemView.findViewById(R.id.rides_row_length);
        }

        void bind(RideRow rideRow) {
            System.out.println("rideId" + rideRow.rideId + " minDate= " + rideRow.minEventDate + " max=" + rideRow.maxEventDate);
            // TODO only show id in debug builds?
            if (rideRow.getMinDate() != null) {
                dateView.setText(SIMPLE_DATE_FORMAT.format(rideRow.getMinDate())+ " ("+rideRow.rideId + ")");
            }
            rideLengthView.setText(rideRow.getMinuteDuration()+" " );

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, MapActivity.class);
                //intent.putExtra(MapActivity.FILE_NAME, rideRow.getName());
                context.startActivity(intent);
            });
        }
    }

}