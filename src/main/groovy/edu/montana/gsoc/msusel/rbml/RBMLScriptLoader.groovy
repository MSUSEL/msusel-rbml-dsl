/**
 * The MIT License (MIT)
 *
 * MSUSEL RBML DSL
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
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

import edu.montana.gsoc.msusel.rbml.model.Pattern
import org.codehaus.groovy.control.CompilerConfiguration

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class RBMLScriptLoader {

    static def load(String file) {
        def conf = new CompilerConfiguration()
        conf.setScriptBaseClass("edu.montana.gsoc.msusel.rbml.RBMLScript")
        def shell = new GroovyShell(this.class.classLoader, new Binding(), conf)
        Pattern rbml = shell.evaluate(new File(file))
        PatternManager.instance.processEvents()

        rbml
    }
    
    static void main(String[] args) {
        new RBMLScriptLoader().load("/home/git/models/test.rbml")
    }
}
