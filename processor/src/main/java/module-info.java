module io.github.kaktushose.jdac.processor {
    requires java.compiler;
    requires jdk.compiler;
    requires java.desktop;

    exports io.github.kaktushose.jdac.processor.property;

    provides javax.annotation.processing.Processor with io.github.kaktushose.jdac.processor.property.ValidationProcessor;
}