package basepackage.stand.standbasisprojectonev1.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class PagedResponse<T> {

    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedResponse() {

    }

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.data = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    
}
