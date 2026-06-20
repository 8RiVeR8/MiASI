package com.project.youtlix.contentlibrary.infrastructure.in.web;

/**
 * Request body for adding a season to a series.
 */
public record SeasonRequest(int number, String title) {
}
