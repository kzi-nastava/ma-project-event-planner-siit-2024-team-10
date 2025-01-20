package m3.eventplanner.fragments.offering;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import m3.eventplanner.R;
import m3.eventplanner.models.Offering;

public class OfferingDetailsFragment extends Fragment {

    private Offering offering;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offering = getArguments().getParcelable("offering");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_offering_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button edit = view.findViewById(R.id.edit_offering_button);
        edit.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(OfferingDetailsFragment.this);
            navController.navigate(R.id.editOfferingFragment);
        });

        Button delete = view.findViewById(R.id.delete_offering_button);
        delete.setOnClickListener(v -> showDeleteDialog());
    }
    private void showDeleteDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.conformation_dialog, null);

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> dialog.dismiss());
    }
}