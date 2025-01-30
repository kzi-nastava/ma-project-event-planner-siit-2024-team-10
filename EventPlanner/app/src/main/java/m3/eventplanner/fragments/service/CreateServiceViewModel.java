package m3.eventplanner.fragments.service;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import m3.eventplanner.clients.ClientUtils;
import m3.eventplanner.models.CreateServiceDTO;
import m3.eventplanner.models.CreatedServiceDTO;
import m3.eventplanner.models.GetOfferingCategoryDTO;
import m3.eventplanner.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateServiceViewModel extends ViewModel {
    private final MutableLiveData<List<GetOfferingCategoryDTO>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private ClientUtils clientUtils;
    private final MutableLiveData<List<String>> uploadedImagePaths = new MutableLiveData<>();

    public LiveData<List<String>> getUploadedImagePaths() {
        return uploadedImagePaths;
    }
    public LiveData<String> getError() {
        return error;
    }
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<GetOfferingCategoryDTO>> getCategories() {
        return categories;
    }

    public void initialize(ClientUtils clientUtils) {
        this.clientUtils = clientUtils;
    }

    public void loadCategories(){
        clientUtils.getCategoryService().getCategories().enqueue(new Callback<Collection<GetOfferingCategoryDTO>>() {
            @Override
            public void onResponse(Call<Collection<GetOfferingCategoryDTO>> call, Response<Collection<GetOfferingCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.setValue(response.body().stream()
                            .filter(category->!(category.isPending()||category.isDeleted()))
                            .collect(Collectors.toList()));
                } else {
                    error.setValue("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<Collection<GetOfferingCategoryDTO>> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error loading categories");
            }
        });
    }

    public void createService(CreateServiceDTO service) {
        System.out.println("Creating service: " + service);
        clientUtils.getServiceService().addService(service).enqueue(new Callback<CreatedServiceDTO>() {
            @Override
            public void onResponse(Call<CreatedServiceDTO> call, Response<CreatedServiceDTO> response) {
                if (response.isSuccessful()) {
                    successMessage.setValue("Service created successfully");
                } else {
                    error.setValue("Failed to create product");
                }
            }

            @Override
            public void onFailure(Call<CreatedServiceDTO> call, Throwable t) {
                error.setValue(t.getMessage() != null ? t.getMessage() : "Error creating service");
            }
        });
    }
    public void uploadImages(List<Uri> imageUris, Context context) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (Uri uri : imageUris) {
            try {
                File file = FileUtils.getFile(context, uri);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("files",
                        file.getName(), requestFile);
                parts.add(part);
            } catch (IOException e) {
                error.setValue("Error preparing image: " + e.getMessage());
                return;
            }
        }

        clientUtils.getImageUploadService().uploadImages(parts).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    uploadedImagePaths.setValue(response.body());
                } else {
                    error.setValue("Failed to upload images");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                error.setValue("Error uploading images: " + t.getMessage());
            }
        });
    }
}
