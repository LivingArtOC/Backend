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
    private long fullCount; //전체 수
    private long totalCount; //검색된 수
    private int page;
    private int size;
    private boolean last;
    private List<T> data;
}

