package core.container.vo;

public class ContainerRWContext {
    private String topic;
    private String kafkaBrokerIp;
    private int partition;

    public String getKafkaBrokerIp() { return kafkaBrokerIp; }

    public void setKafkaBrokerIp(String kafkaBrokerIp) { this.kafkaBrokerIp = kafkaBrokerIp; }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }
}
