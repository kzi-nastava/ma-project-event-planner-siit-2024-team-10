package m3.eventplanner.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.viewmodel.HomeScreenViewModel;

public class HomeScreenViewModelFactory implements ViewModelProvider.Factory {
    private final ClientUtils clientUtils;

    public HomeScreenViewModelFactory(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeScreenViewModel(clientUtils);
    }
}
