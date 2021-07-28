/**
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
package edu.montana.gsoc.msusel.rbml.model

import groovy.transform.builder.Builder
import groovy.util.logging.Log4j2

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@Log4j2
class Classifier extends Role {

    List<StructuralFeature> structFeats = []
    List<BehavioralFeature> behFeats = []
    boolean abstrct
    boolean root = false
    boolean ghMember = false

    @Builder(buildMethodName = "create")
    Classifier(String name, Multiplicity mult) {
        super(name, mult)
        abstrct = true
    }

    Classifier() {
        abstrct = true
    }

    StructuralFeature getStructuralFeatureByName(String name) {
        structFeats.find {it.name == name }
    }

    BehavioralFeature getBehavioralFeatureByName(String name) {
        behFeats.find {it.name == name }
    }

    Role findFeatureByName(String name) {
        if (!name)
            return null

        Role r = getStructuralFeatureByName(name)
        if (!r)
            r = getBehavioralFeatureByName(name)

        return r
    }
}
