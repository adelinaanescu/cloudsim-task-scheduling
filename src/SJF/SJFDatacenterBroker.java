package SJF;


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJFDatacenterBroker extends DatacenterBroker {

    //Constructor method that takes a string name and passes it to the superclass constructor to initialize the name of the broker.
    SJFDatacenterBroker(String name) throws Exception {
        super(name);
    }
    //Method that binds cloudlets to VMs based on the shortest job first scheduling algorithm.
    // It first calculates the number of required tasks and VMs and selects the first VM in the VM list.
    // Then, it iterates through the cloudlet list, binding each cloudlet to a VM and outputting a message to the console indicating the task and VM it is bound to.
    // After that, it sorts the received cloudlet list in ascending order of task length divided by the product of the VM's MIPS (Million Instructions Per Second) and number of PEs (Processing Elements) using a simple bubble sort algorithm.
    // Finally, it sets the sorted list as the received cloudlet list for the broker.
    public void scheduleTaskstoVms() {
        // Sort the cloudletList by each cloudlet's length (shortest job first)
        cloudletList.sort(Comparator.comparingLong(Cloudlet::getCloudletLength));

        int reqTasks = cloudletList.size();
        int reqVms = vmList.size();
        Vm vm=  vmList.get(0);;

        for (int i = 0; i < reqTasks; i++) {
            bindCloudletToVm(i, (i % reqVms));
            System.out.println("Task" + cloudletList.get(i).getCloudletId() + " is bound with VM" + vmList.get(i % reqVms).getId());
        }

        //System.out.println("reqTasks: "+ reqTasks);

        ArrayList<Cloudlet> list = new ArrayList<Cloudlet>();
        for (Cloudlet cloudlet : getCloudletReceivedList()) {
            list.add(cloudlet);
        }

        //setCloudletReceivedList(null);

        Cloudlet[] list2 = list.toArray(new Cloudlet[list.size()]);

        //System.out.println("size :"+list.size());

        Cloudlet temp = null;

        int n = list.size();

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (list2[j - 1].getCloudletLength() / (vm.getMips() * vm.getNumberOfPes()) > list2[j].getCloudletLength() / (vm.getMips() * vm.getNumberOfPes())) {
                    //swap the elements!
                    //swap(list2[j-1], list2[j]);
                    temp = list2[j - 1];
                    list2[j - 1] = list2[j];
                    list2[j] = temp;
                }
                // printNumbers(list2);
            }
        }

        ArrayList<Cloudlet> list3 = new ArrayList<Cloudlet>();

        for (int i = 0; i < list2.length; i++) {
            list3.add(list2[i]);
        }
        //printNumbers(list);

        setCloudletReceivedList(list);

        //System.out.println("\n\tSJFS Broker Schedules\n");
        //System.out.println("\n");
    }

    public void printNumber(Cloudlet[] list) {
        for (int i = 0; i < list.length; i++) {
            System.out.print(" " + list[i].getCloudletId());
            System.out.println(list[i].getCloudletStatusString());
        }
        System.out.println();
    }

    public void printNumbers(ArrayList<Cloudlet> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.print(" " + list.get(i).getCloudletId());
        }
        System.out.println();
    }

    //Method that processes a cloudlet returned by a VM.
    // It adds the cloudlet to the received cloudlet list, outputs a message to the console indicating the ID of the received cloudlet, and decrements the cloudletsSubmitted variable.
    // If there are no more cloudlets to submit and all previously submitted cloudlets have finished executing, it calls the scheduleTaskstoVms() method to bind the remaining received cloudlets to VMs and execute them.
    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
                + " received");
        cloudletsSubmitted--;
        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) {
            scheduleTaskstoVms();
            cloudletExecution(cloudlet);
        }
    }

    // Method that handles the execution of a cloudlet.
    // If there are no more cloudlets to execute, it clears the datacenters, finishes execution, and outputs a message to the console indicating that all cloudlets have been executed.
    // Otherwise, if there are still cloudlets to execute but none have been submitted yet, it clears the datacenters and creates new VMs.
    // This is to ensure that each cloudlet has a VM to execute on.
    protected void cloudletExecution(Cloudlet cloudlet) {

        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
            Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
            clearDatacenters();
            finishExecution();
        } else { // some cloudlets haven't finished yet
            if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
                // all the cloudlets sent finished. It means that some bount
                // cloudlet is waiting its VM be created
                clearDatacenters();
                createVmsInDatacenter(0);
            }
        }
    }

    //Method that processes the resource characteristics of a datacenter.
    // It adds the characteristics to the datacenter characteristics list and,
    // if the number of characteristics in the list equals the number of datacenter IDs in the broker's list, it calls the distributeRequestsForNewVmsAcrossDatacenters() method to distribute VM creation requests across the available datacenters.
    @Override
    protected void processResourceCharacteristics(SimEvent ev) {
        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
            distributeRequestsForNewVmsAcrossDatacenters();
        }
    }


    //Method that distributes requests for creating new VMs across the available datacenters.
    //It iterates through the VM list, selects a datacenter to create the VM in, sends a message to the datacenter to create the VM,
    // and outputs a message to the console indicating the ID of the VM and the datacenter it will be created in.
    protected void distributeRequestsForNewVmsAcrossDatacenters() {
        int numberOfVmsAllocated = 0;
        int i = 0;

        final List<Integer> availableDatacenters = getDatacenterIdsList();

        for (Vm vm : getVmList()) {
            int datacenterId = availableDatacenters.get(i++ % availableDatacenters.size());
            String datacenterName = CloudSim.getEntityName(datacenterId);

            if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId() + " in " + datacenterName);
                sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
                numberOfVmsAllocated++;
            }
        }

        setVmsRequested(numberOfVmsAllocated);
        setVmsAcks(0);
    }
}