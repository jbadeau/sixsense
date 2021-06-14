//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import java.io.File;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.DeferredContextBinding;
import org.apache.camel.TypeConverterLoaderException;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.spi.TypeConverterLoader;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.support.SimpleTypeConverter;
import org.apache.camel.support.SimpleTypeConverter.ConversionMethod;

@DeferredContextBinding
public final class FileConverterLoader implements TypeConverterLoader, CamelContextAware {
    private CamelContext camelContext;

    public FileConverterLoader() {
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public CamelContext getCamelContext() {
        return this.camelContext;
    }

    public void load(TypeConverterRegistry registry) throws TypeConverterLoaderException {
        this.registerConverters(registry);
    }

    private void registerConverters(TypeConverterRegistry registry) {
        addTypeConverter(registry, File.class, GenericFile.class, false, (type, exchange, value) -> {
            return FileConverter.genericToFile((GenericFile)value, exchange);
        });
    }

    private static void addTypeConverter(TypeConverterRegistry registry, Class<?> toType, Class<?> fromType, boolean allowNull, ConversionMethod method) {
        registry.addTypeConverter(toType, fromType, new SimpleTypeConverter(allowNull, method));
    }
}
