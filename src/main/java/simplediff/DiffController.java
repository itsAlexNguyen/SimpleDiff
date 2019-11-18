package simplediff;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simplediff.gumtree.diff.web.WebDiff;

@RestController
public class DiffController {

  /**
   * Handles diff requests.
   *
   * @param branch - target branch of repository
   */
  @GetMapping("/diff")
  public String getDiff(@RequestParam String branch) {
    prepareCommands(branch);
    WebDiff.initGenerators();
    final WebDiff diff = new WebDiff(new String[] {"sourceBranch", "targetBranch"});
    return diff.generate();
  }

  private void prepareCommands(String sourceBranch) {
    String bashPath = "bash";
    final String nameOS = System.getProperty("os.name");
    if (nameOS.equals("Windows 10")) {
      bashPath = "C:\\Program Files\\Git\\git-bash.exe";
    }
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(
        bashPath,
        "-c",
        "git clone -b master https://github.com/itsAlexNguyen/samples.git targetBranch && git clone -b "
            + sourceBranch
            + " https://github.com/itsAlexNguyen/samples.git sourceBranch");

    try {
      Process process = processBuilder.start();
      int exitVal = process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void finishedCommands() {
    String bashPath = "bash";
    final String nameOS = System.getProperty("os.name");
    if (nameOS.equals("Windows 10")) {
      bashPath = "C:\\Program Files\\Git\\git-bash.exe";
    }
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(bashPath, "-c", "rm -rf targetBranch && rm -rf sourceBranch");
    try {
      Process process = processBuilder.start();
      int exitVal = process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
