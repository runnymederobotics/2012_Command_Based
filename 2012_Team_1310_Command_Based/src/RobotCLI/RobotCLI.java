package RobotCLI;

import java.io.IOException;
import java.io.InputStreamReader;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.Parsable.ParsableString;
import com.sun.squawk.io.BufferedReader;
import java.io.*;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import com.sun.squawk.util.SquawkHashtable;
import com.sun.squawk.util.StringTokenizer;
import java.util.Enumeration;

public class RobotCLI extends Thread {

    private static class PrintWriter {

        OutputStream stream;
        String buffer = "";

        public PrintWriter(OutputStream stream) {
            this.stream = stream;
        }

        public void print(String s) {
            buffer += s;
        }

        public void println(String s) throws IOException {
            print(s);
            buffer += "\r\n";
            flush();
        }

        public void flush() throws IOException {
            stream.write(buffer.getBytes());
            stream.flush();
            buffer = "";
        }
    }
    String robotName;
    int port;
    VariableContainer variables;

    public RobotCLI(String robotName, int port) throws IOException {
        this.robotName = robotName;
        this.port = port;
        this.variables = new VariableContainer(null, "");
        this.start();
    }

    public VariableContainer getVariables() {
        return variables;
    }

    public static class VariableContainer implements Parsable {

        VariableContainer owner;
        String name;
        private SquawkHashtable variables = new SquawkHashtable();

        public VariableContainer(VariableContainer owner, String name) {
            this.owner = owner;
            this.name = name;
            if (owner != null) {
                owner.variables.put(owner.getPrefix() + name, this);
            }
        }

        String getPrefix() {
            if (name.length() > 0) {
                return name + ".";
            }
            return "";
        }

        public ParsableInteger createInteger(String name, int value) {
            ParsableInteger ret = owner != null ? owner.createInteger(getPrefix() + name, value) : new ParsableInteger(value);
            variables.put(name, ret);
            return ret;
        }

        public ParsableDouble createDouble(String name, double value) {
            ParsableDouble ret = owner != null ? owner.createDouble(getPrefix() + name, value) : new ParsableDouble(value);
            variables.put(name, ret);
            return ret;
        }

        public ParsableString createString(String name, String value) {
            ParsableString ret = owner != null ? owner.createString(getPrefix() + name, value) : new ParsableString(value);
            variables.put(name, ret);
            return ret;
        }

        public VariableContainer createContainer(String name) {
            VariableContainer ret = owner != null ? owner.createContainer(getPrefix() + name) : new VariableContainer(this, getPrefix() + name);
            return ret;
        }

        public void putVariable(String name, Parsable variable) {
            variables.put(name, variable);
        }

        public void parseFrom(String value) {
            //TODO: throw
        }

