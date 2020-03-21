package appmaster;

import core.constants.Constants;
import core.container.vo.ContainerRWContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.yarn.am.ContainerLauncherInterceptor;
import org.springframework.yarn.am.StaticEventingAppmaster;
import org.springframework.yarn.am.allocate.ContainerAllocateData;
import org.springframework.yarn.am.allocate.DefaultContainerAllocator;
import org.springframework.yarn.am.container.AbstractLauncher;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReplicatorAppmaster extends StaticEventingAppmaster implements ContainerLauncherInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicatorAppmaster.class);
    private ConcurrentHashMap<ContainerId, Container> containers = new ConcurrentHashMap<>();
    private Queue<ContainerRWContext> containersContexts = new ConcurrentLinkedQueue<>();
    private InetAddress localhost;

    @Override
    protected void onInit() throws Exception {
        LOGGER.info("Initializing application master...");
        super.onInit();
        ((AbstractLauncher) getLauncher()).addInterceptor(this);

        localhost = InetAddress.getLocalHost();

        //createAllocationGroup();
        prepareContainersToRun();

        LOGGER.info("Successfully added interceptor and got localhost {}", localhost);
    }

    private void createAllocationGroup() {
        DefaultContainerAllocator allocator = (DefaultContainerAllocator) getAllocator();
        allocator.setAllocationValues("RW-CONTAINER", 1, null, 1, 128, null);
    }

    private void allocateContainers(int count, String group) {
        LOGGER.info("Allocating {} containers", count);

        ContainerAllocateData containerAllocateData = new ContainerAllocateData();
        containerAllocateData.addAny(count);
        containerAllocateData.setId(group);
        getAllocator().allocateContainers(containerAllocateData);

        LOGGER.info("Allocated {} containers", count);
    }

    private void prepareContainersToRun() {

        ContainerRWContext rwContext = new ContainerRWContext();
        rwContext.setTopic("TOPIC");
        rwContext.setPartition(0);

        containersContexts.add(rwContext);

        //I hope this will work when we got to submitApplication(). Otherwise we should move realization in our class
        getParameters().setProperty("container-count", String.valueOf(containersContexts.size()));
    }

    @Override
    public void submitApplication() {
        super.submitApplication();
        //allocateContainers(1, "RW-CONTAINER");
    }

    //intercept for adding localhost (I hope this works)
    //Main idea: we creating RWContexts to determine topics and partitions. After that we allocating containers and in this method we running container with right consumer
    @Override
    public ContainerLaunchContext preLaunch(Container container, ContainerLaunchContext launchContext) {
        LOGGER.info("Starting container {}", container.getId());
        ContainerId id = container.getId();

        ContainerRWContext rwContext = containersContexts.poll();

        if(rwContext == null) {
            LOGGER.info("Got null RWContext, should ignore it");
            return launchContext;
        }

        containers.put(id, container);

        Map<String, String> enviroment = new HashMap<>(launchContext.getEnvironment());
        enviroment.put(Constants.AppmasterConstants.APPMASTER_HOST, localhost.getHostAddress());
        enviroment.put(Constants.ContainerConstants.CONTAINER_TOPIC, rwContext.getTopic());
        enviroment.put(Constants.ContainerConstants.TOPIC_PARTITION, String.valueOf(rwContext.getPartition()));

        launchContext.setEnvironment(enviroment);

        return launchContext;
    }

    @Override
    protected void onContainerAllocated(Container container) {
        LOGGER.info("Container {} allocated", container.getId());
        super.onContainerAllocated(container);
    }

    @Override
    protected void onContainerCompleted(ContainerStatus status) {
        LOGGER.info("Container {} completed", status.getContainerId());
    }

    @Override
    protected void onContainerLaunched(Container container) {
        LOGGER.info("Container {} launched", container.getId());
        super.onContainerLaunched(container);
    }

}
