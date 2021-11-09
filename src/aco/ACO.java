package aco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
//import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class ACO {

    private static List<Cloudlet> cloudletList;
    private static int cloudletNum = 20;
    private static List<Vm> vmList;
    private static int vmNum = 2;

    public static void main(String args[]) {
        Log.printLine("Starting ACO Example...");
        int num_user = 1;
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;
        CloudSim.init(num_user, calendar, trace_flag);
        Datacenter datacenter0 = createDatacenter("Datacenter_0");
        DatacenterBroker broker = createBroker();
        int brokerId = broker.getId();
        int vmid = 0;
        int[] mipss = new int[]{278, 289, 132, 209, 286};
        long size = 10000;
        int ram = 2048;
        long bw = 10000;
        int pesNumber = 1;
        String vmm = "xen";
        vmList = new ArrayList<Vm>();
        for (int i = 0; i < vmNum; i++) {
            vmList.add(new Vm(vmid, brokerId, mipss[i], pesNumber, ram, bw, size, vmm,
                    new CloudletSchedulerSpaceShared()));
            vmid++;
        }
        broker.submitVmList(vmList);
        int id = 0;
        long[] lengths = new long[]{19365, 49809, 30218, 44157, 16754, 18336, 20045, 31493, 30727, 2341, 31017, 32100, 2145, 12343, 14567, 23452, 23121, 1345, 7643, 1234, 2345, 4321, 2113, 5674, 5432, 2123, 4321, 1231, 2131, 2455};
        long fileSize = 300;
        long outputSize = 300;
        UtilizationModel model = new UtilizationModelFull();
        cloudletList = new ArrayList<Cloudlet>();
        for (int i = 0;
                i < cloudletNum;
                i++) {
            Cloudlet cloudlet = new Cloudlet(id, lengths[i], pesNumber, fileSize, outputSize,
                    model, model, model);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
            id++;
        }

        broker.submitCloudletList(cloudletList);
//broker.bind(5,50);//bind(ÂìÒÏ¸öÊý£¬µü´ú´ÎÊý)
//broker.bindCloudletsToVmsSimple();

        CloudSim.startSimulation();
        List<Cloudlet> newList = broker.getCloudletReceivedList();

        CloudSim.stopSimulation();

        printCloudletList(newList);

        Log.printLine(
                "ACO finished!");
    }

    public static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<Host>();
        int mips = 1000;
        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;
        for (int i = 0; i < vmNum; i++) {
            List<Pe> peList = new ArrayList<Pe>();
            peList.add(new Pe(0, new PeProvisionerSimple(mips)));
            hostList.add(
                    new Host(
                            hostId,
                            new RamProvisionerSimple(ram),
                            new BwProvisionerSimple(bw),
                            storage,
                            peList,
                            new VmSchedulerTimeShared(peList)));
            hostId++;
        }

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMcm = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<Storage>();
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm,
                hostList, time_zone, cost, costPerMcm, costPerStorage, costPerBw);
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker",1,100.0,1.0,5.0,1.0,0.5);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        String indent = " ";
        Log.printLine();
        Log.printLine("========OUTPUT========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Datacenter ID" + indent +"VMID"+indent+"Time "+indent+"Start Time"+indent+"Finish Time ");
DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCESSS");
                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent
                        + cloudlet.getVmId() + indent + indent + dft.format(cloudlet.getActualCPUTime())
                        + indent + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}
