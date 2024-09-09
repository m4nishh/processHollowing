# Process Hollowing in Java

This project demonstrates how to implement **process hollowing** in Java using **JNA (Java Native Access)** to call Windows API functions. 
Process hollowing is a technique often used in malware to inject malicious code into a legitimate process. The legitimate code is unmapped from the memory, and the malicious code is loaded in its place.

## Features

- **Create a Suspended Process**: Creates a legitimate Windows process in a suspended state.
- **Unmap the Legitimate Code**: Removes the legitimate code from the process memory using `ZwUnmapViewOfSection`.
- **Inject Malicious Code**: Writes malicious or arbitrary code into the newly unmapped process.
- **Modify the Thread Context**: Changes the execution point of the suspended process to the injected code.
- **Resume the Process**: Resumes the process, allowing it to execute the injected malicious code.
- **Retrieve Process Base Address**: Extracts the base address of the process image using `NtQueryInformationProcess`.

## Prerequisites

Before you start, ensure you have the following tools and libraries:

- **JDK 8 or higher**: You need to install Java Development Kit.
- **JNA (Java Native Access)**: Used to call native Windows API functions from Java.
- **Windows**: The project is specific to the Windows operating system, as it involves Windows API functions.

## Setup Instructions

  ### Step 1: Clone the Repository
  
    First, clone the repository to your local machine:
       git clone https://github.com/your-username/process-hollowing-java.git 
       cd process-hollowing-java
  ### Step 2: Install Dependancies
    <dependencies>
        <dependency>
            <groupId>net.java.dev.jna</groupId>
            <artifactId>jna</artifactId>
            <version>5.9.0</version>
        </dependency>
        <dependency>
            <groupId>net.java.dev.jna</groupId>
            <artifactId>jna-platform</artifactId>
            <version>5.9.0</version>
        </dependency>
    </dependencies>
 ### Step 3: Compile and run
   javac -cp path/to/jna.jar:. *.java
    java -cp path/to/jna.jar:. ProcessHollowing
 ### Application should be running as Administrator


## Code Structure
Here's an overview of the key files:

 - **ProcessHollowing.java**: The main class that implements the process hollowing technique. It creates a suspended process, injects malicious code, and modifies the thread context.
 - **Kernel32.java**: Interface for calling Windows Kernel32 functions using JNA.
 - **Ntdll.java**: Interface for calling Ntdll functions, including ZwUnmapViewOfSection.
 - **ProcessBaseAddress.java**: Contains the logic to retrieve the base address of the process image using NtQueryInformationProcess.


 ## How it Works
 - **Create Suspended Process**: The project starts by creating a new suspended process (e.g., notepad.exe) using CreateProcessA with the CREATE_SUSPENDED flag.
 - **Unmap Legitimate Code**: After creating the suspended process, we unmap the legitimate code from memory using ZwUnmapViewOfSection, making room for the injected code.
 - **Inject Malicious Code**: We allocate memory in the process and write our malicious code into it using VirtualAllocEx and WriteProcessMemory.
 - **Set Thread Context**: The entry point of the process is modified using SetThreadContext to point to the injected code.
 - **Resume the Process**: Finally, the thread is resumed using ResumeThread, allowing the process to execute the injected malicious code.

------------------------------------------------------------***----------------------------------------------------------***------------------------------------------------------------------
