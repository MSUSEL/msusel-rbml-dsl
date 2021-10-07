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
package edu.montana.gsoc.msusel.rbml.factory

import edu.montana.gsoc.msusel.rbml.events.SourceTypeResolution
import edu.montana.gsoc.msusel.rbml.model.Multiplicity
import edu.montana.gsoc.msusel.rbml.PatternManager
import edu.montana.gsoc.msusel.rbml.model.Association

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class SourceFactory extends AbstractFactory {

    boolean isLeaf() {
        return false
    }

    def newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        Association assoc = null
        
        if (builder.current != null && builder.current instanceof Association) {
            assoc = (Association) builder.current
            
            int lower = 1
            int upper = 1
            if (attributes["mult"] != null) {
                String mult = attributes["mult"]
                mult = mult.replaceAll("\\*", "-1")
                if (mult.contains("..")) {
                    lower = new Integer(mult.split("\\.\\.")[0])
                    upper = new Integer(mult.split("\\.\\.")[1])
                }
            }
            if (attributes["role"] != null)
                PatternManager.instance.events << new SourceTypeResolution(role: assoc, type: attributes["role"])
            assoc.setSourceMult(new Multiplicity(lower: lower, upper: upper))
            assoc.setSourceName(value)
        }
        attributes.clear()
        
        assoc
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object child) {
    }
}
