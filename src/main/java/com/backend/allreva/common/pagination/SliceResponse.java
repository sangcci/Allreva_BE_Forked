package com.backend.allreva.common.pagination;

import java.util.List;

public record SliceResponse<T, C>(List<T> items, C nextCursor) {}
