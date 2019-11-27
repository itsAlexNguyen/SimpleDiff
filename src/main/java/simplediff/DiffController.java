package simplediff;

import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import simplediff.gumtree.diff.web.WebDiff;

@RestController
public class DiffController {

  private static final String BASH_PATH;
  private static final String CLEANUP_COMMAND;
  private static final String ORIGINAL_SOURCE_FOLDER = "source";
  private static final String MODIFIED_SOURCE_FOLDER = "target";

  static {
    final String nameOS = System.getProperty("os.name");
    if (nameOS.equals("Windows 10")) {
      BASH_PATH = "C:\\Program Files\\Git\\git-bash.exe";
    } else {
      BASH_PATH = "bash";
    }
    CLEANUP_COMMAND =
        String.format("rm -rf %s && rm -rf %s", ORIGINAL_SOURCE_FOLDER, MODIFIED_SOURCE_FOLDER);
  }

  /**
   * Handles diff requests.
   *
   * @param targetBranch - target branch of repository
   * @param repoSlug - URL friendly alias of repository
   * @param pullRequestID - pull request number
   */
  @RequestMapping(
      value = "/diff",
      method = RequestMethod.GET,
      produces = {"application/xml", "text/xml"})
  public String getDiff(
      final @RequestParam String targetBranch,
      final @RequestParam String repoSlug,
      final @RequestParam int pullRequestID) {
    prepareCommands(targetBranch, repoSlug, pullRequestID);
    WebDiff.initGenerators();
    final WebDiff diff = new WebDiff(new String[] {ORIGINAL_SOURCE_FOLDER, MODIFIED_SOURCE_FOLDER});
    final String xmlOutput = diff.generate();
    finishedCommands();
    return xmlOutput;
  }

  private void prepareCommands(
      final String targetBranch, final String repoSlug, final int pullRequestID) {

    final String[] commandList = new String[4];
    commandList[0] =
        String.format(
            "git clone -b %s https://github.com/%s.git -n --depth 1 %s",
            targetBranch, repoSlug, ORIGINAL_SOURCE_FOLDER);
    commandList[1] =
        String.format(
            "git clone -b %s https://github.com/%s.git -n --depth 1 %s",
            targetBranch, repoSlug, MODIFIED_SOURCE_FOLDER);
    commandList[2] =
        String.format(
            "cd %s && git fetch origin pull/%d/head", MODIFIED_SOURCE_FOLDER, pullRequestID);
    commandList[3] =
        String.join(
            "",
            "cd ",
            MODIFIED_SOURCE_FOLDER,
            " && git diff --name-only --diff-filter=M ",
            targetBranch,
            " FETCH_HEAD | xargs -I %% bash -c 'git checkout FETCH_HEAD -- %%; cd ../",
            ORIGINAL_SOURCE_FOLDER,
            "; git checkout ",
            targetBranch,
            " -- %%;'");

    for (String command : commandList) {
      executeBashCommand(command);
    }
  }

  private void finishedCommands() {
    executeBashCommand(CLEANUP_COMMAND);
  }

  private void executeBashCommand(final String command) {
    final ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(BASH_PATH, "-c", command);
    try {
      final Process process = processBuilder.start();
      int exitVal = process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
