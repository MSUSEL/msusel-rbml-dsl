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

    def "generate role blocks"(resource, blocks) {
        given:
        String text = getClass().getResource("/rbmldef/${resource}.yml").readLines().join('\n')
        Yaml yaml = new Yaml()
        SpecificationReader reader = new SpecificationReader()

        when:
        def map = yaml.load(text)
        reader.processSPS(map)

        def rblocks = reader.sps.roleBlocks()

        then:
            rblocks.size() == blocks

        where:
        resource                  | blocks
        'abstract_factory'        | 7
        'adapter'                 | 5
        'bridge'                  | 3
        'builder'                 | 4
        'chain_of_responsibility' | 3
        'command'                 | 5
        'composite'               | 3
        'decorator'               | 5
        'facade'                  | 1
        'factory_method'          | 3
        'flyweight'               | 5
        'interpreter'             | 3
        'iterator'                | 5
        'mediator'                | 4
        'memento'                 | 2
        'observer'                | 3
        'prototype'               | 2
        'singleton'               | 1
        'strategy'                | 2
        'state'                   | 2
        'template_method'         | 1
        'visitor'                 | 5
    }
}