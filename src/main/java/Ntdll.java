import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public interface Ntdll extends Library {
    Ntdll INSTANCE = (Ntdll) Native.load("ntdll", Ntdll.class);

    int ZwUnmapViewOfSection(HANDLE hProcess, Pointer baseAddress);
    int NtQueryInformationProcess(HANDLE processHandle, int processInformationClass, Pointer processInformation, int processInformationLength, IntByReference returnLength);

}
