module io.github.kaktushose.jdac.processor {
    requires java.compiler;
    requires jdk.compiler;
    requires java.desktop;
    requires com.palantir.javapoet;

    exports io.github.kaktushose.jdac.processor.property.api;

    provides javax.annotation.processing.Processor with io.github.kaktushose.jdac.processor.property.ValidationProcessor,
            io.github.kaktushose.jdac.processor.property.ListCreatorProcessor;
}