        public String toString() {
            String ret = "{";
            Enumeration keys = variables.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                ret += key;
                ret += ": ";
                ret += ((Parsable) variables.get(key)).toString();
                if (keys.hasMoreElements()) {
                    ret += ", ";
                }
            }
            ret += "}";
            return ret;
        }
    }

    public void run() {
        while (true) {
            try {
                ServerSocketConnection serverSocket = (ServerSocketConnection) Connector.open("socket://:" + port);

                while (true) {
                    StreamConnection clientSocket = serverSocket.acceptAndOpen();
                    Thread clientThread = new Thread(new ClientHandler(clientSocket));
                    clientThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {

        StreamConnection clientSocket;

        boolean headlessMode = false;
        
        ClientHandler(StreamConnection clientSocket) {
            this.clientSocket = clientSocket;
        }

        String[] split(String s) {
            String[] ret;
            StringTokenizer st = new StringTokenizer(s, " ");
            int numTokens = st.countTokens();

            if (numTokens == 0) {
                ret = new String[]{s};
            } else {
                ret = new String[numTokens];
                int i = 0;
                while (st.hasMoreTokens()) {
                    ret[i++] = st.nextToken();
                }
            }

            return ret;
        }

        public void run() {
            try {
                InputStreamReader in = new InputStreamReader(clientSocket.openInputStream());
                PrintWriter out = new PrintWriter(clientSocket.openOutputStream());

                System.out.println("Client connected");
                out.println("Hello! You are connected to '" + robotName + "'\nType 'help' for help.\n> ");
                out.flush();
                String request = null;
                int c;
                while ((c = in.read()) > 0) {
                    if (request == null) {
                        request = new String();
                    }
                    if (c == '\r') {
                        continue;
                    } else if (c != '\n') {
                        request += (char) c;
                        continue;
                    } else {
                        System.out.println("Got request: " + request);
                        String[] commandAndArgs = split(request);
                        String command = commandAndArgs[0].trim();
                        if(!headlessMode) {
                            out.print(">> ");
                        }
                        if ("help".equals(command)) {
                            out.println("Command List:\r\nset [variable] = [value]\r\nprint [variable]\r\nwatch [interval (ms)] [variable]\r\nlist\r\nquit\r\nexit");
                        } else if ("set".equals(command)) {
                            doSetCommand(commandAndArgs, out);
                        } else if ("print".equals(command)) {
                            doPrintCommand(commandAndArgs, out);
                        } else if ("list".equals(command)) {
                            doListCommand(commandAndArgs, out);
                        } else if ("quit".equals(command) || "exit".equals(command)) {
                            clientSocket.close();
                            return;
                        } else if ("headless".equals(command)) {
                            headlessMode = true;
                        } else if ("watch".equals(command)) {
                            doWatchCommand(commandAndArgs, out, null);
                        } else {
                            out.println("Unknown command");
                        }

                        if(!headlessMode) {
                            out.print("\r\n> ");
                        } else {
                            out.print("\n");
                        }
                        out.flush();
                    }
                    request = new String();
                }
                System.out.println("Client disconnected");
            } catch (IOException e) {
                System.out.println("Client error: " + e.toString());
            }
        }

        private void doWatchCommand(String[] commandAndArgs, PrintWriter out, BufferedReader in) throws IOException {
            if (commandAndArgs.length < 3 || !"watch".equals(commandAndArgs[0])) {
                out.println("Invalid 'watch' command");
                return;
            }
            int waitMs = 0;
            try {
                waitMs = Integer.parseInt(commandAndArgs[1]);
            } catch (NumberFormatException e) {
                out.println("Error: first argument should be wait time in milliseconds");
                return;
            }
            int sequence = 0;
            while (true) {
                out.print("Sequence number: " + sequence + "\n");
                ++sequence;
                for (int i = 2; i < commandAndArgs.length; ++i) {
                    Object theVariable = variables.variables.get(commandAndArgs[i]);
                    if (theVariable == null) {
                        out.println("Unknown variable: " + commandAndArgs[i]);
                        continue;
                    }
                    out.println(commandAndArgs[i] + " = " + ((Parsable) theVariable).toString());
                }
                out.println("<< hit enter to stop watching >>");
                out.flush();
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //in.readLine();
        }

        private void doListCommand(String[] commandAndArgs, PrintWriter out) throws IOException {
            //With the listCommand do we really care if we passed it too many parameters?
            /*if (/*commandAndArgs.length != 1 || !"list".equals(commandAndArgs[0])) {
                out.println("Invalid 'list' command");
                return;
            }*/
            String variableList = "";
            //variableList += "All variables:\n";
            Enumeration keys = variables.variables.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                //Parsable parsable = (Parsable) variables.variables.get(key);
                Object obj = variables.variables.get(key);
                boolean isVariableContainer = obj.getClass().getName().equals(VariableContainer.class.getName());
                if(!isVariableContainer) {
                    variableList += key + (!headlessMode ? "\r" : "") + "\n";// + " = " + parsable.toString() + "\n";
                }
            }
            System.out.println(variableList);
            out.println(variableList);
        }

        private void doPrintCommand(String[] commandAndArgs, PrintWriter out) throws IOException {
            if (commandAndArgs.length < 2 || !"print".equals(commandAndArgs[0])) {
                out.println("Invalid 'print' command");
                return;
            }
            for (int i = 1; i < commandAndArgs.length; ++i) {
                Object theVariable = variables.variables.get(commandAndArgs[i]);
                if (theVariable == null) {
                    out.println("Unknown variable: '" + commandAndArgs[i] + "'");
                    return;
                }
                out.println(commandAndArgs[i] + " = " + ((Parsable) theVariable).toString());
            }
        }

        public void doSetCommand(String[] commandAndArgs, PrintWriter out) throws IOException {
            if (commandAndArgs.length != 4 || !"set".equals(commandAndArgs[0]) || !"=".equals(commandAndArgs[2])) {
                out.println("Invalid 'set' command");
                return;
            }
            Object theVariable = variables.variables.get(commandAndArgs[1]);
            if (theVariable == null) {
                out.println("Unknown variable: " + commandAndArgs[1]);
                return;
            }

            try {
                ((Parsable) theVariable).parseFrom(commandAndArgs[3]);
            } catch (NumberFormatException e) {
                out.println("Error: unable to parse the value");
                return;
            }
            out.println(commandAndArgs[1] + " = " + commandAndArgs[3]);
        }
    }
}
