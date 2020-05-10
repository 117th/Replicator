package core.constants;

import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;

public class Constants {

    public static class AppmasterConstants {
        public static final String APPMASTER_HOST = "APPMASTER_HOST";
    }

    public static class ContainerConstants {
        public static final String CONTAINER_TOPIC = "CONTAINER_TOPIC";
        public static final String TOPIC_PARTITION = "TOPIC_PARTITION";
    }

    public static class HBaseConstants {
        public static final byte[] COLUMN_FAMILY = "cf".getBytes();
        public static final ColumnFamilyDescriptor CF_DESCRIPTOR = ColumnFamilyDescriptorBuilder.of(COLUMN_FAMILY);
    }

}
