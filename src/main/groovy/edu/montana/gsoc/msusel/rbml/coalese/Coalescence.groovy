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
package edu.montana.gsoc.msusel.rbml.coalese

import com.google.common.collect.Lists
import com.google.common.collect.Queues
import edu.isu.isuese.datamodel.*
import edu.montana.gsoc.msusel.rbml.PatternLoader
import edu.montana.gsoc.msusel.rbml.model.ClassRole
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.InterfaceRole
import edu.montana.gsoc.msusel.rbml.model.SPS

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class Coalescence {

    /**
     *
     * @param patterns
     * @return
     */
    def coalesce(List<PatternInstance> patterns) {
        Map<Pattern, List<PatternInstance>> map = partition(patterns)

        map.each { Pattern k, List<PatternInstance> instanceList ->
            instanceList.each {
                SPS sps = loadPattern(k.getName())
                println(sps)
                expand(it, sps)
            }
        }

        List<PatternInstance> retVal = []

        map.each { Pattern k, List<PatternInstance> instanceList ->
            retVal += compareAndCombine(instanceList)
        }

        retVal
    }

    /**
     * Maps pattern instances to their Pattern definition
     *
     * @param patterns List of found pattern instances
     * @return Map containing the pattern instances partitioned by pattern definition
     */
    def partition(List<PatternInstance> patterns) {
        Map<Pattern, List<PatternInstance>> map = [:]

        patterns.each {
            if (!map[it.getParentPattern()])
                map[it.getParentPattern()] = [it]
            else
                map[it.getParentPattern()] << it
        }

        map
    }

    /**
     * Expands an identified pattern instance to include all components allowed by the RBML definition
     *
     * @param instance Initial pattern instance to be expanded.
     */
    def expand(PatternInstance instance, SPS sps) {
        List<Type> visited = Lists.newArrayList(instance.getTypes())
        def queue = Queues.newArrayDeque(visited)

        while(!queue.isEmpty()) {
            Type it = queue.poll()
            Role r = instance.getRoleBoundTo(it)
            edu.montana.gsoc.msusel.rbml.model.Role spsRole = sps.findTypeRoleByName(r.getName())
            Map<RelationType, List<edu.montana.gsoc.msusel.rbml.model.Role>> srcRels = sps.getSrcRelations(spsRole)
            Map<RelationType, List<edu.montana.gsoc.msusel.rbml.model.Role>> destRels = sps.getDestRelations(spsRole)

            // check all outgoing relationships for non-pattern types and add to queue
            if (srcRels.keySet().contains(RelationType.ASSOCIATION)) {
                updateVisited(instance, it.getAssociatedTo(), visited, srcRels, queue)
            }
            if (srcRels.keySet().contains(RelationType.GENERALIZATION)) {
                updateVisited(instance, it.getGeneralizedBy(), visited, srcRels, queue)
            }
            if (srcRels.keySet().contains(RelationType.REALIZATION)) {
                updateVisited(instance, it.getRealizes(), visited, srcRels, queue)
            }
            if (srcRels.keySet().contains(RelationType.AGGREGATION)) {
                updateVisited(instance, it.getAggregatedTo(), visited, srcRels, queue)
            }
            if (srcRels.keySet().contains(RelationType.COMPOSITION)) {
                updateVisited(instance, it.getComposedTo(), visited, srcRels, queue)
            }
            if (srcRels.keySet().contains(RelationType.USE)) {
                updateVisited(instance, it.getUseTo(), visited, srcRels, queue)
            }

            // check all incoming relationships for non-pattern types and add to queue
            if (destRels.keySet().contains(RelationType.ASSOCIATION)) {
                updateVisited(instance, it.getAssociatedFrom(), visited, destRels, queue)
            }
            if (destRels.keySet().contains(RelationType.GENERALIZATION)) {
                updateVisited(instance, it.getGeneralizes(), visited, destRels, queue)
            }
            if (destRels.keySet().contains(RelationType.REALIZATION)) {
                updateVisited(instance, it.getRealizedBy(), visited, destRels, queue)
            }
            if (destRels.keySet().contains(RelationType.AGGREGATION)) {
                updateVisited(instance, it.getAggregatedFrom(), visited, destRels, queue)
            }
            if (destRels.keySet().contains(RelationType.COMPOSITION)) {
                updateVisited(instance, it.getComposedFrom(), visited, destRels, queue)
            }
            if (destRels.keySet().contains(RelationType.USE)) {
                updateVisited(instance, it.getUseFrom(), visited, destRels, queue)
            }
        }
    }

    private void updateVisited(PatternInstance inst, Set<Type> related, ArrayList<Type> visited, Map<RelationType, List<edu.montana.gsoc.msusel.rbml.model.Role>> map, Queue<Type> queue) {
        for (Type t : related) {
            if (visited.contains(t))
                continue
            if (matchAndBind(inst, map[RelationType.ASSOCIATION], t)) {
                visited << t
                queue.offer(t)
                break
            }
        }
    }

    boolean matchAndBind(PatternInstance inst, List<edu.montana.gsoc.msusel.rbml.model.Role> roles, Type type) {
        for (edu.montana.gsoc.msusel.rbml.model.Role role : roles) {
            switch (role) {
                case ClassRole:
                    if (type instanceof Class && !type.isAbstract()) {
                        Role r = inst.findRole(role.getName())
                        if (r) {
                            inst.addRoleBinding(RoleBinding.of(r, Reference.to(type)))
                            return true
                        }
                    }
                    break
                case InterfaceRole:
                    if (type instanceof Interface) {
                        Role r = inst.findRole(role.getName())
                        if (r) {
                            inst.addRoleBinding(RoleBinding.of(r, Reference.to(type)))
                            return true
                        }
                    }
                    break
                case Classifier:
                    Role r = inst.findRole(role.getName())
                    if (r) {
                        inst.addRoleBinding(RoleBinding.of(r, Reference.to(type)))
                        return true
                    }
                    break
            }
        }

        false
    }

    /**
     * Takes a set of pattern instances as input and compares each pair, merging those which share a common set of classes.
     * This compare and merge process continues, until either there is only one instance remaining or until no merge occurs.
     *
     * @param patterns The list of pattern instances to be compared and combined.
     */
    def compareAndCombine(List<PatternInstance> patterns) {
        if (patterns.size() <= 1)
            return

        boolean changed = true
        while (patterns.size() > 1 && changed) {
            changed = false
            for (int i = 0; i < patterns.size() - 1; i++) {
                for (int j = i + 1; j < patterns.size(); j++) {
                    if (compare(patterns.get(i), patterns.get(j))) {
                        combine(patterns.get(i), patterns.get(j))
                        PatternInstance del = patterns.get(j)
                        patterns.remove(j)
                        del.delete()
                        changed = true
                    }
                }
            }
        }
    }

    def compare(PatternInstance i, PatternInstance j) {
        List<Role> mand = i.getParentPattern().mandatoryRoles()
        Set<Type> iMandBind = [].toSet()
        Set<Type> jMandBind = [].toSet()

        mand.each {
            iMandBind.addAll(i.getTypesBoundTo(it))
            jMandBind.addAll(i.getTypesBoundTo(it))
        }

        int before = iMandBind.size()
        iMandBind.removeAll(jMandBind)
        return before > iMandBind.size()
    }

    def combine(PatternInstance i, PatternInstance j) {
        for (RoleBinding rb : j.getRoleBindings()) {
            if (!i.getRoleBindings().contains(rb)) {
                j.removeRoleBinding(rb)
                i.addRoleBinding(rb)
            }
        }
    }

    SPS loadPattern(String pattern) {
        PatternLoader.instance.loadPattern(getPatternName(pattern))
    }

    String getPatternName(String pattern) {
        return pattern.toLowerCase().replaceAll(/_/, ' ')
    }

    Pattern findPatternForName(String pattern) {
        pattern = getPatternName(pattern).capitalize()
        Pattern.findFirst("name = ?", pattern)
    }
}
