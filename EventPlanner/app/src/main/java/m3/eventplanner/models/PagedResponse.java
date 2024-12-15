package m3.eventplanner.models;

import java.util.Collection;

public class PagedResponse<T>{
    private Collection<T> content;
    private int totalPages;
    private long totalElements;

    public PagedResponse(Collection<T> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public Collection<T> getContent() {
        return content;
    }

    public void setContent(Collection<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
