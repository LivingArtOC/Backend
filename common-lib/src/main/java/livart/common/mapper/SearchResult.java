package livart.common.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult<T> {
    private long totalCount; //검색된 수
    private int page;
    private int size;
    private List<T> data;
}

