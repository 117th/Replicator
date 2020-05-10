package core.kafka.serdeser;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class SpecificRecordDeser<T extends SpecificRecord> implements Deserializer<T> {

    final private Class<T> recordClass;

    public SpecificRecordDeser(Class<T> recordClass) {
        this.recordClass = recordClass;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        SpecificDatumReader specificDatumReader = new SpecificDatumReader(this.recordClass);

        SpecificRecord specificRecord = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);

            DataFileStream<T> dataFileStream = new DataFileStream<>(is, specificDatumReader);
            specificRecord = dataFileStream.next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return (T) specificRecord;
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return deserialize(topic, data);
    }

    @Override
    public void close() {

    }
}
