//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;

@Converter(
        generateLoader = true
)
public final class FileConverter {
    private FileConverter() {
    }

    @Converter
    public static File genericToFile(GenericFile<File> genericFile, Exchange exchange) throws IOException {
        Object body = genericFile.getBody();
        File file;
        if (body instanceof byte[]) {
            byte[] bos = (byte[])((byte[])body);
            String destDir = System.getProperty("java.io.tmpdir");
            file = new File(destDir, genericFile.getFileName());
            if (!file.getCanonicalPath().startsWith(destDir)) {
                throw new IOException("File is not jailed to the destination directory");
            }

            Files.write(file.toPath(), bos, new OpenOption[]{StandardOpenOption.CREATE});
            file.deleteOnExit();
        } else {
            file = (File)body;
        }

        return file;
    }
}
