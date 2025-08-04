package m3.eventplanner.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import m3.eventplanner.models.GetCalendarItemDTO;

public class CalendarItemAdapter extends RecyclerView.Adapter<CalendarItemAdapter.CalendarItemViewHolder> {

    private List<GetCalendarItemDTO> calendarItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GetCalendarItemDTO calendarItem);
    }

    public CalendarItemAdapter(List<GetCalendarItemDTO> calendarItems, OnItemClickListener listener) {
        this.calendarItems = calendarItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CalendarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new CalendarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarItemViewHolder holder, int position) {
        GetCalendarItemDTO calendarItem = calendarItems.get(position);
        holder.bind(calendarItem, listener);
    }

    @Override
    public int getItemCount() {
        return calendarItems.size();
    }

    public static class CalendarItemViewHolder extends RecyclerView.ViewHolder {
        TextView title, time;

        public CalendarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            time = itemView.findViewById(android.R.id.text2);
        }

        public void bind(GetCalendarItemDTO calendarItem, OnItemClickListener listener) {
            title.setText(calendarItem.getTitle());

            String type = calendarItem.getType();
            String timeText;

            if ("CREATED_EVENT".equals(type) || "ACCEPTED_EVENT".equals(type)) {
                timeText = "All day";
            } else {
                try {
                    LocalDateTime start = LocalDateTime.parse(calendarItem.getStartTime());
                    LocalDateTime end = LocalDateTime.parse(calendarItem.getEndTime());
                    timeText = start.toLocalTime() + " - " + end.toLocalTime();
                } catch (DateTimeParseException e) {
                    timeText = "Invalid time";
                }
            }

            time.setText(timeText);
            itemView.setBackgroundColor(getCalendarItemColor(calendarItem.getType()));
            itemView.setOnClickListener(v -> listener.onItemClick(calendarItem));
        }

        private int getCalendarItemColor(String itemType) {
            switch (itemType) {
                case "ACCEPTED_EVENT":
                    return Color.parseColor("#F8D7DA");
                case "CREATED_EVENT":
                    return Color.parseColor("#D4EDDA");
                case "RESERVATION":
                    return Color.parseColor("#D6EAF8");
                default:
                    return Color.BLACK;
            }
        }
    }

    public void updateCalendarItems(List<GetCalendarItemDTO> newItems) {
        Collections.sort(newItems, new Comparator<GetCalendarItemDTO>() {
            @Override
            public int compare(GetCalendarItemDTO o1, GetCalendarItemDTO o2) {
                boolean o1AllDay = isAllDay(o1);
                boolean o2AllDay = isAllDay(o2);

                // Put all-day events first
                if (o1AllDay && !o2AllDay) return -1;
                if (!o1AllDay && o2AllDay) return 1;

                // If both are not all-day, sort by start time
                if (!o1AllDay && !o2AllDay) {
                    try {
                        LocalDateTime start1 = LocalDateTime.parse(o1.getStartTime());
                        LocalDateTime start2 = LocalDateTime.parse(o2.getStartTime());
                        return start1.compareTo(start2);
                    } catch (DateTimeParseException e) {
                        return 0; // Keep original order on parse error
                    }
                }

                // If both are all-day, maintain original order
                return 0;
            }

            private boolean isAllDay(GetCalendarItemDTO item) {
                String type = item.getType();
                return "CREATED_EVENT".equals(type) || "ACCEPTED_EVENT".equals(type);
            }
        });
        this.calendarItems = newItems;
        notifyDataSetChanged();
    }

}