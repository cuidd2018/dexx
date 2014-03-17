/*
 * Copyright (c) 2014 Andrew O'Malley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.andrewoma.dexx.collection;

import com.github.andrewoma.dexx.collection.internal.base.AbstractSortedSet;
import com.github.andrewoma.dexx.collection.internal.builder.AbstractSelfBuilder;
import com.github.andrewoma.dexx.collection.internal.redblack.DerivedKeyFactory;
import com.github.andrewoma.dexx.collection.internal.redblack.RedBlackTree;
import com.github.andrewoma.dexx.collection.internal.redblack.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 */
public class TreeSet<E> extends AbstractSortedSet<E> {
    private final Tree<E, E> tree;
    private final RedBlackTree<E, E> redBlackTree;

    protected static final TreeSet EMPTY = new TreeSet();

    @NotNull
    public static <E> BuilderFactory<E, TreeSet<E>> factory(final Comparator<? super E> ordering) {
        return new BuilderFactory<E, TreeSet<E>>() {
            @NotNull
            @Override
            public Builder<E, TreeSet<E>> newBuilder() {
                return new AbstractSelfBuilder<E, TreeSet<E>>(new TreeSet<E>(ordering)) {
                    @NotNull
                    @Override
                    public Builder<E, TreeSet<E>> add(E element) {
                        result = result.add(element);
                        return this;
                    }
                };
            }
        };
    }

    public TreeSet() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <E> TreeSet<E> empty() {
        return EMPTY;
    }

    public TreeSet(Comparator<? super E> ordering) {
        tree = null;
        redBlackTree = new RedBlackTree<E, E>(new DerivedKeyFactory(), ordering, new IdentityKeyFunction<E>());
    }

    private TreeSet(Tree<E, E> tree, RedBlackTree<E, E> redBlackTree) {
        this.tree = tree;
        this.redBlackTree = redBlackTree;
    }

    @Override
    public Comparator<? super E> comparator() {
        return redBlackTree.getOrdering();
    }

    @NotNull
    public TreeSet<E> add(E value) {
        return new TreeSet<E>(redBlackTree.update(tree, value, value, true), redBlackTree);
    }

    @NotNull
    public TreeSet<E> remove(E value) {
        return new TreeSet<E>(redBlackTree.delete(tree, value), redBlackTree);
    }

    public boolean contains(E value) {
        return redBlackTree.contains(tree, value);
    }

    public int size() {
        return RedBlackTree.count(tree);
    }

    @Nullable
    @Override
    public E first() {
        try {
            return redBlackTree.smallest(tree).getValue();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public E last() {
        try {
            return redBlackTree.greatest(tree).getValue();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @NotNull
    public Iterator<E> iterator() {
        return redBlackTree.keysIterator(tree);
    }

    @NotNull
    @Override
    public SortedSet<E> drop(int number) {
        return new TreeSet<E>(redBlackTree.drop(tree, number), redBlackTree);
    }

    @NotNull
    @Override
    public SortedSet<E> take(int number) {
        return new TreeSet<E>(redBlackTree.take(tree, number), redBlackTree);
    }

    @NotNull
    @Override
    public SortedSet<E> from(@NotNull E value, boolean inclusive) {
        return new TreeSet<E>(redBlackTree.from(tree, value, inclusive), redBlackTree);
    }

    @NotNull
    @Override
    public SortedSet<E> to(@NotNull E value, boolean inclusive) {
        return new TreeSet<E>(redBlackTree.until(tree, value, inclusive), redBlackTree);
    }

    @NotNull
    @Override
    public SortedSet<E> range(@NotNull E from, boolean fromInclusive, @NotNull E to, boolean toInclusive) {
        return new TreeSet<E>(redBlackTree.range(tree, from, fromInclusive, to, toInclusive), redBlackTree);
    }
}