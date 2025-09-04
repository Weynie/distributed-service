package com.distributed.service;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Client {

    // Color output
    static final String RESET = "\u001B[0m";
    static final String GREEN = "\u001B[32m";   // Success
    static final String RED = "\u001B[31m";     // Errors
    static final String YELLOW = "\u001B[33m";  // Warnings and Usages
    static final String CYAN = "\u001B[36m";    // Selected records output

    static EMPService empService;

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        // Determine whether a host parameter was provided.
        Set<String> opKeywords = new HashSet<>(Arrays.asList("insert", "update", "delete", "select"));
        String host;
        String[] opArgs;
        if (opKeywords.contains(args[0].toLowerCase())) {
            host = null; // No explicit host provided.
            opArgs = args;
        } else {
            host = args[0];
            opArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        // Prepare listener for retry callbacks
        RetryListener listener = null;
        
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            // Factory: Look up the factory and then create a new EMPService instance.
            // This ensures that every client has a connection to db 
            EMPServiceFactory factory = (EMPServiceFactory) registry.lookup("DistributedServiceFactory");
            listener = new RetryListenerImpl();
            empService = factory.newEMPService();
            // needed to show retry message, without it, the retry messages show at client side
            empService.setRetryListener(listener);
            System.out.println(GREEN + "Connected to a dedicated EMPService instance via factory on " + (host == null ? "localhost" : host) + RESET);
        } catch (Exception e) {
            System.err.println(RED + "Client exception: " + e.toString() + RESET);
            e.printStackTrace();
            return;
        }
        
        List<Operation> operations = parseOperations(opArgs);
        
        try {
            empService.setAutoCommit(false);
            System.out.println(GREEN + "Auto-commit disabled." + RESET);
            
            for (Operation op : operations) {
                switch (op.type) {
                    case "insert":
                        insertEmployee(op.tokens);
                        break;
                    case "update":
                        updateEmployee(op.tokens);
                        break;
                    case "delete":
                        deleteEmployee(op.tokens);
                        break;
                    case "select":
                        selectEmployee(op.tokens);
                        break;
                    default:
                        System.out.println(YELLOW + "Warning: Unknown operation: " + op.type + RESET);
                        break;
                }
            }
            empService.commit();
            System.out.println(GREEN + "All operations committed successfully." + RESET);
        } catch (Exception e) {
            System.err.println(RED + "Error encountered: " + e.getMessage() + RESET);
            e.printStackTrace();
            try {
                empService.rollback();
                System.out.println(RED + "Transaction rolled back due to an error." + RESET);
            } catch (Exception re) {
                System.err.println(RED + "Error during rollback: " + re.getMessage() + RESET);
                re.printStackTrace();
            }
        } finally {
            try {
                // close connection after all the client's transactions are done
                empService.close();
            } catch (Exception re) {
                System.err.println(RED + "Error closing connection: " + re.getMessage() + RESET);
                re.printStackTrace();
            }
            // Unexport listener to allow JVM exit
            if (listener != null) {
                try {
                    UnicastRemoteObject.unexportObject(listener, true);
                } catch (NoSuchObjectException e) {
                    // ignore
                }
            }
            // Exit explicitly
            System.exit(0);
        }
    }
    
    // if no parameters are provided, then print the usage for users
    static void printUsage() {
        System.out.println(YELLOW + "Usage:" + RESET);
        System.out.println("java -cp classLocation Client [<host>] <operation> <parameters> ...");
        System.out.println("If no host is provided, localhost is assumed.");
        System.out.println("Operations:");
        System.out.println("Note: record id is case-sensitive");
        System.out.println("insert <id> name=<name> title=<title>");
        System.out.println("update <id> name=<newName> title=<newTitle>");
        System.out.println("delete <id>");
        System.out.println("select [<id>]");
        System.out.println("You can chain multiple operations in one invocation.");
        System.out.println("Example:");
        System.out.println("java -cp target/classes com.distributed.service.Client select");
        System.out.println("(or delete and then insert with specific record: java -cp target/classes com.distributed.service.Client delete E8 insert E11 name=jones title=Eng.)");
        System.out.println("(if you want to reset the whole db: java -cp target/classes com.distributed.service.RestoreInvoker)");
    }
    
    static List<Operation> parseOperations(String[] args) {
        List<Operation> operations = new ArrayList<>();
        Set<String> opKeywords = new HashSet<>(Arrays.asList("insert", "update", "delete", "select"));
        Operation current = null;
        for (String arg : args) {
            if (opKeywords.contains(arg.toLowerCase())) {
                if (current != null) {
                    operations.add(current);
                }
                current = new Operation(arg.toLowerCase());
            } else {
                if (current == null) {
                    System.out.println(YELLOW + "Warning: parameter provided without an operation keyword: " + arg + RESET);
                    System.exit(1);
                }
                current.tokens.add(arg);
            }
        }
        if (current != null) {
            operations.add(current);
        }
        return operations;
    }
    
    static class Operation {
        String type;
        List<String> tokens = new ArrayList<>();
        Operation(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type + " " + tokens;
        }
    }
    
    static void insertEmployee(List<String> tokens) throws Exception {
        if (tokens.size() < 3) {
            // considering misusage of functions would interrupt the afterwards operations, hereby presenting an exception 
            throw new Exception("Insert Error: Not enough parameters. Expected: <id> name=<name> title=<title>");
        }
        String eno = tokens.get(0);
        String name = null;
        String title = null;
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.startsWith("name=")) {
                name = token.substring("name=".length());
            } else if (token.startsWith("title=")) {
                title = token.substring("title=".length());
            }
        }
        // paramter number is enough, but the format is wrong, for example, not starting with name= or title=
        if (name == null || title == null) {
            throw new Exception("Insert Error: Missing required parameters. Expected format: name=<...> and title=<...>");
        }
        int addStatus = empService.addNewEmployee(eno, name, title);
        sleepDelay(3000);
        if (addStatus == 1) {
            System.out.println(GREEN + "Insert: " + eno + " " + name + " " + title + " added successfully." + RESET);
        } else {
            throw new Exception("Insert Error: Could not add new employee " + eno + " " + name);
        }
    }
    
    static void updateEmployee(List<String> tokens) throws Exception {
        if (tokens.size() < 3) {
            throw new Exception("Update Error: Not enough parameters. Expected: <id> name=<newName> title=<newTitle>");
        }
        String eno = tokens.get(0);
        String newName = null;
        String newTitle = null;
        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.startsWith("name=")) {
                newName = token.substring("name=".length());
            } else if (token.startsWith("title=")) {
                newTitle = token.substring("title=".length());
            }
        }
        if (newName == null || newTitle == null) {
            throw new Exception("Update Error: Missing required parameters. Expected format: name=<...> and title=<...>");
        }
        int updateStatus = empService.updateEmployee(eno, newName, newTitle);
        sleepDelay(3000);
        if (updateStatus == 1) {
            System.out.println(GREEN + "Update: " + eno + " " + newName + " " + newTitle + " updated successfully." + RESET);
        } else {
            throw new Exception("Update Error: Could not update employee " + eno + " " + newName);
        }
    }
    
    static void deleteEmployee(List<String> tokens) throws Exception {
        if (tokens.size() < 1) {
            throw new Exception("Delete Error: Not enough parameters. Expected: <id>");
        }
        String eno = tokens.get(0);
        EMP emp = empService.findEmployeeById(eno);
        if (emp == null) {
            throw new Exception("Delete Error: No employee with No.: " + eno + " found.");
        }
        int delStatus = empService.deleteEmployee(eno);
        sleepDelay(3000);
        if (delStatus == 1) {
            System.out.println(GREEN + "Delete: " + eno + " deleted successfully." + RESET);
        } else {
            throw new Exception("Delete Error: Could not delete employee " + eno);
        }
    }
    
    static void selectEmployee(List<String> tokens) throws Exception {
        if (tokens.isEmpty()) {
            List<EMP> empList = empService.getAllEmployees();
            System.out.println(CYAN + "Select: All employees:" + RESET);
            for (EMP emp : empList) {
                System.out.println(CYAN + emp + RESET);
            }
        } else {
            String eno = tokens.get(0);
            EMP emp = empService.findEmployeeById(eno);
            if (emp != null) {
                System.out.println(CYAN + "Select: " + emp + RESET);
            } else {
                // we here not consider selection failure as critical failure, so not invoke exceptions
                System.out.println(YELLOW + "Select Warning: No employee found with No.: " + eno + RESET);
            }
        }
    }
    
    static void sleepDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println(YELLOW + "Sleep interrupted: " + e.getMessage() + RESET);
        }
    }
}
