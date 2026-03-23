package io.github.kaktushose.jdac.components.pagination;

import io.github.kaktushose.jdac.components.pagination.layout.ControlRow;
import io.github.kaktushose.jdac.components.pagination.layout.Dynamic;
import io.github.kaktushose.jdac.components.pagination.layout.Static;

public sealed interface PaginationLayout permits ControlRow, Dynamic, Static { }
