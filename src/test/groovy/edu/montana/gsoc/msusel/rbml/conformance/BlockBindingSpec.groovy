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
package edu.montana.gsoc.msusel.rbml.conformance

import edu.isu.isuese.datamodel.Class
import edu.isu.isuese.datamodel.Type
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.Role
import org.javalite.activejdbc.test.DBSpec
import org.junit.Test

class BlockBindingSpec extends DBSpec {

    @Test
    void "create a block binding from of"() {
        Role src = Classifier.builder().name("src").create()
        Role dest = Classifier.builder().name("dest").create()
        RoleBlock rb = RoleBlock.of(src, dest)
        Type srcT = Class.builder().name("SrcImpl").compKey("SrcImpl").create()
        Type destT = Class.builder().name("DestImpl").compKey("DestImpl").create()
        ModelBlock mb = ModelBlock.of(srcT, destT)

        def binding = BlockBinding.of(rb, mb)

        a(binding).shouldNotBeNull()
        a(binding.rb).shouldEqual(rb)
        a(binding.mb).shouldEqual(mb)
        a(binding.roleBindings.size()).shouldEqual(2)
        a(binding.roleBindings[0].role).shouldEqual(src)
        a(binding.roleBindings[1].role).shouldEqual(dest)
        a(binding.roleBindings[0].type).shouldEqual(srcT)
        a(binding.roleBindings[1].type).shouldEqual(destT)
    }

    @Test
    void "create a block binding from fo"() {
        Role src = Classifier.builder().name("src").create()
        Role dest = Classifier.builder().name("dest").create()
        RoleBlock rb = RoleBlock.of(src, dest)
        Type srcT = Class.builder().name("SrcImpl").compKey("SrcImpl").create()
        Type destT = Class.builder().name("DestImpl").compKey("DestImpl").create()
        ModelBlock mb = ModelBlock.of(srcT, destT)

        def binding = BlockBinding.fo(rb, mb)

        a(binding).shouldNotBeNull()
        a(binding.rb).shouldEqual(rb)
        a(binding.mb).shouldEqual(mb)
        a(binding.roleBindings.size()).shouldEqual(2)
        a(binding.roleBindings[0].role).shouldEqual(src)
        a(binding.roleBindings[1].role).shouldEqual(dest)
        a(binding.roleBindings[0].type).shouldEqual(destT)
        a(binding.roleBindings[1].type).shouldEqual(srcT)
    }

    @Test(expected = IllegalArgumentException.class)
    void "create a block binding with null role block"() {
        Type srcT = Class.builder().name("SrcImpl").compKey("SrcImpl").create()
        Type destT = Class.builder().name("DestImpl").compKey("DestImpl").create()
        ModelBlock mb = ModelBlock.of(srcT, destT)

        def binding = BlockBinding.fo(null, mb)
    }

    @Test(expected = IllegalArgumentException.class)
    void "create a block binding with null model block"() {
        Role src = Classifier.builder().name("src").create()
        Role dest = Classifier.builder().name("dest").create()
        RoleBlock rb = RoleBlock.of(src, dest)

        def binding = BlockBinding.fo(rb, null)
    }
}