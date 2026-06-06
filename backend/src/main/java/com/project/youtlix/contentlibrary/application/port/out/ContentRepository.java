package com.project.youtlix.contentlibrary.application.port.out;

import com.project.youtlix.contentlibrary.domain.model.*;
import java.util.List;
import java.util.Optional;

/** Output port for storing and querying library content. */
public interface ContentRepository {
    void save(Content content);
    Optional<Content> ofId(ContentId id);
    List<Content> matching(SearchCriteria criteria);
    List<Content> byKeyword(String phrase);
    List<Content> page(Page page);
    void remove(ContentId id);
}
