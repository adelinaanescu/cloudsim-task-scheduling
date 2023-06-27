package BCO;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;
import utils.GenerateWorkloadSpecificationMatrix;

import java.text.DecimalFormat;
import java.util.*;

public class BCO_Scheduler {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static BCO BCOSchedulerInstance;
    private static double mapping[];
    private static double[][] commMatrix;
    private static double[][] execMatrix;
    private static double[][] vmMatrix;
    private static double[][] cloudletMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        // VM Parameters
        long size; // image size (MB)
        int ram; // vm memory (MB)
        int mips;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        // create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            mips = (int) vmMatrix[i][0];
            ram = (int) vmMatrix[i][1];
            size = (long) vmMatrix[i][3];
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }


    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        // cloudlet parameters
        long fileSize; // MB
        long outputSize; // MB
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        long length; // MI (Million Instructions)

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (mapping[i]);
            length = (long) cloudletMatrix[i][0];
            fileSize = (long) cloudletMatrix[i][1];
            outputSize = (long) cloudletMatrix[i][2];
            long cloudletLength = length * (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, cloudletLength, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(dcId + 2);
            list.add(cloudlet[i]);
        }

        return list;
    }
    private static List<Vm> createHomogenousVM(int userId, int vms) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 20000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 500;
        long bw = 3500;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }

    private static List<Cloudlet> createHomogenousCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long fileSize = 5000;
        long outputSize = 250;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (mapping[i]);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }


    public static void main(String[] args) {
        Log.printLine("Starting BCO Scheduler...");

        new GenerateMatrices();
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();

        new GenerateWorkloadSpecificationMatrix();
        vmMatrix = GenerateWorkloadSpecificationMatrix.getVmMatrix();
        cloudletMatrix = GenerateWorkloadSpecificationMatrix.getCloudletMatrix();


        BCOSchedulerInstance = new BCO(); // Adjust based on your BCO class
        mapping = BCOSchedulerInstance.run(); // Adjust based on your BCO class method to execute BCO

        try {
            // Same steps to create Datacenters, Broker, VMs, and Cloudlets
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }

            //Third step: Create Broker
            BCODatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            //Fourth step: Create VMs and Cloudlets and send them to broker
//            vmList = createHomogenousVM(brokerId, Constants.NO_OF_DATA_CENTERS);
//            cloudletList = createHomogenousCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            // mapping our dcIds to cloudsim dcIds
            HashSet<Integer> dcIds = new HashSet<>();
            HashMap<Integer, Integer> hm = new HashMap<>();
            for (Datacenter dc : datacenter) {
                if (!dcIds.contains(dc.getId()))
                    dcIds.add(dc.getId());
            }
            Iterator<Integer> it = dcIds.iterator();
            for (int i = 0; i < mapping.length; i++) {
                if (hm.containsKey((int) mapping[i])) continue;
                hm.put((int) mapping[i], it.next());
            }
            for (int i = 0; i < mapping.length; i++)
                mapping[i] = hm.containsKey((int) mapping[i]) ? hm.get((int) mapping[i]) : mapping[i];


            broker.submitVmList(vmList);
            broker.setMapping(mapping);
            broker.submitCloudletList(cloudletList);

            // Same steps for simulation

            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine(BCO_Scheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an error");
        }
    }

    private static BCODatacenterBroker createBroker(String name) throws Exception {
        return new BCODatacenterBroker(name);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("OUTPUT is:");
        Log.printLine("cloudlet id" + indent + "status" +
                indent + "data center id" +
                indent + "vm id" +
                indent + indent + "time" +
                indent + "start time" +
                indent + "finish time");

        double mxFinishTime = 0;
        DecimalFormat dft = new DecimalFormat("###.##");
        dft.setMinimumIntegerDigits(2);
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("success");
                Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) +
                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
            mxFinishTime = Math.max(mxFinishTime, cloudlet.getFinishTime());
        }
        Log.printLine(mxFinishTime);
        BCOSchedulerInstance.printBestFitness();
    }
}
