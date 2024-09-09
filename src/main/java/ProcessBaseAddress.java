import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import java.util.Arrays;
import java.util.List;

public class ProcessBaseAddress {

    // Define the PEB_LDR_DATA structure (simplified)
    public static class PEB_LDR_DATA extends Structure {
        public byte Reserved1[] = new byte[8];  // Placeholder, not needed
        public Pointer InMemoryOrderModuleList;  // Pointer to a list of loaded modules

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Reserved1", "InMemoryOrderModuleList");
        }
    }

    // Define the PEB structure (simplified)
    public static class PEB extends Structure {

        public Pointer Ldr;                     // Pointer to PEB_LDR_DATA structure

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Reserved1", "Reserved2", "Ldr");
        }
    }

    // Define the PROCESS_BASIC_INFORMATION structure
    public static class PROCESS_BASIC_INFORMATION extends Structure {
        public Pointer PebBaseAddress;  // PEB Base Address


        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Reserved1", "PebBaseAddress", "Reserved2", "UniqueProcessId", "Reserved3");
        }
    }

    public static Pointer getBaseAddressOfImage(HANDLE processHandle) {
        Ntdll ntdll = Ntdll.INSTANCE;
        Kernel32 kernel32 = Kernel32.INSTANCE;

        // Step 1: Query the basic information of the process
        PROCESS_BASIC_INFORMATION processBasicInformation = new PROCESS_BASIC_INFORMATION();
        IntByReference returnLength = new IntByReference();
        int status = ntdll.NtQueryInformationProcess(processHandle, 0, processBasicInformation.getPointer(), processBasicInformation.size(), returnLength);
        processBasicInformation.read();

        if (status != 0) {
            System.out.println("Failed to query process information. Status: " + status);
            return null;
        }

        // Step 2: Read the PEB structure from the process
        PEB peb = new PEB();
        IntByReference bytesRead = new IntByReference();
        int readSuccess = kernel32.ReadProcessMemory(processHandle, processBasicInformation.PebBaseAddress, peb.getPointer(), peb.size(), bytesRead);
        peb.read();


        // Step 3: Read the PEB_LDR_DATA structure (which contains the module list)
        PEB_LDR_DATA pebLdrData = new PEB_LDR_DATA();
        readSuccess = kernel32.ReadProcessMemory(processHandle, peb.Ldr, pebLdrData.getPointer(), pebLdrData.size(), bytesRead);
        pebLdrData.read();

        Pointer baseAddress = pebLdrData.InMemoryOrderModuleList;  // Simplified
        System.out.println("Base address of the image: " + baseAddress);

        return baseAddress;
    }
}
