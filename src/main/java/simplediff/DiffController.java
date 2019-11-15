package simplediff;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.*;

@RestController
public class DiffController {
    String bashPath =  "bash";

    @GetMapping("/diff")
    public String getDiff(@RequestParam String branch) {
        String nameOS = System.getProperty("os.name");
        if (nameOS.equals("Windows 10")){
            bashPath = "C:\\Program Files\\Git\\git-bash.exe";
        }
        prepareCommands(branch);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(bashPath, "-c", "./gumtree/run/bin/gumtree webdiff targetBranch sourceBranch > index.html");

        try {
            Process process = processBuilder.start();
            int exitVal = process.waitFor();

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream("index.html")));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }



            if (exitVal == 0) {
                //finishedCommands();
                return output.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Couldn't perform operation";
    }

    private void prepareCommands(String sourceBranch) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(bashPath, "-c", "git clone -b master https://github.com/itsAlexNguyen/samples.git targetBranch && git clone -b " + sourceBranch + " https://github.com/itsAlexNguyen/samples.git sourceBranch");

        try {

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finishedCommands() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(bashPath, "-c", "rm -rf targetBranch && rm -rf sourceBranch");
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}