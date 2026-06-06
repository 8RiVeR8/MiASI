package com.project.youtlix.contentlibrary.application.port.in;

import com.project.youtlix.contentlibrary.domain.model.ContentId;
import com.project.youtlix.contentlibrary.domain.model.Metadata;

/** Command for PU9 modifying content metadata. */
public record UpdateMetadataCommand(ContentId contentId, Metadata metadata) {}
