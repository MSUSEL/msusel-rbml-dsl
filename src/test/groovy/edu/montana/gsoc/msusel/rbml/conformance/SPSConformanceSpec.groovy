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

import edu.isu.isuese.datamodel.*
import edu.montana.gsoc.msusel.rbml.conformance.RoleBinding
import edu.montana.gsoc.msusel.rbml.io.SpecificationReader
import edu.montana.gsoc.msusel.rbml.model.*
import org.apache.commons.lang3.tuple.Pair
import org.javalite.activejdbc.test.DBSpec
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import static org.junit.Assert.fail

class SPSConformanceSpec extends DBSpec {

    System sys

    @Test
    void testConforms() {
        SPS sps = createSPS()
        PatternInstance inst = createConformingPatternInstance()
        SPSConformance fixture = new SPSConformance()
        def tuple = fixture.conforms(sps, inst)

        the(tuple[0]).shouldEqual(0.75) // TODO Look into this
        the(tuple[1]).shouldEqual(0.8) // TODO Look into this
        the(tuple[2].size()).shouldEqual(0)
        the(tuple[3].size()).shouldEqual(8)
    }

    @Test(expected = IllegalArgumentException.class)
    void testConforms_null_null() {
        SPS sps = null
        PatternInstance inst = null
        SPSConformance fixture = new SPSConformance()
        fixture.conforms(sps, inst)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testConforms_ok_null() {
        SPS sps = createSPS()
        PatternInstance inst = null
        SPSConformance fixture = new SPSConformance()
        fixture.conforms(sps, inst)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testConforms_null_ok() {
        SPS sps = null
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        fixture.conforms(sps, inst)
        fail()
    }

    @Test
    void testConforms_empty_empty() {
        SPS sps = new SPS()
        PatternInstance inst = new PatternInstance()
        SPSConformance fixture = new SPSConformance()
        Tuple tuple = fixture.conforms(sps, inst)

        the(tuple[0]).shouldEqual(0.0)
        the(tuple[1]).shouldEqual(0.0)
        the(tuple[2].size()).shouldEqual(0)
        the(tuple[3].size()).shouldEqual(0)
    }

    @Test
    void testConforms_ok_empty() {
        SPS sps = createSPS()
        PatternInstance inst = new PatternInstance()
        SPSConformance fixture = new SPSConformance()
        Tuple tuple = fixture.conforms(sps, inst)

        the(tuple[0]).shouldEqual(0.0)
        the(tuple[1]).shouldEqual(0.0)
        the(tuple[2].size()).shouldEqual(0)
        the(tuple[3].size()).shouldEqual(0)
    }

    @Test
    void testConforms_empty_ok() {
        SPS sps = new SPS()
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        def tuple = fixture.conforms(sps, inst)

        the(tuple[0]).shouldEqual(0.0)
        the(tuple[1]).shouldEqual(0.0)
        the(tuple[2].size()).shouldEqual(3)
        the(tuple[3].size()).shouldEqual(0)
    }

    @Test
    void testGetNonConformingModelBlocks() {
        SPS sps = createSPS()
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        List<RoleBlock> rbs = sps.roleBlocks()
        Random rand = new Random()
        List<ModelBlock> blocks = fixture.getModelBlocks(inst)
        blocks.each {
            RoleBlock rb = rbs[rand.nextInt(rbs.size())]
            if (!mapping[rb])
                mapping[rb] = []
            mapping[rb] << BlockBinding.of(rb, it)
        }

        List<ModelBlock> result = fixture.getNonConformingModelBlocks(mapping, inst)

        the(result.size()).shouldEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetNonConformingModelBlocks_null_null() {
        Map<RoleBlock, List<BlockBinding>> mapping = null
        SPSConformance fixture = new SPSConformance()
        List<ModelBlock> blocks = fixture.getNonConformingModelBlocks(mapping, null)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetNonConformingModelBlocks_null_ok() {
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        fixture.getNonConformingModelBlocks(null, inst)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetNonConformingModelBlocks_ok_null() {
        SPS sps = createSPS()
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        Map<RoleBlock, List<BlockBinding>> mapping = createMapping(fixture, sps, inst)
        fixture.getNonConformingModelBlocks(mapping, null)
        fail()
    }

    @Test
    void testGetNonConformingModelBlocks_empty_ok() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        SPSConformance fixture = new SPSConformance()
        PatternInstance inst = createPatternInstance()
        def mbs = fixture.getModelBlocks(inst)
        List<ModelBlock> blocks = fixture.getNonConformingModelBlocks(mapping, inst)

        the(blocks.size()).shouldEqual(mbs.size())
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetNonConformingModelBlocks_empty_null() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        SPSConformance fixture = new SPSConformance()
        fixture.getNonConformingModelBlocks(mapping, null)
        fail()
    }

    @Test
    void testGetConformingModelBlocks() {
        PatternInstance inst = createPatternInstance()
        SPS sps = createSPS()
        SPSConformance fixture = new SPSConformance()
        Map<RoleBlock, List<BlockBinding>> mapping = createMapping(fixture, sps, inst)
        List<ModelBlock> blocks = fixture.getConformingModelBlocks(mapping)

        the(blocks.size()).shouldEqual(15)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetConformingModelBlocks_null() {
        Map<RoleBlock, List<BlockBinding>> mapping = null
        SPSConformance fixture = new SPSConformance()
        fixture.getConformingModelBlocks(mapping)
        fail()
    }

    @Test
    void testGetConformingModelBlocks_empty() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        SPSConformance fixture = new SPSConformance()
        List<ModelBlock> blocks = fixture.getConformingModelBlocks(mapping)
        a(blocks.isEmpty()).shouldBeTrue()
    }

    @Test
    void testGetUnboundRoles() {
        PatternInstance inst = createPatternInstance()
        SPS sps = createSPS()
        SPSConformance fixture = new SPSConformance()
        List<ModelBlock> mbs = fixture.getModelBlocks(inst)
        List<RoleBlock> rbs = sps.roleBlocks()
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        mapping[rbs[0]] = [BlockBinding.of(rbs[0], mbs[0])]
        List<RoleBlock> roles = fixture.getUnboundRoles(mapping, sps)

        a(roles.size()).shouldBeEqual(rbs.size() - 1)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundRoles_null_null() {
        Map<RoleBlock, List<BlockBinding>> mapping = null
        SPS sps = null
        SPSConformance fixture = new SPSConformance()
        fixture.getUnboundRoles(mapping, sps)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundRoles_null_ok() {
        Map<RoleBlock, List<BlockBinding>> mapping = null
        SPS sps = null
        SPSConformance fixture = new SPSConformance()
        fixture.getUnboundRoles(mapping, sps)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundRoles_ok_null() {
        SPS sps = null
        SPSConformance fixture = new SPSConformance()
        PatternInstance inst = createPatternInstance()
        Map<RoleBlock, List<BlockBinding>> mapping = createMapping(fixture, sps, inst)
        fixture.getUnboundRoles(mapping, sps)
        fail()
    }

    @Test
    void tetGetUnboundRoles_empty_empty() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        SPS sps = new SPS()
        SPSConformance fixture = new SPSConformance()
        List<RoleBlock> roles = fixture.getUnboundRoles(mapping, sps)
        the(roles.isEmpty()).shouldBeTrue()
    }

    @Test
    void testGetUnboundRoles_empty_ok() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        SPS sps = createSPS()
        SPSConformance fixture = new SPSConformance()
        List<RoleBlock> roles = fixture.getUnboundRoles(mapping, sps)
        the(roles.isEmpty()).shouldBeFalse()
    }

    @Test
    void testGetUnboundRoles_ok_empty() {
        SPS sps = new SPS()
        PatternInstance inst = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        Map<RoleBlock, List<BlockBinding>> mapping = createMapping(fixture, sps, inst)
        List<RoleBlock> roles = fixture.getUnboundRoles(mapping, sps)
        the(roles.isEmpty()).shouldBeTrue()
    }

    @Test
    void testCreateFeatureBindings() {
        RoleBinding rb = createRoleBinding()
        SPSConformance fixture = new SPSConformance()
        List<FeatureBinding> bindings = fixture.createFeatureBindings(rb)

        the(bindings.size()).shouldEqual(2)
    }

    @Test(expected = IllegalArgumentException.class)
    void testCreateFeatureBindings_null() {
        RoleBinding rb = null
        SPSConformance fixture = new SPSConformance()
        fixture.createFeatureBindings(rb)
        fail()
    }

    @Test
    void testCreateStructuralFeatureBindings() {
        RoleBinding rb = createRoleBinding()
        SPSConformance fixture = new SPSConformance()
        List<FeatureBinding> bindings = fixture.createStructuralFeatureBindings(rb)

        the(bindings.size()).shouldEqual(1)
    }

    @Test
    void testCreateBehavioralFeatureBindings() {
        RoleBinding rb = createRoleBinding()
        SPSConformance fixture = new SPSConformance()
        List<FeatureBinding> bindings = fixture.createBehavioralFeatureBindings(rb)

        the(bindings.size()).shouldEqual(1)
    }

    @Test
    void testGetUnboundTypes() {
        PatternInstance pi = createPatternInstance()
        SPSConformance fixture = new SPSConformance()
        SPS sps = createSPS()
        List<ModelBlock> mbs = fixture.getModelBlocks(pi)
        List<RoleBlock> rbs = sps.roleBlocks()
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        mapping[rbs[0]] = [BlockBinding.of(rbs[0], mbs[0])]

        List<Type> unbound = fixture.getUnboundTypes(mapping, pi)

        the(unbound.size()).shouldEqual((mbs.size() - 1) * 2)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundTypes_null_null() {
        PatternInstance pi = null
        Map<RoleBlock, List<BlockBinding>> mapping = null

        SPSConformance fixture = new SPSConformance()
        fixture.getUnboundTypes(mapping, pi)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundTypes_ok_null() {
        PatternInstance pi = null
        Map<RoleBlock, List<BlockBinding>> mapping = [:]

        SPSConformance fixture = new SPSConformance()
        fixture.getUnboundTypes(mapping, pi)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetUnboundTypes_null_ok() {
        PatternInstance pi = createPatternInstance()
        Map<RoleBlock, List<BlockBinding>> mapping = null

        SPSConformance fixture = new SPSConformance()
        fixture.getUnboundTypes(mapping, pi)
    }

    @Test
    void testGetUnboundTypes_empty_ok() {
        PatternInstance pi = createPatternInstance()
        Map<RoleBlock, List<BlockBinding>> mapping = [:]

        SPSConformance fixture = new SPSConformance()
        List<Type> unbound = fixture.getUnboundTypes(mapping, pi)

        the(unbound.size()).shouldBeEqual(pi.types.size())
    }

    @Test
    void testGetModelBlocks() {
        PatternInstance pi = createPatternInstance()

        a(pi).shouldNotBeNull()

        SPSConformance fixture = new SPSConformance()
        def modelBlocks = fixture.getModelBlocks(pi)

        a(modelBlocks.size()).shouldEqual(3)
    }

    @Test
    void testGetGenRealDestTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getGenRealDestTypes(map["B"])

        a(types.size()).shouldEqual(3)
    }

    @Test
    void testGetAssocDestTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getAssocDestTypes(map["E"])

        a(types.size()).shouldEqual(3)
    }

    @Test
    void testGetDependDestTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getDependDestTypes(map["J"])

        a(types.size()).shouldEqual(1)
    }

    @Test
    void testGetGenRealSrcTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getGenRealSrcTypes(map["F"])

        a(types.size()).shouldEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetGenRealSrcTypes_null() {
        SPSConformance fixture = new SPSConformance()

        fixture.getGenRealSrcTypes(null)
    }

    @Test
    void testGetAssocSrcTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getAssocSrcTypes(map["H"])

        a(types.size()).shouldEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void testGetAssocSrcTypes_null() {
        SPSConformance fixture = new SPSConformance()
        fixture.getAssocSrcTypes(null)
    }

    @Test
    void testGetDependSrcTypes() {
        def map = buildModelWithRelations()

        SPSConformance fixture = new SPSConformance()

        Set<Type> types = fixture.getDependSrcTypes(map["D"])

        a(types.size()).shouldEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void testCheckBlockConformance_null_mb() {
        SPSConformance fixture = new SPSConformance()

        Type src = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        fixture.checkBlockConformance(null, mb)
    }

    @Test(expected = IllegalArgumentException.class)
    void testCheckBlockConformance_rb_null() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role src = edu.montana.gsoc.msusel.rbml.model.Classifier.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role dest = edu.montana.gsoc.msusel.rbml.model.Classifier.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(src, dest)

        fixture.checkBlockConformance(rb, null)
    }

    @Test
    void testCheckBlockConformance_conforms() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = edu.montana.gsoc.msusel.rbml.model.Classifier.builder()
                .name("RoleA")
                .mult(Multiplicity.fromString("1..1"))
                .create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = ClassRole.builder()
                .name("RoleB")
                .mult(Multiplicity.fromString("1..1"))
                .create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.INTERFACE)
                .compKey("ClassA")
                .name("Class A")
                .create()
        Type dest = Type.builder().type(Type.CLASS)
                .compKey("ClassB")
                .name("Class B")
                .create()
        ModelBlock mb = ModelBlock.of(src, dest)

        boolean value = fixture.checkBlockConformance(rb, mb)
        a(value).shouldBeTrue()
    }

    @Test
    void testCheckBlockConformance_conforms2() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = ClassRole.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = ClassRole.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        boolean value = fixture.checkBlockConformance(rb, mb)
        a(value).shouldBeTrue()
    }

    @Test
    void testCheckBlockConformance_real() {
        SPSConformance fixture = new SPSConformance()

        PatternInstance pi = createPatternInstance()
        SPS sps = createSPS()

        List<ModelBlock> modelBlocks = fixture.getModelBlocks(pi)

        sps.roleBlocks().each { RoleBlock rb ->
            modelBlocks.each { ModelBlock mb ->
                println("conformance: ${fixture.checkBlockConformance(rb, mb)}")
            }
        }
    }

    @Test
    void testCheckBlockConformance_nonconforms() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = ClassRole.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = ClassRole.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.INTERFACE).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.ENUM).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        boolean value = fixture.checkBlockConformance(rb, mb)
        a(value).shouldBeFalse()
    }

    @Test
    void testCheckBlockConformance_nonconforms2() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = ClassRole.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = InterfaceRole.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        boolean value = fixture.checkBlockConformance(rb, mb)
        a(value).shouldBeFalse()
    }

    @Test(expected = IllegalArgumentException.class)
    void testCheckLocalConformance_null() {
        SPSConformance fixture = new SPSConformance()

        fixture.checkLocalConformance(null)
    }

    @Test
    void testCheckLocalConformance_conforms() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = ClassRole.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = InterfaceRole.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        BlockBinding bb = BlockBinding.of(rb, mb)

        double value = fixture.checkLocalConformance(bb)

        the(value).shouldEqual(1.0d)
    }

    @Test
    void testCheckLocalConformance_nonconforms() {
        SPSConformance fixture = new SPSConformance()

        edu.montana.gsoc.msusel.rbml.model.Role srcR = ClassRole.builder().name("RoleA").mult(Multiplicity.fromString("1..1")).create()
        edu.montana.gsoc.msusel.rbml.model.Role destR = InterfaceRole.builder().name("RoleB").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock rb = RoleBlock.of(srcR, destR)

        Type src = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type dest = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        ModelBlock mb = ModelBlock.of(src, dest)

        BlockBinding bb = BlockBinding.of(rb, mb)

        double value = fixture.checkLocalConformance(bb)

        the(value).shouldEqual(1.0)
    }

    @Test
    void testSharingConstraint() {
        edu.montana.gsoc.msusel.rbml.model.Role roleA = ClassRole.builder().name("Role A").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleB = ClassRole.builder().name("Role B").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleC = ClassRole.builder().name("Role C").create()

        Type classA = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type classB = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        Type classC = Type.builder().type(Type.CLASS).compKey("ClassC").name("Class C").create()

        RoleBlock rb1 = RoleBlock.of(roleA, roleB)
        RoleBlock rb2 = RoleBlock.of(roleB, roleC)

        ModelBlock mb1 = ModelBlock.of(classA, classB)
        ModelBlock mb2 = ModelBlock.of(classB, classC)

        SharingConstraint sc1 = SharingConstraint.of(rb1, rb2)
        sc1.on(roleB)
        def constraints = [sc1]

        def map = [rb1 : constraints, rb2 : constraints]

        SPSConformance conform = new SPSConformance()

        BlockBinding binding1 = BlockBinding.of(rb1, mb1)
        BlockBinding binding2 = BlockBinding.of(rb2, mb2)

        boolean val1 = conform.sharingConstraint(rb1, binding1, map)
        boolean val2 = conform.sharingConstraint(rb2, binding2, map)

        a(val1).shouldBeTrue()
        a(val2).shouldBeTrue()
    }

    @Test
    void testSharingConstraint_2() {
        edu.montana.gsoc.msusel.rbml.model.Role roleA = ClassRole.builder().name("Role A").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleB = ClassRole.builder().name("Role B").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleC = ClassRole.builder().name("Role C").create()

        Type classA = Type.builder().type(Type.CLASS).compKey("ClassA").name("Class A").create()
        Type classB = Type.builder().type(Type.CLASS).compKey("ClassB").name("Class B").create()
        Type classC = Type.builder().type(Type.CLASS).compKey("ClassC").name("Class C").create()
        Type classD = Type.builder().type(Type.CLASS).compKey("ClassD").name("Class D").create()

        RoleBlock rb1 = RoleBlock.of(roleA, roleB)
        RoleBlock rb2 = RoleBlock.of(roleB, roleC)

        ModelBlock mb1 = ModelBlock.of(classA, classB)
        ModelBlock mb2 = ModelBlock.of(classB, classC)
        ModelBlock mb3 = ModelBlock.of(classD, classC)

        SharingConstraint sc1 = SharingConstraint.of(rb1, rb2)
        sc1.on(roleB)
        def constraints = [sc1]

        def map = [rb1 : constraints, rb2 : constraints]

        SPSConformance conform = new SPSConformance()

        BlockBinding binding1 = BlockBinding.of(rb1, mb1)
        BlockBinding binding2 = BlockBinding.of(rb2, mb2)


        boolean val1 = conform.sharingConstraint(rb1, binding1, map)
        boolean val2 = conform.sharingConstraint(rb2, binding2, map)

        a(val1).shouldBeTrue()
        a(val2).shouldBeTrue()
    }

    @Test
    void testFindSharingConstraints() {
        edu.montana.gsoc.msusel.rbml.model.Role roleA = ClassRole.builder().name("Role A").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleB = ClassRole.builder().name("Role B").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleC = ClassRole.builder().name("Role C").create()
        edu.montana.gsoc.msusel.rbml.model.Role roleD = ClassRole.builder().name("Role D").create()

        def blocks = []
        blocks << RoleBlock.of(roleA, roleB)
        blocks << RoleBlock.of(roleB, roleC)
        blocks << RoleBlock.of(roleA, roleD)

        SPSConformance fixture = new SPSConformance()

        def map = fixture.findSharingConstraints(blocks)

        a(map[blocks[0]].size()).shouldEqual(2)
        a(map[blocks[1]].size()).shouldEqual(1)
        a(map[blocks[2]].size()).shouldEqual(1)
    }

    @Test(expected = IllegalArgumentException.class)
    void testFindSharingConstraints_null() {
        SPSConformance fixture = new SPSConformance()

        def map = fixture.findSharingConstraints(null)
    }

    @Test
    void testFindSharingConstraints_empty() {
        SPSConformance fixture = new SPSConformance()

        def map = fixture.findSharingConstraints([])

        a(map.isEmpty()).shouldBeTrue()
    }

    @Test
    void testPairs() {
        def blocks = []
        for (int i = 0; i < 8; i++) {
            edu.montana.gsoc.msusel.rbml.model.Role t = edu.montana.gsoc.msusel.rbml.model.Classifier.builder().name("$i").create()
            blocks << RoleBlock.of(t, t)
        }

        SPSConformance conform = new SPSConformance()
        def pairs = conform.pairs(blocks)

        a(pairs.size()).shouldEqual((blocks.size() * (blocks.size() - 1)) / 2)
    }

    @Test
    void testPairs_Empty() {
        def blocks = []

        SPSConformance conform = new SPSConformance()
        def pairs = conform.pairs(blocks)

        a(pairs.size()).shouldEqual((blocks.size() * (blocks.size() - 1)) / 2)
    }

    @Test(expected = IllegalArgumentException.class)
    void testPairs_Null() {
        def blocks = null

        SPSConformance conform = new SPSConformance()
        conform.pairs(blocks)
    }

    @Test
    void testRealizationMult() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        ClassRole A = ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create()
        ClassRole B = ClassRole.builder().name("B").mult(Multiplicity.fromString("1..*")).create()
        ClassRole C = ClassRole.builder().name("C").mult(Multiplicity.fromString("0..*")).create()
        ClassRole D = ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock AB = RoleBlock.of(A, B)
        RoleBlock BC = RoleBlock.of(B, C)
        RoleBlock CD = RoleBlock.of(C, D)
        RoleBlock DA = RoleBlock.of(D, A)
        Type a = Type.builder().type(Type.CLASS).name("a").compKey("a").create()
        Type b = Type.builder().type(Type.CLASS).name("b").compKey("b").create()
        Type c = Type.builder().type(Type.CLASS).name("c").compKey("c").create()
        Type d = Type.builder().type(Type.CLASS).name("d").compKey("d").create()
        ModelBlock ab = ModelBlock.of(a, b)
        ModelBlock bc = ModelBlock.of(b, c)
        ModelBlock cd = ModelBlock.of(c, d)
        ModelBlock da = ModelBlock.of(d, a)
        mapping[AB] = [BlockBinding.of(AB, ab)]
        mapping[BC] = [BlockBinding.of(BC, bc)]
        mapping[CD] = [BlockBinding.of(CD, cd)]
        mapping[DA] = [BlockBinding.of(DA, da)]

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        double value = fixture.realizationMult(mapping, sat)

        the(value).shouldEqual(1.0)
    }

    @Test
    void testRealizationMult2() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        ClassRole A = ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create()
        ClassRole B = ClassRole.builder().name("B").mult(Multiplicity.fromString("1..*")).create()
        ClassRole C = ClassRole.builder().name("C").mult(Multiplicity.fromString("0..*")).create()
        ClassRole D = ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create()
        RoleBlock AB = RoleBlock.of(A, B)
        RoleBlock BC = RoleBlock.of(B, C)
        RoleBlock CD = RoleBlock.of(C, D)
        RoleBlock DA = RoleBlock.of(D, A)
        Type a = Type.builder().type(Type.CLASS).name("a").compKey("a").create()
        Type b = Type.builder().type(Type.CLASS).name("b").compKey("b").create()
        Type c = Type.builder().type(Type.CLASS).name("c").compKey("c").create()
        Type d = Type.builder().type(Type.CLASS).name("d").compKey("d").create()
        ModelBlock ab = ModelBlock.of(a, b)
        ModelBlock bc = ModelBlock.of(b, c)
        ModelBlock cd = ModelBlock.of(c, d)
        mapping[AB] = [BlockBinding.of(AB, ab)]
        mapping[BC] = [BlockBinding.of(BC, bc)]
        mapping[CD] = []
        mapping[DA] = []

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        double value = fixture.realizationMult(mapping, sat)

        the(value).shouldEqual(0.75)
    }

    @Test
    void testRealizationMult3() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        ClassRole A = ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create()
        ClassRole B = ClassRole.builder().name("B").mult(Multiplicity.fromString("0..*")).create()
        ClassRole C = ClassRole.builder().name("C").mult(Multiplicity.fromString("1..*")).create()
        ClassRole D = ClassRole.builder().name("D").mult(Multiplicity.fromString("1..*")).create()
        RoleBlock AB = RoleBlock.of(A, B)
        RoleBlock BC = RoleBlock.of(B, C)
        RoleBlock CD = RoleBlock.of(C, D)
        RoleBlock DA = RoleBlock.of(D, A)
        Type a = Type.builder().type(Type.CLASS).name("a").compKey("a").create()
        Type b = Type.builder().type(Type.CLASS).name("b").compKey("b").create()
        Type c = Type.builder().type(Type.CLASS).name("c").compKey("c").create()
        ModelBlock ab = ModelBlock.of(a, b)
        ModelBlock bc = ModelBlock.of(b, c)
        mapping[AB] = [BlockBinding.of(AB, ab)]
        mapping[BC] = []
        mapping[CD] = []
        mapping[DA] = []

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        double value = fixture.realizationMult(mapping, sat)

        the(value).shouldEqual(0.5)
    }

    @Test
    void testRealizationMult4() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        ClassRole A = ClassRole.builder().name("A").mult(Multiplicity.fromString("0..1")).create()
        ClassRole B = ClassRole.builder().name("B").mult(Multiplicity.fromString("1..*")).create()
        ClassRole C = ClassRole.builder().name("C").mult(Multiplicity.fromString("1..*")).create()
        ClassRole D = ClassRole.builder().name("D").mult(Multiplicity.fromString("1..*")).create()
        RoleBlock AA = RoleBlock.of(A, A)
        RoleBlock BC = RoleBlock.of(B, C)
        RoleBlock CD = RoleBlock.of(C, D)
        Type a = Type.builder().type(Type.CLASS).name("a").compKey("a").create()
        ModelBlock aa = ModelBlock.of(a, a)
        mapping[AA] = [BlockBinding.of(AA, aa)]
        mapping[BC] = []
        mapping[CD] = []

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        double value = fixture.realizationMult(mapping, sat)

        the(value).shouldEqual(0.25)
    }

    @Test(expected = IllegalArgumentException.class)
    void testRealizationMult_null() {
        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        fixture.realizationMult(null, sat)
    }

    @Test
    void testRealizationMult_empty() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        double value = fixture.realizationMult(mapping, sat)

        the(value).shouldEqual(0.0)
    }

    @Test(expected = IllegalArgumentException.class)
    void testRealizaitonMult_ok_null() {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        ClassRole A = ClassRole.builder().name("A").mult(Multiplicity.fromString("0..1")).create()
        ClassRole B = ClassRole.builder().name("B").mult(Multiplicity.fromString("1..*")).create()
        ClassRole C = ClassRole.builder().name("C").mult(Multiplicity.fromString("1..*")).create()
        ClassRole D = ClassRole.builder().name("D").mult(Multiplicity.fromString("1..*")).create()
        RoleBlock AA = RoleBlock.of(A, A)
        RoleBlock BC = RoleBlock.of(B, C)
        RoleBlock CD = RoleBlock.of(C, D)
        Type a = Type.builder().type(Type.CLASS).name("a").compKey("a").create()
        ModelBlock aa = ModelBlock.of(a, a)
        mapping[AA] = [BlockBinding.of(AA, aa)]
        mapping[BC] = []
        mapping[CD] = []

        SPSConformance fixture = new SPSConformance()
        Pair<List<edu.montana.gsoc.msusel.rbml.model.Role>, List<edu.montana.gsoc.msusel.rbml.model.Role>> sat = Pair.of([], [])
        fixture.realizationMult(mapping, null)
        fail()
    }

    @Test(expected = IllegalArgumentException.class)
    void testFeatureRoleBindings_null_item_item() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def bindings = [RoleBinding.of(roles[0], types[0]),
                        RoleBinding.of(roles[1], types[1]),
                        RoleBinding.of(roles[2], types[2])]

        fixture.featureRoleBindings(null, roles, bindings)
    }

    @Test
    void testFeatureRoleBindings_empty_item_item() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def bindings = [RoleBinding.of(roles[0], types[0]),
                        RoleBinding.of(roles[1], types[1]),
                        RoleBinding.of(roles[2], types[2])]

        def result = fixture.featureRoleBindings([], roles, bindings)
        the(result.size()).shouldEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void testFeatureRoleBindings_item_null_item() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def bindings = [RoleBinding.of(roles[0], types[0]),
                        RoleBinding.of(roles[1], types[1]),
                        RoleBinding.of(roles[2], types[2])]

        fixture.featureRoleBindings(types, null, bindings)
    }

    @Test
    void testFeatureRoleBindings_item_empty_item() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def bindings = [RoleBinding.of(roles[0], types[0]),
                        RoleBinding.of(roles[1], types[1]),
                        RoleBinding.of(roles[2], types[2])]

        def result = fixture.featureRoleBindings(types, [], bindings)
        the(result.size()).shouldEqual(0)
    }

    @Test(expected = IllegalArgumentException.class)
    void testFeatureRoleBindings_item_item_null() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        fixture.featureRoleBindings(types, roles, null)
    }

    @Test
    void testFeatureRoleBindings_item_item_empty() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                     Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                     Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                     Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                     Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                     Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                     Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                     Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                     Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                     Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def result = fixture.featureRoleBindings(types, roles, [])
        the(result.size()).shouldEqual(50)
    }

    @Test
    void testFeatureRoleBindings_item_item_item() {
        SPSConformance fixture = new SPSConformance()

        def types = [Type.builder().type(Type.CLASS).name("a").compKey("a").create(),
                 Type.builder().type(Type.CLASS).name("b").compKey("b").create(),
                 Type.builder().type(Type.CLASS).name("c").compKey("c").create(),
                 Type.builder().type(Type.CLASS).name("d").compKey("d").create(),
                 Type.builder().type(Type.CLASS).name("e").compKey("e").create(),
                 Type.builder().type(Type.CLASS).name("f").compKey("f").create(),
                 Type.builder().type(Type.CLASS).name("g").compKey("g").create(),
                 Type.builder().type(Type.CLASS).name("h").compKey("h").create(),
                 Type.builder().type(Type.CLASS).name("i").compKey("i").create(),
                 Type.builder().type(Type.CLASS).name("j").compKey("j").create()]

        def roles = [ClassRole.builder().name("A").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("B").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("C").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("D").mult(Multiplicity.fromString("1..1")).create(),
                     ClassRole.builder().name("E").mult(Multiplicity.fromString("1..1")).create()]

        def bindings = [RoleBinding.of(roles[0], types[0]),
                        RoleBinding.of(roles[1], types[1]),
                        RoleBinding.of(roles[2], types[2])]

        def result = fixture.featureRoleBindings(types, roles, bindings)
        the(result.size()).shouldEqual(14)
    }

    def buildModelWithRelations() {
        Map<String, Type> types = [:]

        sys = System.builder().name("Test").key("Test").create()
        sys.saveIt()
        Project proj = Project.builder().name("Test").projKey("Test").version("1.1").create()
        sys.addProject(proj)
        Module mod = Module.builder().name("default").moduleKey("default").create()
        proj.addModule(mod)
        Namespace ns = Namespace.builder().name("ns").nsKey("ns").create()
        File f = File.builder().name("test").type(FileType.SOURCE).fileKey("test").create()
        ns.addFile(f)

        types["A"] = Type.builder().type(Type.CLASS).name("A").compKey("A").create()
        types["B"] = Type.builder().type(Type.CLASS).name("B").compKey("B").create()
        types["B"].setAbstract(true)
        types["C"] = Type.builder().type(Type.INTERFACE).name("C").compKey("C").create()
        types["D"] = Type.builder().type(Type.CLASS).name("D").compKey("D").create()
        types["E"] = Type.builder().type(Type.CLASS).name("E").compKey("E").create()
        types["F"] = Type.builder().type(Type.CLASS).name("F").compKey("F").create()
        types["G"] = Type.builder().type(Type.CLASS).name("G").compKey("G").create()
        types["H"] = Type.builder().type(Type.CLASS).name("H").compKey("H").create()
        types["I"] = Type.builder().type(Type.CLASS).name("I").compKey("I").create()
        types["J"] = Type.builder().type(Type.CLASS).name("J").compKey("J").create()
        types.values().each { f.addType(it) }

        types["A"].associatedTo(types["B"])
        types["B"].generalizes(types["D"])
        types["B"].generalizes(types["E"])
        types["B"].generalizes(types["F"])
        types["F"].realizes(types["C"])
        types["F"].composedTo(types["B"])
        types["E"].associatedTo(types["G"])
        types["E"].associatedTo(types["H"])
        types["E"].associatedTo(types["I"])
        types["J"].dependencyTo(types["D"])

        types
    }

    def createPatternInstance() {
        def map = buildModelWithRelations()

        Pattern p = Pattern.findFirst("patternKey = ?", "gof:Visitor")

        Role client = Role.createIt("roleKey", "client", "name", "client")
        Role parent = Role.createIt("roleKey", "parent", "name", "parent")
        Role child = Role.createIt("roleKey", "child", "name", "child")
        Role other = Role.createIt("roleKey", "other", "name", "other")

        p.addRole(client)
        p.addRole(parent)
        p.addRole(child)
        p.addRole(other)

        System sys = System.builder().name("Test").key("test").create()
        Project proj = Project.builder().name("TestProj").projKey("testproj").version("1.0").create()
        Module mod = Module.builder().name("TestMod").moduleKey("testmod").create()
        Namespace ns = Namespace.builder().name("TestNS").nsKey("testns").create()
        File file = File.builder().fileKey("test.java").name("test.java").create()
        Type tClient = Type.builder().type(Type.CLASS)
                .name("Client")
                .compKey("client")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tParent = Type.builder().type(Type.CLASS)
                .name("Parent")
                .compKey("parent")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tChild = Type.builder().type(Type.CLASS)
                .name("Child")
                .compKey("child")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tOther = Type.builder().type(Type.CLASS)
                .name("Other")
                .compKey("other")
                .accessibility(Accessibility.PUBLIC)
                .create()
        file.addType(tParent)
        file.addType(tOther)
        file.addType(tChild)
        file.addType(tClient)
        ns.addFile(file)
        mod.addNamespace(ns)
        proj.addModule(mod)
        sys.saveIt()
        sys.addProject(proj)

        tChild.generalizedBy(tParent)
        tClient.associatedTo(tParent)
        tOther.associatedTo(tChild)

        PatternInstance i = PatternInstance.createIt("instKey", "key")

        edu.isu.isuese.datamodel.RoleBinding rb1 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb1.setRoleRefPair(client, Reference.builder().refKey(tClient.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb2 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb2.setRoleRefPair(parent, Reference.builder().refKey(tParent.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb3 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb3.setRoleRefPair(child, Reference.builder().refKey(tChild.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb4 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb4.setRoleRefPair(other, Reference.builder().refKey(tOther.getRefKey()).refType(RefType.TYPE).create())

        i.addRoleBinding(rb1)
        i.addRoleBinding(rb2)
        i.addRoleBinding(rb3)
        i.addRoleBinding(rb4)

        proj.addPatternInstance(i)

        i
    }

    def createConformingPatternInstance() {
        def map = buildModelWithRelations()

        Pattern p = Pattern.findFirst("patternKey = ?", "gof:Visitor")

        Role client = Role.createIt("roleKey", "client", "name", "client")
        Role parent = Role.createIt("roleKey", "parent", "name", "parent")
        Role child = Role.createIt("roleKey", "child", "name", "child")
        Role element = Role.createIt("roleKey", "element", "name", "element")
        Role elechild = Role.createIt("roleKey", "elechild", "name", "elechild")
        Role struct = Role.createIt("roleKey", "struct", "name", "struct")

        p.addRole(client)
        p.addRole(parent)
        p.addRole(child)
        p.addRole(element)
        p.addRole(elechild)
        p.addRole(struct)

        System sys = System.builder().name("Test").key("test").create()
        Project proj = Project.builder().name("TestProj").projKey("testproj").version("1.0").create()
        Module mod = Module.builder().name("TestMod").moduleKey("testmod").create()
        Namespace ns = Namespace.builder().name("TestNS").nsKey("testns").create()
        File file = File.builder().fileKey("test.java").name("test.java").create()
        Type tClient = Type.builder().type(Type.CLASS)
                .name("Client")
                .compKey("client")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tParent = Type.builder().type(Type.CLASS)
                .name("Visitor")
                .compKey("visitor")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tChild = Type.builder().type(Type.CLASS)
                .name("VisitorChild")
                .compKey("vischild")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Type tElement = Type.builder().type(Type.CLASS)
                .name("Element")
                .compKey("element")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Method teleAccept = Method.builder()
                .name("Accept")
                .compKey("element:Accept")
                .accessibility(Accessibility.PUBLIC)
                .type(TypeRef.createPrimitiveTypeRef("void"))
                .create()
        teleAccept.addModifier(Modifier.forName("ABSTRACT"))
        Parameter param1 = Parameter.builder().name("vis").create()
        param1.type = TypeRef.builder()
                .type(TypeRefType.Type)
                .ref(Reference.builder()
                        .refType(RefType.TYPE)
                        .refKey(tParent.refKey)
                        .create())
                .typeName(tParent.getName())
                .create()
        teleAccept.addParameter(param1)
        tElement.addMember(teleAccept)
        Type tEleChild = Type.builder().type(Type.CLASS)
                .name("ElementChild")
                .compKey("elechild")
                .accessibility(Accessibility.PUBLIC)
                .create()
        Method tecAccept = Method.builder()
                .name("Accept")
                .compKey("elechild:Accept")
                .accessibility(Accessibility.PUBLIC)
                .type(TypeRef.createPrimitiveTypeRef("void"))
                .create()
        Parameter param2 = Parameter.builder().name("vis").create()
        param2.type = TypeRef.builder()
                .type(TypeRefType.Type)
                .ref(Reference.builder()
                        .refType(RefType.TYPE)
                        .refKey(tParent.refKey)
                        .create())
                .typeName(tParent.getName())
                .create()
        tecAccept.addParameter(param2)
        tEleChild.addMember(tecAccept)
        Type tStruct = Type.builder().type(Type.CLASS)
                .name("Structure")
                .compKey("struct")
                .accessibility(Accessibility.PUBLIC)
                .create()
        file.addType(tParent)
        file.addType(tStruct)
        file.addType(tChild)
        file.addType(tClient)
        file.addType(tElement)
        file.addType(tEleChild)
        ns.addFile(file)
        mod.addNamespace(ns)
        proj.addModule(mod)
        sys.saveIt()
        sys.addProject(proj)

        Method tabsVisit = Method.builder()
                .name("Visit")
                .compKey("visitor:Visit")
                .accessibility(Accessibility.PUBLIC)
                .type(TypeRef.createPrimitiveTypeRef("void"))
                .create()
        tabsVisit.addModifier(Modifier.forName("ABSTRACT"))
        Parameter param3 = Parameter.builder().name("vis").create()
        param3.type = TypeRef.builder()
                .type(TypeRefType.Type)
                .ref(Reference.builder()
                        .refType(RefType.TYPE)
                        .refKey(tElement.refKey)
                        .create())
                .typeName(tElement.getName())
                .create()
        tabsVisit.addParameter(param3)
        tParent.addMember(tabsVisit)

        Method tVisit = Method.builder()
                .name("Visit")
                .compKey("vischild:Visit")
                .accessibility(Accessibility.PUBLIC)
                .type(TypeRef.createPrimitiveTypeRef("void"))
                .create()
        Parameter param4 = Parameter.builder().name("vis").create()
        param4.type = TypeRef.builder()
                .type(TypeRefType.Type)
                .ref(Reference.builder()
                        .refType(RefType.TYPE)
                        .refKey(tElement.refKey)
                        .create())
                .typeName(tElement.getName())
                .create()
        tVisit.addParameter(param4)
        tParent.addMember(tVisit)

        tChild.generalizedBy(tParent)
        tEleChild.generalizedBy(tElement)
        tClient.associatedTo(tParent)
        tStruct.associatedTo(tElement)
        tChild.useFrom(tEleChild)

        PatternInstance i = PatternInstance.createIt("instKey", "key")

        edu.isu.isuese.datamodel.RoleBinding rb1 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb1.setRoleRefPair(client, Reference.builder().refKey(tClient.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb2 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb2.setRoleRefPair(parent, Reference.builder().refKey(tParent.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb3 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb3.setRoleRefPair(child, Reference.builder().refKey(tChild.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb4 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb4.setRoleRefPair(element, Reference.builder().refKey(tElement.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb5 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb5.setRoleRefPair(elechild, Reference.builder().refKey(tEleChild.getRefKey()).refType(RefType.TYPE).create())
        edu.isu.isuese.datamodel.RoleBinding rb6 = edu.isu.isuese.datamodel.RoleBinding.createIt()
        rb6.setRoleRefPair(element, Reference.builder().refKey(tStruct.getRefKey()).refType(RefType.TYPE).create())

        i.addRoleBinding(rb1)
        i.addRoleBinding(rb2)
        i.addRoleBinding(rb3)
        i.addRoleBinding(rb4)
        i.addRoleBinding(rb5)
        i.addRoleBinding(rb6)

        proj.addPatternInstance(i)

        i
    }

    @Test
    void createPatternInstance_Empty() {
        Pattern p = Pattern.findFirst("patternKey = ?", "gof:visitor")
        PatternInstance inst = PatternInstance.createIt()

        SPSConformance fixture = new SPSConformance()
        def modelBlocks = fixture.getModelBlocks(inst)

        a(modelBlocks.size()).shouldEqual(0)
    }

    @Test(expected = IllegalArgumentException)
    void createPatternInstance_Null() {
        SPSConformance fixture = new SPSConformance()
        fixture.getModelBlocks(null)
    }

    @Test
    void testEnumsWorkAsExpected() {
        Type e1 = Type.builder().type(Type.ENUM).name("e1").compKey("e1").accessibility(Accessibility.PUBLIC).create()
        Type e2 = Type.builder().type(Type.ENUM).name("e2").compKey("e2").accessibility(Accessibility.PUBLIC).create()
        Type e3 = Type.builder().type(Type.ENUM).name("e3").compKey("e3").accessibility(Accessibility.PUBLIC).create()

        e1.realizes(e2)
        e1.realizes(e3)
        e3.realizes(e2)
        e1.generalizes(e2)

        the(e1.getRealizes().size()).shouldEqual(2)
        the(e3.getRealizes().size()).shouldEqual(1)
        the(e2.getRealizedBy().size()).shouldEqual(2)
        the(e2.getGeneralizedBy().size()).shouldEqual(1)
    }

    @Test
    void testClassesWorkAsExpected() {
        Type e1 = Type.builder().type(Type.CLASS).name("e1").compKey("e1").accessibility(Accessibility.PUBLIC).create()
        Type e2 = Type.builder().type(Type.CLASS).name("e2").compKey("e2").accessibility(Accessibility.PUBLIC).create()
        Type e3 = Type.builder().type(Type.CLASS).name("e3").compKey("e3").accessibility(Accessibility.PUBLIC).create()

        e1.realizes(e2)
        e1.realizes(e3)
        e3.realizes(e2)
        e1.generalizes(e2)

        the(e1.getRealizes().size()).shouldEqual(2)
        the(e3.getRealizes().size()).shouldEqual(1)
        the(e2.getRealizedBy().size()).shouldEqual(2)
        the(e2.getGeneralizedBy().size()).shouldEqual(1)
    }

    @Test
    void testInterfacesWorkAsExpected() {
        Type e1 = Type.builder().type(Type.INTERFACE).name("e1").compKey("e1").accessibility(Accessibility.PUBLIC).create()
        Type e2 = Type.builder().type(Type.INTERFACE).name("e2").compKey("e2").accessibility(Accessibility.PUBLIC).create()
        Type e3 = Type.builder().type(Type.INTERFACE).name("e3").compKey("e3").accessibility(Accessibility.PUBLIC).create()

        e1.realizes(e2)
        e1.realizes(e3)
        e3.realizes(e2)
        e1.generalizes(e2)

        the(e1.getRealizes().size()).shouldEqual(2)
        the(e3.getRealizes().size()).shouldEqual(1)
        the(e2.getRealizedBy().size()).shouldEqual(2)
        the(e2.getGeneralizedBy().size()).shouldEqual(1)
    }

    @Test
    void testAllThreeTypesWorkTogether() {
        Type e1 = Type.builder().type(Type.INTERFACE).name("e1").compKey("e1").accessibility(Accessibility.PUBLIC).create()
        Type e2 = Type.builder().type(Type.CLASS).name("e2").compKey("e2").accessibility(Accessibility.PUBLIC).create()
        Type e3 = Type.builder().type(Type.ENUM).name("e3").compKey("e3").accessibility(Accessibility.PUBLIC).create()

        e1.realizes(e2)
        e1.realizes(e3)
        e3.realizes(e2)
        e1.generalizes(e2)

        the(e1.getRealizes().size()).shouldEqual(2)
        the(e3.getRealizes().size()).shouldEqual(1)
        the(e2.getRealizedBy().size()).shouldEqual(2)
        the(e2.getGeneralizedBy().size()).shouldEqual(1)
    }

    def createMapping(SPSConformance fixture, SPS sps, PatternInstance inst) {
        Map<RoleBlock, List<BlockBinding>> mapping = [:]

        if (!fixture || !sps || !inst)
            return mapping

        List<ModelBlock> mbs = fixture.getModelBlocks(inst)

        sps.roleBlocks().each { rb ->
            mbs.each { mb ->
                if (mapping[rb])
                    mapping[rb] << BlockBinding.of(rb, mb)
                else
                    mapping[rb] = [BlockBinding.of(rb, mb)]
            }
        }

        mapping
    }

    def createSPS() {
        String text = getClass().getResource('/rbmldef/visitor.yml').readLines().join('\n')
        Yaml yaml = new Yaml()
        SpecificationReader reader = new SpecificationReader()
        def map = yaml.load(text)
        reader.processSPS(map)
        reader.sps
    }

    def createRoleBinding() {
        Type c = Type.builder().type(Type.CLASS)
                .name("class")
                .compKey("class")
                .accessibility(Accessibility.PUBLIC)
                .create()

        Field f = Field.builder()
                .name("field")
                .compKey("field")
                .accessibility(Accessibility.PUBLIC)
                .create()
        f.type = TypeRef.createPrimitiveTypeRef("int")
        c.addMember(f)

        Method m = Method.builder()
                .name("method")
                .compKey("method")
                .accessibility(Accessibility.PUBLIC)
                .create()

        m.type = TypeRef.createPrimitiveTypeRef("void")

        Parameter p = Parameter.builder().name("param").create()
        p.type = TypeRef.createPrimitiveTypeRef("int")

        m.addParameter(p)
        c.addMember(m)

        edu.montana.gsoc.msusel.rbml.model.Classifier type = edu.montana.gsoc.msusel.rbml.model.UnknownType.builder().name("Type").create()

        ClassRole role = ClassRole.builder()
                .name("role")
                .mult(Multiplicity.fromString("1..1"))
                .create()

        StructuralFeature sf = StructuralFeature.builder()
                .name("s1")
                .mult(Multiplicity.fromString("1..1"))
                .create()

        role.structFeats << sf

        BehavioralFeature bf = BehavioralFeature.builder()
                .name("b1")
                .mult(Multiplicity.fromString("1..1"))
                .create()

        bf.params << edu.montana.gsoc.msusel.rbml.model.Parameter.builder().var("param").type(type).create()

        role.behFeats << bf

        RoleBinding.of(role, c)
    }
}