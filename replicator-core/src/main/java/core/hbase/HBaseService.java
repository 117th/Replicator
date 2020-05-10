package core.hbase;

import core.constants.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class HBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseService.class);

    private Connection connection;
    private Map<TableName, BufferedMutator> bufferedMutatorMap;

    public HBaseService() throws Exception {
        Configuration hbaseConfiguration = HBaseConfiguration.create();
        HBaseAdmin.available(hbaseConfiguration);

        connection = ConnectionFactory.createConnection(hbaseConfiguration);
    }

    public void addMutation(TableName tableName, Mutation mutation) throws Exception {

        if(!isTableExists(tableName)) createTable(tableName);

        bufferedMutatorMap.putIfAbsent(tableName, connection.getBufferedMutator(tableName));

        BufferedMutator tableBufferedMutator = bufferedMutatorMap.get(tableName);
        tableBufferedMutator.mutate(mutation);
    }

    public void flush() {
        for(Map.Entry<TableName, BufferedMutator> entry : bufferedMutatorMap.entrySet()) {
            try {
                entry.getValue().flush();
            } catch (IOException e) {
                LOGGER.error("Table {}: buffered mutator failed to flush. Will retry in next batch", entry.getKey().getNameAsString());
            }
        }
    }

    private boolean isTableExists(TableName tableName) throws IOException {
        return connection.getAdmin().tableExists(tableName);
    }

    private void createTable(TableName tableName) throws IOException {

        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(tableName);
        tableBuilder.setColumnFamily(Constants.HBaseConstants.CF_DESCRIPTOR);
        tableBuilder.setRegionReplication(2);
        //valuable for big tables (more than 500 mb) to improve durability and performance
        tableBuilder.setCompactionEnabled(true);

        connection.getAdmin().createTable(tableBuilder.build());
    }
}
