package io.github.kaktushose.jdac.processor.property;

import com.sun.source.tree.NewClassTree;

import javax.lang.model.element.VariableElement;

public record Property(VariableElement element,
                       NewClassTree initializer,
                       String name,
                       String type,
                       String category,
                       String fallbackBehaviour,
                       String stage) { }
