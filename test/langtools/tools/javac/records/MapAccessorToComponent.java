/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @summary test for javax.lang.model.util.Elements::recordComponentFor
 * @modules jdk.compiler
 */

import java.io.IOException;
import java.net.URI;
import java.util.List;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import com.sun.source.util.JavacTask;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementFilter;

import static javax.tools.JavaFileObject.Kind.SOURCE;

public class MapAccessorToComponent {
    public static void main(String... args) throws IOException {
        new MapAccessorToComponent().run();
    }

    public void run() throws IOException {
        String code = "record R(int val1, int val2) {}";
        JavaCompiler c = ToolProvider.getSystemJavaCompiler();
        JavacTask t = (JavacTask) c.getTask(null, null, null, null, null,
                List.of(new MyFileObject(code)));
        TypeElement record = (TypeElement) t.analyze().iterator().next();
        for (RecordComponentElement rce : ElementFilter.recordComponentsIn(record.getEnclosedElements())) {
            ExecutableElement accessor = rce.getAccessor();
            if (!Objects.equals(t.getElements().recordComponentFor(accessor), rce)) {
                throw new AssertionError("Did not find the correct record component!");
            }
        }
    }

    class MyFileObject extends SimpleJavaFileObject {
        private final String code;

        MyFileObject(String code) {
            super(URI.create("myfo:///Test.java"), SOURCE);
            this.code = code;
        }
        @Override
        public String getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
