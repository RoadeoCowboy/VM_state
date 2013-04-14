package Lab1;

import java.net.URL;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

/**
 * Write a description of class MyVM here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyVM
{
    // instance variables 
    private String vmname ;
    private ServiceInstance si ; 
    private VirtualMachine vm ;
    //public static String getVmwareVM() { return "David.Chen@sjsu.edu" ; }
    /**
     * Constructor for objects of class MyVM
     */
    public MyVM() 
    {
        // initialise instance variables
        try {
            //System.out.println("Hello.");
            vmname = Config.getVmwareVM();
            //System.out.println(vmname);
            si = new ServiceInstance(new URL(Config.getVmwareHostURL()), 
                Config.getVmwareLogin(), Config.getVmwarePassword(), true);

            Folder rootFolder = si.getRootFolder();
            ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");

            if(mes==null || mes.length == 0) //exit if there is new mes entity or if there are not objects
            {                               //in the mes array.
                return;
            }

            for(int i=0; i<mes.length; i++) //iterate through mes array looking for VirtualMachine
            {                               //that matches my name.

                if(mes[i].getName().equals(vmname))
                {
                    System.out.println(mes[i].getName());
                    vm = (VirtualMachine) mes[i];   //set the vm to that virtualmachine. Suspect some issue here.

                }

            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }

    }

    /**
     * Destructor for objects of class MyVM
     */
    protected void finalize() throws Throwable
    {
        si.getServerConnection().logout();
    } 

    /**
     * Power On the Virtual Machine
     */
    public void powerOn() 
    {
        try {
            VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
            if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
            {
                vm.powerOnVM_Task(null);
                System.out.println("vm: " + vm.getName() + " is now powered on.");
            }
            else if(vmri.getPowerState() == VirtualMachinePowerState.suspended)
            {
                vm.powerOnVM_Task(null);
                System.out.println("vm: " + vm.getName() + " is now powered on.");
            }
            else
            {
                System.out.println("vm: " + vm.getName() + " is already powered on! No action taken.");
                return;
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Power Off the Virtual Machine
     */
    public void powerOff() 
    {   //Any reference to vm gives NullPointerExceptions.
        try {
            //System.out.println("Hello.");
            VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
            if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
            {
                vm.powerOffVM_Task();
                System.out.println("vm: " + vm.getName() + " is now powered off.");
            }
            else
            {
                System.out.println("vm: " + vm.getName() + " is already powered off/suspended! No action taken.");
                return;
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Reset the Virtual Machine
     */

    public void reset() 
    {
        try {
            VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
            if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
            {
                vm.resetVM_Task();
                System.out.println("vm: " + vm.getName() + " reset.");
            }    
            else
            {
                System.out.println("vm: " + vm.getName() + " is powered off/suspended! No action taken.");
                return;
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    /**
     * Suspend the Virtual Machine
     */

    public void suspend() 
    {
        try {
            VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();
            if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
            {
                vm.suspendVM_Task();
                System.out.println("vm: " + vm.getName() + " suspend.");
            }     
            else 
            {
                System.out.println("vm: " + vm.getName() + " is powered off/suspended! No action taken.");
                return;
            }
        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    public void forceShutdown()
    {
        try {
            VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();

            if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
            {
                System.out.println("Current " + vm.getName() + " State is poweredOff, Powering On before shutdown.");
                Task task = vm.powerOnVM_Task(null);
                task.waitForMe();    //There is lag between Powercycle, need a delay
                System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                vm.powerOffVM_Task();
                //task.waitForMe();
                System.out.println("System has been shutdown.");
            }
            else if(vmri.getPowerState() == VirtualMachinePowerState.suspended)
            {
                System.out.println("Current " + vm.getName() + " State is suspended, Powering On before shutdown.");
                Task task = vm.powerOnVM_Task(null);
                task.waitForMe();
                System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                vm.powerOffVM_Task();
                System.out.println("System has been shutdown.");
            }
            else if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
            {
                System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                vm.powerOffVM_Task();

                System.out.println("vm:" + vm.getName() + " forced shutdown.");
            }      

        } catch ( Exception e ) 
        { System.out.println( e.toString() ) ; }
    }

    public void shutdownAll()
    {
        try{
            VirtualMachine vm_David = null;
            Folder rootFolder = si.getRootFolder(); //Problem with si variable
            ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");

            for(int i=0; i<mes.length; i++) //iterate through mes array looking for VirtualMachine
            {
                if(mes[i].getName().equals(vmname))
                {
                    vm_David = (VirtualMachine) mes[i]; //Tracks my VM.
                }
                //Goes here if mes[i].getName() does not equal vmname.
                vm = (VirtualMachine) mes[i];   //set the vm to that virtualmachine. 

                {
                    VirtualMachineRuntimeInfo vmri = (VirtualMachineRuntimeInfo) vm.getRuntime();

                    if(vmri.getPowerState() == VirtualMachinePowerState.poweredOff)
                    {
                        System.out.println("Current " + vm.getName() + " State is poweredOff, Powering On before shutdown.");
                        Task task = vm.powerOnVM_Task(null);
                        task.waitForMe();   //There is lag between Powercycle, need a delay
                        System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                        vm.powerOffVM_Task();
                        System.out.println("System has been shutdown.");
                    }
                    else if(vmri.getPowerState() == VirtualMachinePowerState.suspended)
                    {
                        System.out.println("Current " + vm.getName() + " State is suspended, Powering On before shutdown.");
                        Task task = vm.powerOnVM_Task(null);
                        task.waitForMe();
                        System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                        vm.powerOffVM_Task();
                        System.out.println("System has been shutdown.");
                    }
                    else if(vmri.getPowerState() == VirtualMachinePowerState.poweredOn)
                    {
                        System.out.println("State is now PoweredOn, Shutting down VM (PowerOff).");
                        vm.powerOffVM_Task();

                        System.out.println("vm:" + vm.getName() + " forced shutdown.");
                    }    
                }
            }
            vm = vm_David;
            System.out.println("My VM is: " + vm.getName());

        } catch (Exception e)
        { System.out.println( e.toString() ) ; }
    }

    // if(mes[i].getName().equals(vmname))  //if match.
    public static void run() throws Exception
    {

    }

}

