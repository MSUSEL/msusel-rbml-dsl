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
package edu.montana.gsoc.msusel.rbml.model

import edu.montana.gsoc.msusel.rbml.io.SpecificationReader
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification


class SPSSpec extends Specification {

    def "generate role blocks"() {
        given:
            def spec = '''\
                SPS:
                    name: test
                    roles:
                        - Classifier:
                            name: X
                        - Classifier:
                            name: Y
                        - Class:
                            name: Z
                        - Class:
                            name: W
                    relations:
                        - Association:
                            name: XY
                            mult: 1..1
                            source:
                                name: x
                                mult: 1..*
                                type: X
                            dest:
                                name: y
                                mult: 1..*
                                type: Y
                        - Generalization:
                            name: zy
                            mult: 0..1
                            child: Z
                            parent: Y
            '''
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processSPS(map)
            def rblocks = reader.sps.roleBlocks()

        then:
            rblocks.size() == 2
    }
}