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
package edu.montana.gsoc.msusel.rbml.conformance
/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class ChainIdentifier {

//    def findChains(System system) {
//        int index = 0
//        def chains = system.patternChains
//        if (!chains) {
//            chains = createChains(system.projects[0].patterns)
//            index = 1
//        }
//
//        (index..(system.projects.size())).each { i ->
//            system.projects[i].patterns.each { p ->
//                PatternChain chain
//                chains.each { c ->
//                    if (c.matches(p)) {
//                        chain = c
//                        return
//                    }
//                }
//
//                if (chain)
//                    chain << p
//                else
//                    createChain(p)
//            }
//        }
//    }
//
//    List<PatternChain> createChains(List<PatternInstance> insts) {
//        List<PatternChain> chains = []
//        insts.each {
//            chains << createChain(it)
//        }
//        chains
//    }
//
//    PatternChain createChain(PatternInstance inst) {
//
//    }
}
