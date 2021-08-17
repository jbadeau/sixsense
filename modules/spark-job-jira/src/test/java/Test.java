import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.types.StructType;
import org.zalando.spark.jsonschema.SchemaConverter;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        try {
            StructType schemea = SchemaConverter.convertContent(FileUtils.readFileToString(new File("D:\\git\\sixsense\\modules\\schemas\\src\\main\\resources\\jira\\json-schema\\jira.issue.strict.json"), "UTF-8"));
            String schema = schemea.json();
            StructType.fromJson(schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
