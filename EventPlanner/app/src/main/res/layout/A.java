package layout;

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/reserve_screen"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".fragments.reservation.CreateReservationFragment">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="12dp"
        android:gravity="center"
        android:text="@string/reserve_a_service"
        android:textAppearance="@style/TextAppearance.AppCompat.Display1"
        android:textColor="?attr/colorPrimary" />

    <TextView
        android:id="@+id/service_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Reserving for service: "
        android:layout_marginLeft="8dp"
        android:textSize="16sp" />

    <TextView
        android:id="@+id/reservation_period"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Reservation period: "
        android:layout_marginTop="8dp"
        android:layout_marginLeft="8dp" />

    <Spinner
        android:id="@+id/event_spinner"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginStart="10dp"
        android:layout_marginTop="16dp"
        android:text="@string/choose_event"
        android:tooltipText="@string/choose_event"
        android:contentDescription="@string/choose_event" />

    <TextView
        android:id="@+id/event_date"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/event_date"
        android:layout_marginTop="8dp"
        android:visibility="gone"
        android:layout_marginLeft="8dp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_marginTop="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Start Time: "
                android:layout_marginEnd="8dp" />

            <TextView
                android:id="@+id/selected_start_time"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Not selected"
                android:textStyle="bold" />

            <Button
                android:id="@+id/select_start_time_button"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Select"
                style="@style/Widget.MaterialComponents.Button.TextButton" />
        </LinearLayout>

        <!-- End Time Section -->
        <LinearLayout
            android:id="@+id/end_time_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginTop="8dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="End Time: "
                android:layout_marginEnd="8dp" />

            <TextView
                android:id="@+id/selected_end_time"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Not selected"
                android:textStyle="bold" />

            <Button
                android:id="@+id/select_end_time_button"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Select"
                style="@style/Widget.MaterialComponents.Button.TextButton" />
        </LinearLayout>

        <TextView
            android:id="@+id/duration_info"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:textStyle="italic" />
    </LinearLayout>
    <TextView
        android:id="@+id/error"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:textStyle="italic"
        android:textColor="@color/vibrant_red"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center">

        <Button
            android:id="@+id/submit"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/submit" />

        <Button
            android:id="@+id/cancel"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/cancel"
            android:layout_marginLeft="20dp"
            android:backgroundTint="@color/brand_purple_light"
            android:textColor="@color/white" />
    </LinearLayout>

</LinearLayout>