package m3.eventplanner.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import m3.eventplanner.models.PagedResponse;

public abstract class PaginatedViewModel<T> extends ViewModel {
    protected final MutableLiveData<PagedResponse<T>> pagedData = new MutableLiveData<>();
    protected final MutableLiveData<String> error = new MutableLiveData<>();
    protected int currentPage = 0;
    protected int totalPages = 0;

    public LiveData<PagedResponse<T>> getPagedData() {
        return pagedData;
    }

    public LiveData<String> getError() {
        return error;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public abstract void fetchPage(int page);
}

