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

import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.Role
import spock.lang.Specification


class SharingConstraintSpec extends Specification {

    def "setting a sharing constraint between two role blocks"() {
        given:
            RoleBlock first = new RoleBlock()
            RoleBlock second = new RoleBlock()

        when:
            SharingConstraint sc = SharingConstraint.of(first, second)

        then:
            sc != null
            sc.first != null
            sc.second != null
    }

    def "first role is null"() {
        given:
            RoleBlock first = null
            RoleBlock second = new RoleBlock()

        when:
            SharingConstraint sc = SharingConstraint.of(first, second)

        then:
            thrown IllegalArgumentException
    }

    def "second role is null"() {
        given:
            RoleBlock second = null
            RoleBlock first = new RoleBlock()

        when:
            SharingConstraint sc = SharingConstraint.of(first, second)

        then:
            thrown IllegalArgumentException
    }

    def "setting the role which is shared between two role blocks"() {
        given:
            Role r = Classifier.builder().name("Test").create()
            SharingConstraint sc = SharingConstraint.of(new RoleBlock(), new RoleBlock())

        when:
            sc.on(r)

        then:
            sc.shared == r
    }

    def "shared role is null"() {
        given:
            Role r = null
            SharingConstraint sc = SharingConstraint.of(new RoleBlock(), new RoleBlock())

        when:
            sc.on(r)

        then:
            thrown IllegalArgumentException
    }

    def "evaluate"() {

    }
}