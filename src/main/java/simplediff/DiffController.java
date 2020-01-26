package simplediff;

import java.io.IOException;
import java.io.File;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import simplediff.gumtree.diff.web.WebDiff;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

@RestController
public class DiffController {

  private static final String ORIGINAL_SOURCE_FOLDER = "source";
  private static final String MODIFIED_SOURCE_FOLDER = "target";

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
    try {
      prepareCommands(targetBranch, repoSlug, pullRequestID);
      WebDiff.initGenerators();
      final WebDiff diff = new WebDiff(new String[] {ORIGINAL_SOURCE_FOLDER, MODIFIED_SOURCE_FOLDER});
      final String xmlOutput = diff.generate(getTitle(repoSlug, pullRequestID, targetBranch), targetBranch);
      return xmlOutput;
    } finally {
      deleteDirectory(new File(ORIGINAL_SOURCE_FOLDER));
      deleteDirectory(new File(MODIFIED_SOURCE_FOLDER));
    }
  }

  private static void deleteDirectory(File path) {
    File[] files = path.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    path.delete();
  }

  private String getTitle(String repoSlug, int pullRequestID, String targetBranch){
    return "Repository: " + repoSlug + " - Target Branch: " + targetBranch + " - PR #" + pullRequestID;
  }

  private static AbstractTreeIterator prepareTreeParser(Repository repo, String ref) throws IOException {
    // from the commit we can build the tree which allows us to construct the TreeParser
    Ref head = repo.exactRef(ref);
    try (RevWalk walk = new RevWalk(repo)) {
      RevCommit commit = walk.parseCommit(head.getObjectId());
      RevTree tree = walk.parseTree(commit.getTree().getId());

      CanonicalTreeParser treeParser = new CanonicalTreeParser();
      try (ObjectReader reader = repo.newObjectReader()) {
        treeParser.reset(reader, tree.getId());
      }
      walk.dispose();
      return treeParser;
    }
  }

  private void prepareCommands(
      final String targetBranch, final String repoSlug, final int pullRequestID) {

    try {
      Git og_repo = Git.cloneRepository()
         .setURI(String.format("https://github.com/%s.git", repoSlug))
         .setDirectory(new File(ORIGINAL_SOURCE_FOLDER))
         .setBranch(targetBranch)
         .call();
      Git mod_repo = Git.cloneRepository()
         .setURI(String.format("https://github.com/%s.git", repoSlug))
         .setDirectory(new File(MODIFIED_SOURCE_FOLDER))
         .setBranch(targetBranch)
         .call();
      mod_repo.fetch()
         .setRemote("origin")
         .setRefSpecs(new RefSpec(String.format("refs/pull/%d/head:refs/remotes/origin/pr/%d", pullRequestID, pullRequestID)))
         .call();

      AbstractTreeIterator oldTree = prepareTreeParser(mod_repo.getRepository(), "FETCH_HEAD");
      AbstractTreeIterator newTree = prepareTreeParser(mod_repo.getRepository(), "refs/heads/" + targetBranch);
      List<DiffEntry> changed_files =
        mod_repo.diff()
          .setShowNameAndStatusOnly(true)
          .setOldTree(oldTree)
          .setNewTree(newTree)
          .call();

      CheckoutCommand ogCheckout = og_repo.checkout().setStartPoint(targetBranch);
      CheckoutCommand modifiedCheckout = mod_repo.checkout().setStartPoint("FETCH_HEAD");
      for (DiffEntry changed_file : changed_files) {
        if (changed_file.getChangeType() == DiffEntry.ChangeType.MODIFY) {
          ogCheckout.addPath(changed_file.getNewPath()); // newPath == oldPath because its modified, not moved/added/removed
          modifiedCheckout.addPath(changed_file.getNewPath());
        }
      }
      ogCheckout.call();
      modifiedCheckout.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
