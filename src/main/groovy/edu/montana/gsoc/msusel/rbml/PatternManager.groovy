/*
 * The MIT License (MIT)
 *
 * MSUSEL RBML DSL
 * Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory and Idaho State University, Informatics and
 * Computer Science, Empirical Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.rbml

import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.GeneralizationHierarchy
import edu.montana.gsoc.msusel.rbml.model.Lifeline
import edu.montana.gsoc.msusel.rbml.model.Pattern

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Singleton
class PatternManager {

    Pattern current
    List<Pattern> patterns = []
    def events = []

    void reset() {
        patterns = []
        current = null
        events = []
    }

    Classifier findClassifier(String name) {
        for (def r : current.sps.classifiers) {
            if (r instanceof GeneralizationHierarchy) {
                if (name == r.getName())
                    return r.root
                else {
                    for (def x : r.children) {
                        if (name == x.getName())
                            return x
                    }
                }
            } else if (name == r.getName()) {
                return r
            }
        }

        null
    }

    Lifeline findLifeline(String name) {
        current.ips.lines.find { it.getName() == name }
    }

    void processEvents() {
        events.each {
            it.resolve()
        }
        events.clear()
    }
}
