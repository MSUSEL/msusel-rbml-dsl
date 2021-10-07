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

import com.google.common.collect.Sets
import edu.isu.isuese.datamodel.Field
import edu.isu.isuese.datamodel.Method
import edu.isu.isuese.datamodel.PatternInstance
import edu.isu.isuese.datamodel.Type
import edu.montana.gsoc.msusel.rbml.model.*
import org.apache.commons.lang3.tuple.Pair

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class SPSConformance {

    Map<Role, List<Type>> roleTypeMap = [:]

    /**
     *
     * @param sps
     * @param instance
     * @return
     */
    def conforms(SPS sps, PatternInstance instance) {

        if (sps == null || instance == null)
            throw new IllegalArgumentException()

        Map<RoleBlock, List<BlockBinding>> mapping = [:]
        List<ModelBlock> modelBlocks = getModelBlocks(instance)

        double totalLocal = 0
        int total = 0

        sps.roleBlocks().each { RoleBlock rb ->
            mapping[rb] = mapping[rb] ?: []

            modelBlocks.each { ModelBlock mb ->
                BlockBinding binding = checkBlockConformance(rb, mb)
                if (binding) {
                    totalLocal += checkLocalConformance(binding)
                    total += 1
                    mapping[rb] << binding
                }
            }
        }

        // Instance Conformance Index
        double ici
        if (total <= 0)
            ici = 0.0
        else
            ici = totalLocal / total

        List<Pair<RoleBlock, BlockBinding>> toRemove = []
        mapping.each { RoleBlock rb, List<BlockBinding> list ->
            list.each { mb ->
                if (!sharingConstraint(rb, mb, constructSharingContraints(mapping)))
                    toRemove << Pair.of(rb, mb)
            }
        }

        Pair<List<Role>, List<Role>> sat = Pair.of([], [])
        def val = realizationMult(mapping, sat)

        double psi = 0.0

        if (sat && (sat.left || sat.right)) {
            // Pattern Satisfaction Index
            psi = (double) sat.right.size() / (sat.left.size() + sat.right.size())
        }

        List<ModelBlock> nonConformingModelBlocks = getNonConformingModelBlocks(mapping, instance)
        List<ModelBlock> conformingModelBlocks = getConformingModelBlocks(mapping)

        // TODO Return Tuple of (ICI, PSI, NonComList, ComList)
        return new Tuple(ici, psi, nonConformingModelBlocks, conformingModelBlocks)
    }

    Map<RoleBlock, List<SharingConstraint>> constructSharingContraints(mapping) {
        [:]
    }

    List<ModelBlock> getNonConformingModelBlocks(Map<RoleBlock, List<BlockBinding>> mapping, PatternInstance inst) {
        if (mapping == null || !inst)
            throw new IllegalArgumentException()

        List<ModelBlock> blocks = getModelBlocks(inst)

        mapping.each { RoleBlock rb, List<BlockBinding> bb ->
            bb.each {
                blocks.remove(it.mb)
            }
        }

        blocks
    }

    List<ModelBlock> getConformingModelBlocks(Map<RoleBlock, List<BlockBinding>> mapping) {
        if (mapping == null)
            throw new IllegalArgumentException()

        def results = []
        mapping.each { RoleBlock rb, List<BlockBinding> bb ->
            bb.each {
                results << it.mb
            }
        }
        results
    }

    List<RoleBlock> getUnboundRoles(Map<RoleBlock, List<BlockBinding>> mapping, SPS sps) {
        if (mapping == null || sps == null)
            throw new IllegalArgumentException()

        List<RoleBlock> list = sps.roleBlocks()

        mapping.each { RoleBlock rb, List<BlockBinding> binding ->
            if (list.contains(rb))
                list.remove(rb)
        }

        list
    }

    List<Type> getUnboundTypes(Map<RoleBlock, List<BlockBinding>> mapping, PatternInstance inst) {
        if (mapping == null || inst == null)
            throw new IllegalArgumentException()

        List<Type> unbound = [] as List<Type>
        inst.getTypes().each { Type t ->
            for (List<BlockBinding> binding : mapping.values()) {
                for (BlockBinding bind : binding) {
                    if (bind.getMb().getDest() == t)
                        break
                    if (bind.getMb().getSource() == t)
                        break
                }
            }
            unbound << t
        }

        unbound
    }

    List<ModelBlock> getModelBlocks(PatternInstance inst) {
        if (inst == null)
            throw new IllegalArgumentException()

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

//        types.each { Type dest ->
//            getGenRealSrcTypes(dest).each { Type src ->
//                blocks << ModelBlock.of(src, dest).type(BlockType.GENERALIZATION)
//            }
//            getAssocSrcTypes(dest).each { Type src ->
//                blocks << ModelBlock.of(src, dest).type(BlockType.ASSOCIATION)
//            }
//            getDependSrcTypes(dest).each { Type src ->
//                blocks << ModelBlock.of(src, dest).type(BlockType.DEPENDENCY)
//            }
//        }

        blocks as List
    }

    /**
     * Extracts the types the given type specializes
     * @param src Type pointed away from via realization or generalization
     * @return Set of types
     */
    private Set<Type> getGenRealDestTypes(Type src) {
        Set<Type> types = [] as Set

        types += src.getRealizedBy()
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

        types += src.getAssociatedTo()
        types += src.getAggregatedTo()
        types += src.getComposedTo()

        types
    }

    /**
     * Extracts the types that the given type is connected from via dependency
     * @param src Source side of the dependency
     * @return Set of types
     */
    private Set<Type> getDependDestTypes(Type src) {
        Set<Type> types = [] as Set

        types += src.getUseTo()
        types += src.getDependencyTo()

        types
    }

    /**
     * Extracts the types that specialize the given type
     * @param dest Type pointed towards via realization or generalization
     * @return Set of types
     */
    Set<Type> getGenRealSrcTypes(Type dest) {
        if (!dest)
            throw new IllegalArgumentException()

        Set<Type> types = [] as Set

        types += dest.getRealizedBy()
        types += dest.getGeneralizedBy()

        types
    }

    /**
     * Extracts the types the given type is associated from
     * @param dest The destination side of the association
     * @return Set of types
     */
    Set<Type> getAssocSrcTypes(Type dest) {
        if (!dest)
            throw new IllegalArgumentException()

        Set<Type> types = [] as Set

        types += dest.getAssociatedFrom()
        types += dest.getAggregatedFrom()
        types += dest.getComposedFrom()

        types
    }

    /**
     * Extracts the types the given type is dependent from
     * @param dest The destination side of the dependency
     * @return Set of types
     */
    private Set<Type> getDependSrcTypes(Type dest) {
        Set<Type> types = [] as Set

        types += dest.getUseFrom()
        types += dest.getDependencyFrom()

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
        if (!rb || !mb)
            throw new IllegalArgumentException()

        BlockBinding binding = null
        if (rb.type == mb.type) {
            if (rb.matchesSource(mb.source) && rb.matchesDest(mb.dest)) {
                binding = BlockBinding.of(rb, mb)
            } else if (rb.matchesSource(mb.dest) && rb.matchesDest(mb.source)) {
                binding = BlockBinding.fo(rb, mb)
            }
        }

        return binding
    }

    def checkLocalConformance(BlockBinding binding) {
        if (!binding)
            throw new IllegalArgumentException()

        double total = 0
        double unmapped = 0
        double counts = 0
        double localConformance = 1

        if (binding) {
            // role multiplicities are met?
            Map<Role, Integer> countMap = [:]
            binding.roleBindings.each { b ->
                if (!countMap[b.role])
                    countMap[b.role] = 0
                if (b.type)
                    countMap[b.role] += 1
            }

            countMap.each {role, count ->
                if (role.mult.inRange(count))
                    counts += 1
                total += 1
            }

            binding.roleBindings.each { b ->
                def featBind = createFeatureBindings(b)
                def count = 0
                if (b.role instanceof  Classifier)
                {
                    Classifier c = (Classifier) b.role
                    count += c.structFeats.size()
                    count += c.behFeats.size()
                }
                Set<Feature> feats = [].toSet()
                featBind.each { fb ->
                    feats << ((FeatureBinding) fb).feat
                }
                unmapped += count - feats.size()
                total += count
                counts += count - unmapped
            }

            localConformance = counts / total
        }

        localConformance
    }

    def createFeatureBindings(RoleBinding rb) {
        if (!rb)
            throw new IllegalArgumentException()

        def bindings = []
        Role role = rb.role
        if (role instanceof Classifier) {
            bindings += createBehavioralFeatureBindings(rb)

            bindings += createStructuralFeatureBindings(rb)
        }

        bindings
    }

    def createBehavioralFeatureBindings(RoleBinding rb) {
        if (!rb)
            throw new IllegalArgumentException()

        def bindings = []

        List<Method> methods = new ArrayList<>(rb.type.methods)

        // 1. Identify those methods with matching return types
        Map<BehavioralFeature, List<Method>> matching = [:]
        Classifier role = (Classifier) rb.role
        role.behFeats.each { BehavioralFeature feat ->
            if (feat.type) {
                if (feat.type instanceof UnknownType) {
                    matching[feat] = rb.type.methods.findAll { Method m ->
                        !roleTypeMap.values().contains(m.type)
                    }
                } else {
                    matching[feat] = rb.type.methods.findAll { Method m ->
                        roleTypeMap[feat.type]?.contains(m.type)
                    }
                }
            } else {
                matching[feat] = rb.type.methods
            }
        }

        // 2. Identify those methods with matching params
        matching.each { feat, list ->
            def matches = list.findAll {
                paramsAlign(feat, it)
            }

            feat.mult.lower.times {
                while (matches && !methods.contains(matches[0]))
                    matches.pop()

                if (!matches.isEmpty()) {
                    methods.remove(matches.get(0))
                    bindings << FeatureBinding.of(feat, matches.pop())
                }
            }
        }

        bindings
    }

    def paramsAlign(BehavioralFeature feat, Method m) {
        boolean aligned = true

        if (m.params.size() < feat.params.size()) return false
        else {
            for (int i = 0; i < feat.params.size(); i++) {
                Parameter p = feat.params[i]
                if (p.type) {
                    if (p.type instanceof UnknownType) {
                        Type t = roleTypeMap[p.type]?.find {
                            it.name == m.params[i].type.typeName
                        }
                        aligned && !t
                    }
                    else {
                        Type t = roleTypeMap[p.type]?.find {
                            it.name == m.params[i].type.typeName
                        }
                        aligned && t
                    }
                }
            }
        }

        aligned
    }

    def createStructuralFeatureBindings(RoleBinding rb) {
        if (!rb)
            throw new IllegalArgumentException()

        def bindings = []

        List<Field> fields = new ArrayList<>(rb.type.fields)
        Classifier role = (Classifier) rb.role
        List<StructuralFeature> structs = new ArrayList<>(role.structFeats)
        structs.sort { a, b ->
            if (!a.type && !b.type) return 0
            if (a.type && !b.type) return 1
            if (!a.type && !b.type) return -1
            if (a.type instanceof UnknownType && b.type instanceof UnknownType)
                return 0
            if (a.type instanceof UnknownType && !(b.type instanceof UnknownType))
                return -1
            if (!(a.type instanceof UnknownType) && b.type instanceof UnknownType)
                return 0
            return 0
        }

        structs.each { StructuralFeature feat ->
            if (feat.type) {
                if (feat.type instanceof UnknownType) {
                    feat.mult.getLower().times {
                        Field f = rb.type.fields.find { Field f ->
                            roleTypeMap[feat.type]?.contains(f.type)
                        }
                        if (f) {
                            bindings << FeatureBinding.of(feat, f)
                        }
                    }
                }
                else {
                    feat.mult.getLower().times {
                        Field f = rb.type.fields.find { Field f ->
                            roleTypeMap[feat.type]?.contains(f.type)
                        }
                        if (f) {
                            bindings << FeatureBinding.of(feat, f)
                        }
                    }
                }
            } else {
                feat.mult.getLower().times {
                    Field f = fields.pop()
                    bindings << FeatureBinding.of(feat, f)
                }
            }
        }

        bindings
    }

    def featureRoleBindings(List<Type> types, List<Role> roles, List<RoleBinding> bindings) {
        if (types == null || roles == null || bindings == null)
            throw new IllegalArgumentException()

        def rprime = Sets.newHashSet(roles)
        rprime.removeAll(bindings*.role)
        def tprime = Sets.newHashSet(types)
        tprime.removeAll(bindings*.type)
        def kprime = []
        rprime.each { r ->
            tprime.each { t ->
                kprime << RoleBinding.of(r, t)
            }
        }
        kprime
    }

    /**
     * first find all sharing constraints involving rb
     * then check that all of these sharing constraints are met by mb
     *
     * @param rb RoleBlock
     * @param mb ModelBlock
     */
    boolean sharingConstraint(RoleBlock rb, BlockBinding binding, Map<RoleBlock, List<SharingConstraint>> constraints) {
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

        List<Pair<RoleBlock, RoleBlock>> candidates = pairs(blocks)

        candidates.each { Pair<RoleBlock, RoleBlock> pair ->
            Role shared = pair.left.sharesWith(pair.right)
            if (shared) {
                SharingConstraint s = SharingConstraint.of(pair.left, pair.right)
                s.on(shared)
                constraints[pair.left] = constraints[pair.left] ? constraints[pair.left] + s : [s]
                constraints[pair.right] = constraints[pair.right] ? constraints[pair.right] + s : [s]
            }
        }

        constraints
    }

    /**
     * Extracts a List of all pairs of RoleBlocks from the provided list for use in constructing sharing constraints
     * @param blocks Initial list of unpaired role blocks
     * @return List of potential pairs of role blocks for use in constructing sharing constraints
     */
    private List<Pair<RoleBlock, RoleBlock>> pairs(List<RoleBlock> blocks) {
        if (blocks == null)
            throw new IllegalArgumentException()

        List<Pair<RoleBlock, RoleBlock>> pairs = []
        for (int i = 0; i < blocks.size() - 1; i++) {
            for (int j = i + 1; j < blocks.size(); j++) {
                pairs << Pair.of(blocks[i], blocks[j])
            }
        }
        pairs
    }

    /**
     * for each rb in mapping.keys check that the multiplicity of the role is met by the  matching instances in mb
     * if a role is found unsatisfied, the mapping instances for the role blocks that contain the role are removed
     * from the mapping
     *
     * @param bindings
     * @return a value between 0.0 and 1.0 representing the percent of
     */
    def realizationMult(Map<RoleBlock, List<BlockBinding>> bindings, Pair<List<Role>, List<Role>> pair) {
        if (bindings == null || !pair)
            throw new IllegalArgumentException()

        // check that the number of classifiers bound to a classifier role
        // satisfy the realization multiplicities associated with the role,
        // and check that mandatory roles have classifiers bound to them.
        Map<Role, Set<Type>> mappings = [:]
        Set<Role> roles = [].toSet()
        bindings.each { role, lst ->
            if (lst.isEmpty()) {
                roles << role.sources.first()
                roles << role.dests.first()
            }
            lst.each { bb ->
                bb.roleBindings.each {
                    if (it.role) roles << it.role
                    if (!mappings[it.role])
                        mappings[it.role] = new HashSet<Type>()
                    mappings[it.role] << it.type
                }
            }
        }

        Map<Role, Integer> counts = [:]
        roles.each { counts[it] = 0}
        mappings.each { role, set ->
            counts[role] = set.size()
        }

        // check that realization multiplicities are satisfied
        // check that mandatory roles have been bound
        counts.each { role, count ->
            if (!role.mult.inRange(count)) {
                pair.getLeft() << role
            } else {
                pair.getRight() << role
            }
        }

        double value = 0.0
        if (mappings.keySet().size() > 0) {
            value = (double) (roles.size() - pair.getLeft().size()) / roles.size()
        }

        value
    }
}
