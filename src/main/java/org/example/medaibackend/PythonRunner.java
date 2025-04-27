package org.example.medaibackend;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonRunner {
    public static String runPythonScript(String... args) {
        try {
            String scriptPath = "pythonProject/main.py";
            String[] command = new String[args.length + 2];
            command[0] = "python3";
            command[1] = scriptPath;
            for (int i = 0; i < args.length; i++) {
                command[i + 2] = args[i];
            }

            Process process = new ProcessBuilder(command).start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            process.waitFor();
            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error running Python script.";
        }
    }
}
