package com.project.youtlix.contentlibrary.infrastructure.in.web;

/**
 * Request body for adding or updating a season.
 */
public record SeasonRequest(int number, String title) {
}
