package com.backend.allreva.common.web.response;

import java.util.List;

public record SliceResponse<T, C>(List<T> items, C nextCursor) {
}
