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

import com.google.inject.Inject
import edu.isu.isuese.datamodel.PatternInstance
import edu.isu.isuese.datamodel.Type
import edu.montana.gsoc.msusel.rbml.model.*
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class SPSConformance {

    // Roles mapped to CodeNodes
    def roleNodeMap = [:]

    /**
     *
     * @param sps
     * @param instance
     * @return
     */
    def conforms(SPS sps, PatternInstance instance) {

        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        List<ModelBlock> modelBlocks = getModelBlocks(instance)

        sps.roleBlocks().each { RoleBlock rb ->
            mapping[rb] = []
            modelBlocks.each { ModelBlock mb ->
                BlockBinding binding = checkBlockConformance(rb, mb)
                if (binding)
                    mapping[rb] << binding
            }
        }

        List<Pair<RoleBlock, BlockBinding>> toRemove = []
        mapping.each { RoleBlock rb, List<BlockBinding> list ->
            list.each { mb ->
                if (!sharingConstraint(rb, mb))
                    toRemove << Pair.of(rb, mb)
            }
        }

        if (realizationMult(mapping))
            noncom = dom(mapping)

        // TODO Info needed to capture:
        // TODO 1. Non-conforming Model Blocks
        // TODO 2. Conforming Model Blocks
        List<ModelBlock> conformingModelBlocks = getConformingModelBlocks(mapping)
        // TODO 3. Roles without bindings
        List<RoleBlock> unboundRoles = getUnboundRoles(mapping, SPS)
        // TODO 4. Unbound Types
        List<Type> unbound = getUnboundTypes(mapping, instance)
        // TODO Use this info to calculate instance conformance index (ICI), pattern satisfaction index (PSI), and to build list of non-conforming and conforming model blocks
        // TODO Return Tuple of (ICI, PSI, NonComList, ComList)
        return Triple.of([], [], 0.0)
    }

    List<ModelBlock> getConformingModelBlocks(Map<RoleBlock, List<BlockBinding>> mapping) {

    }

    List<RoleBlock> getUnboundRoles(Map<RoleBlock, List<BlockBinding>> mapping, SPS sps) {
        List<RoleBlock> list = []

        SPS.roleBlocks().each { RoleBlock rb ->
            if (mapping[rb] == null || mapping[rb].isEmpty())
                list += rb
        }

        list
    }

    List<Type> getUnboundTypes(Map<RoleBlock, List<BlockBinding>> mapping, PatternInstance inst) {

    }

    List<ModelBlock> getModelBlocks(PatternInstance inst) {
        List<Type> types = inst.getTypes()
        Set<ModelBlock> blocks = [] as Set<ModelBlock>

        types.each { Type src ->
            getGenRealDestTypes(src).each { Type dest ->
                blocks << ModelBlock.of(src, dest).type(BlockType.GENERALIZATION)
            }
            getAssocDestTypes(src).each { Type dest ->
                blocks << ModelBlock.of(src, dest).type(BlockType.ASSOCIATION)
            }
            getDependDestTypes(src).each { Type dest ->
                blocks << ModelBlock.of(src, dest).type(BlockType.DEPENDENCY)
            }
        }

        types.each { Type dest ->
            getGenRealSrcTypes(dest).each { Type src ->
                blocks << ModelBlock.of(src, dest).type(BlockType.GENERALIZATION)
            }
            getAssocSrcTypes(dest).each { Type src ->
                blocks << ModelBlock.of(src, dest).type(BlockType.ASSOCIATION)
            }
            getDependSrcTypes(dest).each { Type src ->
                blocks << ModelBlock.of(src, dest).type(BlockType.DEPENDENCY)
            }
        }

        blocks as List
    }

    /**
     * Extracts the types the given type specializes
     * @param src Type pointed away from via realization or generalization
     * @return Set of types
     */
    private Set<Type> getGenRealDestTypes(Type src) {
        Set<Type> types = [] as Set

        types += src.getRealizes()
        types += src.getGeneralizes()

        types
    }

    /**
     * Extracts the types the given type is associated from
     * @param src The source side of the association
     * @return Set of types
     */
    private Set<Type> getAssocDestTypes(Type src) {
        Set<Type> types = [] as Set

        types += src.getAssociatedFrom()
        types += src.getAggregatedFrom()
        types += src.getComposedFrom()

        types
    }

    /**
     * Extracts the types that the given type is connected from via dependency
     * @param src Source side of the dependency
     * @return Set of types
     */
    private Set<Type> getDependDestTypes(Type src) {
        Set<Type> types = [] as Set

        types += src.getUseFrom()
        types += src.getDependencyFrom()

        types
    }

    /**
     * Extracts the types that specialize the given type
     * @param dest Type pointed towards via realization or generalization
     * @return Set of types
     */
    private Set<Type> getGenRealSrcTypes(Type dest) {
        Set<Type> types = [] as Set

        types += dest.getRealizedBy()
        types += dest.getGeneralizedBy()

        types
    }

    /**
     * Extracts the types the given type is associated to
     * @param dest The destination side of the association
     * @return Set of types
     */
    private Set<Type> getAssocSrcTypes(Type dest) {
        Set<Type> types = [] as Set

        types += dest.getAssociatedTo()
        types += dest.getAggregatedTo()
        types += dest.getComposedTo()

        types
    }

    /**
     * Extracts the types the given type is dependent to
     * @param dest The destination side of the dependency
     * @return Set of types
     */
    private Set<Type> getDependSrcTypes(Type dest) {
        Set<Type> types = [] as Set

        types += dest.getUseTo()
        types += dest.getDependencyTo()

        types
    }

    /**
     * Checks the local conformance of the model block to the role block.
     * Local conformance is met if:
     *  1. Role Block type matches Model Block type
     *  2. Each classifier conforms to only one classifier role or each classifier conforms to both the classifier roles
     *
     * @param rb The role block
     * @param mb The model block
     * @return true if the model block conforms to the provided role block
     */
    def checkBlockConformance(RoleBlock rb, ModelBlock mb) {
        BlockBinding binding
        if (rb.type == mb.type) {
            if (checkEndRole(rb.source, mb.source) && checkEndRole(rb.dest, mb.dest)) {
                binding = BlockBinding.of(rb, mb)
            }
            else if (checkEndRole(rb.source, mb.dest) && checkEndRole(rb.dest, mb.source)) {
                binding = BlockBinding.fo(rb, mb)
            }
        }
        // TODO Check that each feature in the role block can be mapped to a feature in the model block
        binding
    }

    /**
     * verify that the provided code type conforms to the provided role
     * @param r Role
     * @param c CodeNode
     * @return true if the role and type have the same types
     */
    private boolean checkEndRole(Role r, Type c) {
        boolean ret = false
        switch (r) {
            case Classifier:
                ret = c instanceof Classifier
                break
            case ClassRole:
                ret = c instanceof Class && !c.isInterface()
                break
            case InterfaceRole:
                ret = c instanceof Class && c.isInterface()
                break
        }

        ret
    }

    /**
     * first find all sharing constraints involving rb
     * then check that all of these sharing constraints are met by mb
     *
     * @param rb RoleBlock
     * @param mb ModelBlock
     */
    boolean sharingConstraint(RoleBlock rb, BlockBinding binding, Map<RoleBlock, List<SharingConstraint>> constraints, Map<RoleBlock, List<BlockBinding>> mappings) {
        boolean ret = true

        constraints[rb].each { SharingConstraint sc ->
            Type c = binding.nodeBoundTo(sc.shared)
            RoleBlock other
            if (sc.first == rb)
                other = sc.second
            else
                other = sc.first

            List<BlockBinding> candidates = mappings[other].findAll {
                it.nodeBoundTo(sc.shared) == c
            }
            ret &= !candidates.empty
        }

        ret
    }

    /**
     * Constructs the sharing constraints associated with the provided list of RoleBlocks
     * @param blocks Blocks from which to form sharing constraints
     * @return Map of Sharing Constraints constructed from the given list of RoleBlocks keyed by a role block used in the constraint
     */
    Map<RoleBlock, List<SharingConstraint>> findSharingConstraints(List<RoleBlock> blocks) {
        Map<RoleBlock, List<SharingConstraint>> constraints = [:]

        List<List<RoleBlock>> candidates = pairs(blocks).findAll() { List<RoleBlock> pair ->
            pair[0].source == pair[1].source || pair[0].dest == pair[1].dest || pair[0].source == pair[1].dest || pair[0].dest == pair[1].source
        }

        candidates.collect { List<RoleBlock> pair ->
            SharingConstraint s = SharingConstraint.of(pair[0], pair[1])
            if (pair[0].source == pair[1].source || pair[0].source == pair[1].dest) {
                s.on(pair[0].source)
            }
            else {
                s.on(pair[0].dest)
            }
            constraints[pair[0]] = constraints[pair[0]] ? constraints[pair[0]] + s : [s]
            constraints[pair[1]] = constraints[pair[1]] ? constraints[pair[1]] + s : [s]
        }

        constraints
    }

    /**
     * Extracts a List of all pairs of RoleBlocks from the provided list for use in constructing sharing constraints
     * @param blocks Initial list of unpaired role blocks
     * @return List of potential pairs of role blocks for use in constructing sharing constraints
     */
    private List<List<RoleBlock>> pairs(List<RoleBlock> blocks) {
        return blocks.tail().collect { [blocks.head(), it] } + (blocks.size() > 1 ? pairs(blocks.tail()) : [])
    }

    /**
     * for each rb in mapping.keys check that the multiplicity of the role is met by the  matching instances in mb
     * if a role is found unsatisfied, the mapping instances for the role blocks that contain the role are removed
     * from the mapping
     *
     * @param bindings
     */
    def realizationMult(List<BlockBinding> bindings) {
        // TODO check that the number of classifiers bound to a classifier role
        //      satisfy the realization multiplicities associated with the role,
        // TODO and check that mandatory roles have classifiers bound to them.
        Map<Role, Set<Type>> mappings = [:]
        bindings.each {
            it.roleBindings.each { RoleBinding rb ->
                if (!mappings.containsKey(rb.role))
                    mappings[rb.role] = [] as Set<Type>
                mappings[rb.role] << rb.type
            }
        }

        Map<Role, Integer> counts = [:]
        mappings.each { role, set ->
            counts[role] = set.size()
        }


        // TODO check that realization multiplicities are satisfied
        // TODO check that mandatory roles have been bound
        counts.each { role, count ->
            if ((role.mult.lower > 0 && count > 0) && role.mult.inRange(count)) {

            }
        }
    }
}
