package messageHandler.impl;

import core.constants.Constants;
import core.container.vo.ContainerRWContext;
import core.hbase.HBaseService;
import core.kafka.vo.DataMessage;
import messageHandler.KafkaService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.yarn.server.api.ContainerContext;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@EnableScheduling
public class BasicMessageHandler extends KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicMessageHandler.class);

    public BasicMessageHandler(ContainerRWContext context) {
        super.init(context);
    }

    protected void handleMessage(ConsumerRecord<byte[], DataMessage> message) throws Exception {
        super.handleMessage(message);

        DataMessage dataMessage = message.value();

        TableName table = TableName.valueOf(dataMessage.getTable().toString());
        String operation = dataMessage.getOperation().toString();
        byte[] hashedKey = saltKey(dataMessage.getRecordKey().toString());

        switch(operation) {
            case "INSERT":
                Put put = new Put(hashedKey);

                GenericRecord gr = parseRecord(dataMessage.getData().array());
                Schema schema = gr.getSchema();
                for(Schema.Field field : schema.getFields()) {
                    put.addColumn(Constants.HBaseConstants.COLUMN_FAMILY, field.name().getBytes(),
                            gr.get(field.name()).toString().getBytes(StandardCharsets.UTF_8));
                }
                hBaseService.addMutation(table, put);
                break;
            case "DELETE":
                Delete delete = new Delete(hashedKey);
                hBaseService.addMutation(table, delete);
                break;
            default:
                throw new RuntimeException("Unknown operation for message: " + operation);
        }

        if(++messaggessProcessed >= 300) {
            hBaseService.flush();
            messaggessProcessed = 0;
        }
    }

    private byte[] saltKey(String key) {
        BigInteger bigInt = BigInteger.valueOf(key.hashCode());
        return bigInt.toByteArray();
    }
}
