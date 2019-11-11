package simplediff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.web.bind.annotation.*;

@RestController
public class DiffController {

    @GetMapping("/diff")
    public String getDiff(@RequestParam String branch) {
        prepareCommands(branch);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "./gumtree/bin/gumtree webdiff targetBranch sourceBranch");
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
                finishedCommands();
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
        processBuilder.command("bash", "-c", "git clone -b master https://github.com/itsAlexNguyen/samples.git targetBranch && git clone -b " + sourceBranch + " https://github.com/itsAlexNguyen/samples.git sourceBranch");

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
        processBuilder.command("bash", "-c", "rm -rf targetBranch && rm -rf sourceBranch");

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