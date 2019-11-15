/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-Rémy Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Floréal Morandat <florealm@gmail.com>
 */

package com.github.gumtreediff.client.diff.web;

import com.github.gumtreediff.actions.ChawatheScriptGenerator;
import com.github.gumtreediff.client.Option;
import com.github.gumtreediff.client.Register;
import com.github.gumtreediff.client.diff.AbstractDiffClient;
import com.github.gumtreediff.gen.Registry;
import com.github.gumtreediff.io.DirectoryComparator;
import com.github.gumtreediff.utils.Pair;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static spark.Spark.*;

@Register(description = "a web diff client", options = WebDiff.Options.class, priority = Registry.Priority.HIGH)
public class WebDiff extends AbstractDiffClient<WebDiff.Options> {

    public WebDiff(String[] args) {
        super(args);
    }

    static class Options extends AbstractDiffClient.Options {
        protected int defaultPort = Integer.parseInt(System.getProperty("gt.webdiff.port", "4567"));
        boolean stdin = true;

        @Override
        public Option[] values() {
            return Option.Context.addValue(super.values(),
                    new Option("--port", String.format("set server port (default to %d)", defaultPort), 1) {
                        @Override
                        protected void process(String name, String[] args) {
                            int p = Integer.parseInt(args[0]);
                            if (p > 0)
                                defaultPort = p;
                            else
                                System.err.printf("Invalid port number (%s), using %d\n", args[0], defaultPort);
                        }
                    },
                    new Option("--no-stdin", String.format("Do not listen to stdin"), 0) {
                        @Override
                        protected void process(String name, String[] args) {
                            stdin = false;
                        }
                    }
            );
        }
    }

    @Override
    protected Options newOptions() {
        return new Options();
    }

    @Override
    public void run() {
        DirectoryComparator comparator = new DirectoryComparator(opts.src, opts.dst);
        comparator.compare();
        for (Pair<File, File> pair: comparator.getModifiedFiles()) {
            try {
                Renderable view = new DiffView(pair.first, pair.second,
                        this.getTreeContext(pair.first.getAbsolutePath()),
                        this.getTreeContext(pair.second.getAbsolutePath()),
                        getMatcher(),
                        new ChawatheScriptGenerator());
                System.out.println(render(view));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String render(Renderable r) {
        HtmlCanvas c = new HtmlCanvas();
        try {
            r.renderOn(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c.toHtml();
    }

    private static String readFile(String path, Charset encoding)  throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}