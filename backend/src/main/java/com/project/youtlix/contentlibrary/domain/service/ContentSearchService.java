package com.project.youtlix.contentlibrary.domain.service;

import com.project.youtlix.contentlibrary.domain.model.Content;
import com.project.youtlix.contentlibrary.domain.model.Page;
import com.project.youtlix.contentlibrary.domain.model.SearchCriteria;
import java.util.List;

/** Domain service representing PU5-PU7 browsing, search and filtering rules. */
public class ContentSearchService {
    /** Returns already selected content page; repository performs actual selection in this skeleton. */
    public List<Content> browse(Page page, List<Content> content) { return content; }
    /** Returns already selected search result; repository performs actual matching in this skeleton. */
    public List<Content> searchByKeyword(String phrase, List<Content> content) { return content; }
    /** Returns already selected filter result; repository performs actual matching in this skeleton. */
    public List<Content> filter(SearchCriteria criteria, List<Content> content) { return content; }
}
