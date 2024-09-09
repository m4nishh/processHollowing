import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;

public interface Kernel32 extends Library {
    Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);

    boolean CreateProcessA(String lpApplicationName, String lpCommandLine, Pointer lpProcessAttributes,
                           Pointer lpThreadAttributes, boolean bInheritHandles, int dwCreationFlags,
                           Pointer lpEnvironment, String lpCurrentDirectory, STARTUPINFO lpStartupInfo,
                           PROCESS_INFORMATION lpProcessInformation);

    Pointer VirtualAllocEx(HANDLE hProcess, Pointer lpAddress, int dwSize, int flAllocationType, int flProtect);

    boolean WriteProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, byte[] lpBuffer, int nSize, IntByReference lpNumberOfBytesWritten);

    boolean GetThreadContext(HANDLE hThread, ProcessHollowing.CONTEXT lpContext);

    boolean SetThreadContext(HANDLE hThread, ProcessHollowing.CONTEXT lpContext);

    DWORD ResumeThread(HANDLE hThread);
    int ReadProcessMemory(HANDLE hProcess, Pointer lpBaseAddress, Pointer lpBuffer, int nSize, IntByReference lpNumberOfBytesRead);

}
